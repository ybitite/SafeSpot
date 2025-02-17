package ch.y.bitite.safespot.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import javax.inject.Inject;

import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.repository.ReportRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private static final String TAG = "HomeViewModel";
    private final ReportRepository repository;
    private final MutableLiveData<List<ReportValidated>> validatedReports = new MutableLiveData<>();
    private final MutableLiveData<LatLng> currentLocation = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Inject
    public HomeViewModel(ReportRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<ReportValidated>> getValidatedReports() {
        return validatedReports;
    }

    public LiveData<LatLng> getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng location) {
        currentLocation.setValue(location);
    }

    // Expose the loading state
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    // Expose the error message
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchValidatedReports() {
        // Set loading to true when starting to fetch
        isLoading.setValue(true);
        repository.fetchValidatedReports(new ReportRepository.FetchValidatedReportsCallback() {
            @Override
            public void onSuccess(List<ReportValidated> reports) {
                validatedReports.setValue(reports);
                // Set loading to false when done
                isLoading.setValue(false);
                // Clear any previous error message
                errorMessage.setValue(null);
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.e(TAG, "Error fetching validated reports: " + errorMsg);
                // Set loading to false even on failure
                isLoading.setValue(false);
                // Set the error message
                errorMessage.setValue(errorMsg);
            }
        });
    }
}