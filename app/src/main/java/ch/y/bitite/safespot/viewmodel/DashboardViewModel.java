package ch.y.bitite.safespot.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.repository.ReportRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * ViewModel for the DashboardFragment.
 * This class handles the logic for displaying validated reports.
 */
@HiltViewModel
public class DashboardViewModel extends ViewModel {
    private static final String TAG = "DashboardViewModel";
    private final ReportRepository repository;
    private final MutableLiveData<List<ReportValidated>> validatedReports = new MutableLiveData<>();
    private final MediatorLiveData<List<ReportValidated>> filteredReports = new MediatorLiveData<>();
    private static final long REFRESH_INTERVAL = TimeUnit.HOURS.toMillis(2); // 2 hours
    private final CompositeDisposable disposables = new CompositeDisposable();
    private boolean isSortedByDate = false; // Ajout de la variable pour suivre l'état du tri
    private boolean isAscending = false; // Ajout de la variable pour suivre l'ordre du tri
    private String currentSearchQuery = "";

    /**
     * Constructor for DashboardViewModel.
     *
     * @param repository The ReportRepository for data operations.
     */
    @Inject
    public DashboardViewModel(ReportRepository repository) {
        this.repository = repository;

        // Observe changes in the validated reports from the repository
        repository.getAllValidatedReports().observeForever(reports -> {
            if (reports != null) {
                validatedReports.setValue(reports);
                filterReports(currentSearchQuery);
            }
        });
        filteredReports.addSource(validatedReports, reports -> filterReports(currentSearchQuery));

        // Initial fetch of validated reports
        fetchValidatedReports();
        // Start the periodic refresh
        startPeriodicRefresh();
    }

    /**
     * Gets the validated reports LiveData.
     *
     * @return The validated reports LiveData.
     */
    public LiveData<List<ReportValidated>> getValidatedReports() {
        return filteredReports;
    }


    /**
     * Refreshes the validated reports from the API.
     */
    public void fetchValidatedReports() {
        Log.d(TAG, "fetchValidatedReports: Fetching validated reports");
        repository.fetchValidatedReports(new ReportRepository.FetchValidatedReportsCallback() {
            @Override
            public void onSuccess(List<ReportValidated> reports) {
                Log.d(TAG, "fetchValidatedReports: Success - Received " + reports.size() + " reports");
                if (isSortedByDate) {
                    sortReportsByDate();
                } else {
                    validatedReports.setValue(reports);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "fetchValidatedReports: Error fetching validated reports: " + errorMessage);
            }
        });
    }

    /**
     * Starts the periodic refresh of validated reports.
     */
    private void startPeriodicRefresh() {
        Log.d(TAG, "startPeriodicRefresh: Starting periodic refresh with interval: " + REFRESH_INTERVAL + "ms");
        Disposable disposable = Observable.interval(REFRESH_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tick -> {
                    Log.d(TAG, "startPeriodicRefresh: Periodic refresh triggered");
                    fetchValidatedReports();
                });
        disposables.add(disposable);
    }

    /**
     * Called when the ViewModel is no longer used and will be destroyed.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared: ViewModel is being cleared");
        // Remove callbacks to prevent memory leaks
        disposables.clear();
    }

    /**
     * Sorts the reports by date (most recent first).
     */
    public void sortReportsByDate() {
        List<ReportValidated> reports = validatedReports.getValue();
        if (reports != null) {
            if (isAscending) {
                // Sort the reports by date in ascending order (oldest first)
                reports.sort(Comparator.comparing(ReportValidated::getDateTimeString));
            } else {
                // Sort the reports by date in descending order (most recent first)
                reports.sort((r1, r2) -> r2.getDateTimeString().compareTo(r1.getDateTimeString()));
            }
            validatedReports.setValue(reports);
            filterReports(currentSearchQuery);
            isAscending = !isAscending; // Toggle the sorting order
            isSortedByDate = true;
        } else {
            isSortedByDate = false;
        }
    }

    public void filterReports(String query) {
        currentSearchQuery = query;
        List<ReportValidated> allReports = validatedReports.getValue();
        List<ReportValidated> filteredList = new ArrayList<>();

        if (allReports != null) {
            for (ReportValidated report : allReports) {
                if (report.getDescription() != null) {
                    if (report.getDescription().toLowerCase().contains(query.toLowerCase())) {
                        filteredList.add(report);
                    }
                }

            }
        }
        filteredReports.setValue(filteredList);
    }
}