package ch.y.bitite.safespot.repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.y.bitite.safespot.model.Report;
import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.model.room.AppDatabase;
import ch.y.bitite.safespot.model.room.ReportDao;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportRepository {
    private final ApiService apiService;
    private final ReportDao reportDao;
    private final ExecutorService executorService;
    private final Application application;

    public ReportRepository(Application application) {
        this.application = application;
        AppDatabase db = AppDatabase.getDatabase(application);
        reportDao = db.reportDao();
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<ReportValidated>> getAllValidatedReports() {
        return reportDao.getAllValidatedReports();
    }

    public void fetchValidatedReports() {
        apiService.getValidatedReports().enqueue(new Callback<List<ReportValidated>>() {
            @Override
            public void onResponse(@NonNull Call<List<ReportValidated>> call, @NonNull Response<List<ReportValidated>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executorService.execute(() -> {
                        reportDao.deleteAllValidatedReports();
                        reportDao.insertValidatedReports(response.body());
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReportValidated>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Erreur de chargement des rapports validés", t);
            }
        });
    }

    public void addReport(Report report, Uri imageUri, AddReportCallback callback) {
        File file = imageUri != null ? getFileFromUri(imageUri) : null;
        RequestBody requestFile = file != null ? RequestBody.create(MediaType.parse("multipart/form-data"), file) : null;
        MultipartBody.Part imagePart = requestFile != null ? MultipartBody.Part.createFormData("image", file.getName(), requestFile) : null;

        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), report.description != null ? report.description : "");
        RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(report.longitude));
        RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(report.latitude));
        RequestBody dateTime = RequestBody.create(MediaType.parse("text/plain"), report.date_time != null ? report.date_time : "");

        Call<Void> call = apiService.addReport(imagePart, description, longitude, latitude, dateTime);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API_SUCCESS", "Rapport ajouté avec succès !");
                    callback.onSuccess();
                } else {
                    Log.e("API_ERROR", "Erreur lors de l'ajout du rapport: " + response.code());
                    callback.onFailure("Erreur lors de l'ajout du rapport: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Erreur lors de l'ajout du rapport", t);
                callback.onFailure("Erreur lors de l'ajout du rapport: " + t.getMessage());
            }
        });
    }

    private File getFileFromUri(Uri uri) {
        File file = null;
        InputStream inputStream = null;
        try {
            // Récupérer le nom du fichier et l'extension
            String fileName = getFileName(uri);
            if (fileName == null) {
                fileName = "temp_image";
            }
            file = new File(application.getCacheDir(), fileName);
            inputStream = application.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            if (inputStream != null) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                outputStream.close();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = application.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public interface AddReportCallback {
        void onSuccess();

        void onFailure(String errorMessage);
    }
}