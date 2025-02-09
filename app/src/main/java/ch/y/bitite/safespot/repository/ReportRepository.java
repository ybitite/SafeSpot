package ch.y.bitite.safespot.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.model.room.AppDatabase;
import ch.y.bitite.safespot.model.room.ReportDao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ReportRepository {
    private final ApiService apiService;
    private final ReportDao reportDao;
    private final ExecutorService executorService;

    public ReportRepository(Application application) {
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

    public void addReport(Report report, AddReportCallback callback) {
        apiService.addReport(report).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API_SUCCESS", "Rapport ajouté avec succès !");
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } else {
                    Log.e("API_ERROR", "Erreur lors de l'ajout du rapport: " + response.code());
                    if (callback != null) {
                        callback.onFailure("Erreur lors de l'ajout du rapport: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Erreur lors de l'ajout du rapport", t);
                if (callback != null) {
                    callback.onFailure("Erreur lors de l'ajout du rapport: " + t.getMessage());
                }
            }
        });
    }

    public interface AddReportCallback {
        void onSuccess();

        void onFailure(String errorMessage);
    }
}