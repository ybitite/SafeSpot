package ch.y.bitite.safespot.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

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
 * ViewModel for the HomeFragment.
 * This class handles the logic for the home screen.
 */
@HiltViewModel
public class HomeViewModel extends ViewModel {

    private static final String TAG = "HomeViewModel";
    private final ReportRepository repository;
    private final MutableLiveData<List<ReportValidated>> validatedReports = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<LatLng> currentLocation = new MutableLiveData<>();
    private static final long REFRESH_INTERVAL = TimeUnit.HOURS.toMillis(2); // 2 hours
    private final CompositeDisposable disposables = new CompositeDisposable();

    /**
     * Constructor for HomeViewModel.
     *
     * @param repository The ReportRepository for data operations.
     */
    @Inject
    public HomeViewModel(ReportRepository repository) {
        this.repository = repository;

        repository.getAllValidatedReports().observeForever(reports -> {
            if (reports != null) {
                validatedReports.setValue(reports);
            }
        });

        fetchValidatedReports();

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
     * Fetches validated reports from the repository.
     */
    public void fetchValidatedReports() {
        Log.d(TAG, "fetchValidatedReports: Fetching validated reports"); // Important log: Indicates the start of the fetch operation.
        isLoading.setValue(true);
        repository.fetchValidatedReports(new ReportRepository.FetchValidatedReportsCallback() {
            @Override
            public void onSuccess(List<ReportValidated> reports) {
                Log.d(TAG, "fetchValidatedReports: Success - Received " + reports.size() + " reports"); // Important log: Indicates successful fetch.
                validatedReports.setValue(reports);
                errorMessage.setValue(null);
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.e(TAG, "fetchValidatedReports: Error fetching validated reports: " + errorMsg); // Important log: Indicates a failure in fetching.
                isLoading.setValue(false);
                errorMessage.setValue(errorMsg);
            }
        });
    }

    /**
     * Starts the periodic refresh of validated reports.
     */
    private void startPeriodicRefresh() {
        Log.d(TAG, "startPeriodicRefresh: Starting periodic refresh with interval: " + REFRESH_INTERVAL + "ms"); // Important log: Indicates the start of the periodic refresh.
        Disposable disposable = Observable.interval(REFRESH_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tick -> {
                    Log.d(TAG, "startPeriodicRefresh: Periodic refresh triggered"); // Important log: Indicates that a periodic refresh has been triggered.
                    fetchValidatedReports();
                });
        disposables.add(disposable);
    }

    /**
     * Gets the loading state LiveData.
     *
     * @return The loading state LiveData.
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Gets the error message LiveData.
     *
     * @return The error message LiveData.
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the current location LiveData.
     *
     * @return The current location LiveData.
     */
    public LiveData<LatLng> getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Sets the current location.
     *
     * @param location The new location.
     */
    public void setCurrentLocation(LatLng location) {
        currentLocation.setValue(location);
    }

    /**
     * Checks if data has been inserted.
     *
     * @return True if data has been inserted, false otherwise.
     */
    public boolean isDataInserted() {
        return repository.isDataInserted();
    }

    /**
     * Called when the ViewModel is no longer used and will be destroyed.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared: ViewModel is being cleared"); // Important log: Indicates that the ViewModel is being cleared.
        disposables.clear();
    }
}