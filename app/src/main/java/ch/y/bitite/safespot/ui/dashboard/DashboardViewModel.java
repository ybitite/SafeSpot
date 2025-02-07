package ch.y.bitite.safespot.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.repository.ReportRepository;

public class DashboardViewModel extends AndroidViewModel {
    private final ReportRepository repository;
    private final LiveData<List<ReportValidated>> validatedReports;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        repository = new ReportRepository(application);
        validatedReports = repository.getAllValidatedReports();
    }

    public LiveData<List<ReportValidated>> getValidatedReports() {
        return validatedReports;
    }

    public void refreshValidatedReports() {
        repository.fetchValidatedReports();
    }
}