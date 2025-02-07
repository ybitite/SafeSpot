package ch.y.bitite.safespot.repository;

import android.app.Application;
import android.util.Log;

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
        apiService.getValidatedReports().enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executorService.execute(() -> {
                        reportDao.deleteAllValidatedReports();
                        reportDao.insertValidatedReports(response.body().validatedReports);
                    });
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                Log.e("API_ERROR", "Erreur de chargement des rapports validés", t);
            }
        });
    }

    public void addReport(ReportValidated reportValidated) {
        apiService.addReport(reportValidated).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API_SUCCESS", "Rapport ajouté avec succès !");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "Erreur lors de l'ajout du rapport", t);
            }
        });
    }
}
