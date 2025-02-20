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
    private static final long REFRESH_INTERVAL = TimeUnit.SECONDS.toMillis(7200); // 1000 seconds
    private final CompositeDisposable disposables = new CompositeDisposable();



    /**
     * Constructor for HomeViewModel.
     *
     * @param repository The ReportRepository for data operations.
     */
    @Inject
    public HomeViewModel(ReportRepository repository) {
        this.repository = repository;

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
        isLoading.setValue(true);
        repository.fetchValidatedReports(new ReportRepository.FetchValidatedReportsCallback() {
            @Override
            public void onSuccess(List<ReportValidated> reports) {
                validatedReports.setValue(reports);
                isLoading.setValue(false);
                errorMessage.setValue(null);
                Log.d(TAG, "fetchValidatedReports onSuccess");
            }

            @Override
            public void onFailure(String errorMsg) {
                if (repository.isFirstFetch()){
                    isLoading.setValue(false);
                    errorMessage.setValue(errorMsg);
                    Log.e(TAG, "fetchValidatedReports onFailure: " + errorMessage);
                }

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

    @Override
    protected void onCleared() {
        super.onCleared();

        disposables.clear();
    }
}