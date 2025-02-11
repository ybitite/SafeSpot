package ch.y.bitite.safespot.ui.dashboard;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.model.Report;
import ch.y.bitite.safespot.repository.ReportRepository;


public class DashboardViewModel extends AndroidViewModel {
    private final ReportRepository repository;
    private final LiveData<List<ReportValidated>> validatedReports;
    private final Handler handler =new Handler(Looper.getMainLooper());
    private static final long REFRESH_INTERVAL = TimeUnit.SECONDS.toMillis(1000); // 300 seconds
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            refreshValidatedReports();
            handler.postDelayed(this, REFRESH_INTERVAL);
        }
    };

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        repository = new ReportRepository(application);
        validatedReports = repository.getAllValidatedReports();
        // Fetch reports immediately
        refreshValidatedReports();
        // Start the periodic refresh
        startPeriodicRefresh();
    }

    public LiveData<List<ReportValidated>> getValidatedReports() {
        return validatedReports;
    }

    public void refreshValidatedReports() {
        repository.fetchValidatedReports();
    }

    private void startPeriodicRefresh() {
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove callbacks to prevent memoryleaks
        handler.removeCallbacks(refreshRunnable);
    }


}