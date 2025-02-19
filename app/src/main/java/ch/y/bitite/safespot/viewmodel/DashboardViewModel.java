package ch.y.bitite.safespot.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
    private static final long REFRESH_INTERVAL = TimeUnit.SECONDS.toMillis(1000); // 1000 seconds
    private final CompositeDisposable disposables = new CompositeDisposable();

    /**
     * Constructor for DashboardViewModel.
     *
     * @param repository The ReportRepository for data operations.
     */
    @Inject
    public DashboardViewModel(ReportRepository repository) {
        this.repository = repository;
        //validatedReports = repository.getAllValidatedReports();
        // Fetch reports immediately
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
        return validatedReports;
    }


    /**
     * Refreshes the validated reports from the API.
     */
    public void fetchValidatedReports() {

        repository.fetchValidatedReports(new ReportRepository.FetchValidatedReportsCallback() {
            @Override
            public void onSuccess(List<ReportValidated> reports) {
                validatedReports.setValue(reports);

            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Error fetching validated reports: " + errorMessage);
            }
        });
    }

    /**
     * Starts the periodic refresh of validated reports.
     */
    private void startPeriodicRefresh() {
        Disposable disposable = Observable.interval(REFRESH_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tick -> fetchValidatedReports());
        disposables.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove callbacks to prevent memory leaks
        disposables.clear();
    }
}