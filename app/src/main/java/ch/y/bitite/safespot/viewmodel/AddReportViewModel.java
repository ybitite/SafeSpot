package ch.y.bitite.safespot.viewmodel;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import javax.inject.Inject;

import ch.y.bitite.safespot.model.Report;
import ch.y.bitite.safespot.repository.ReportRemoteDataSource;
import ch.y.bitite.safespot.repository.ReportRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for the AddReportFragment.
 * This class handles the logic for adding a new report.
 */
@HiltViewModel
public class AddReportViewModel extends ViewModel {

    private final ReportRepository repository;
    private final MutableLiveData<String> description = new MutableLiveData<>();
    private final MutableLiveData<LatLng> location = new MutableLiveData<>();
    private final MutableLiveData<Uri> imageUri = new MutableLiveData<>();
    private final MutableLiveData<ReportState> reportState = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    // Add this line
    private final MutableLiveData<GlobalReportState> globalReportState = new MutableLiveData<>(GlobalReportState.IDLE);
    /**
     * Constructor for AddReportViewModel.
     *
     * @param repository The ReportRepository for data operations.
     */
    @Inject
    public AddReportViewModel(ReportRepository repository) {
        this.repository = repository;
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
     * Gets the description LiveData.
     *
     * @return The description LiveData.
     */
    public LiveData<String> getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param newDescription The new description.
     */
    public void setDescription(String newDescription) {
        description.setValue(newDescription);
    }

    /**
     * Gets the location LiveData.
     *
     * @return The location LiveData.
     */
    public LiveData<LatLng> getLocation() {
        return location;
    }

    /**
     * Sets the location.
     *
     * @param newLocation The new location.
     */
    public void setLocation(LatLng newLocation) {
        location.setValue(newLocation);
    }

    /**
     * Sets the image URI.
     *
     * @param uri The image URI.
     */
    public void setImageUri(Uri uri) {
        imageUri.setValue(uri);
    }

    /**
     * Gets the image URI LiveData.
     *
     * @return The image URI LiveData.
     */
    public LiveData<Uri> getImageUri() {
        return imageUri;
    }

    /**
     * Gets the report state LiveData.
     *
     * @return The report state LiveData.
     */
    public LiveData<ReportState> getReportState() {
        return reportState;
    }

    /**
     * Adds a new report.
     */

    public void addReport() {
        isLoading.setValue(true);

        globalReportState.setValue(GlobalReportState.LOADING);
        String reportDescription = description.getValue();
        double reportLongitude = 0.0;
        double reportLatitude = 0.0;
        if (location.getValue() != null) {
            reportLongitude = location.getValue().longitude;
            reportLatitude = location.getValue().latitude;
        }
        Date reportDateTime = new Date();
        String reportImage = imageUri.getValue() != null ? imageUri.getValue().toString() : "";
        String reportVideo = "1";

        Report report = new Report(reportLongitude, reportLatitude, reportDateTime, reportDescription, reportImage, reportVideo);

        repository.addReport(report, imageUri.getValue(), new ReportRemoteDataSource.AddReportCallback() {
            @Override
            public void onSuccess() {

                isLoading.setValue(false);
                errorMessage.setValue(null);
                Log.d("AddReportViewModel", "addReport onSuccess");

            }

            @Override
            public void onFailure(String errorMsg) {

                isLoading.setValue(false);
                errorMessage.setValue(errorMsg);
                Log.d("AddReportViewModel", "addReport onSuccess");

            }
        });

    }

    /**
     * Gets the global report state LiveData.
     *
     * @return The global report state LiveData.
     */
    public LiveData<GlobalReportState> getGlobalReportState() {
        return globalReportState;
    }
    /**
     * Reset the global report state to IDLE.
     */
    public void resetGlobalReportState() {
        globalReportState.setValue(GlobalReportState.IDLE);
    }

    /**
     * Enum for the report state.
     */
    public enum ReportState {
        LOADING,
        SUCCESS,
        FAILURE
    }
    /**
     * Enum for the global report state.
     */
    public enum GlobalReportState {
        IDLE,
        LOADING,
        SUCCESS,
        FAILURE
    }
}