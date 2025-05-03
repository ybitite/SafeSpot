package ch.y.bitite.safespot.viewmodel;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.Date;

import javax.inject.Inject;

import ch.y.bitite.safespot.model.Report;
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
    // New variable for the date and time
    private final MutableLiveData<Date> dateTime = new MutableLiveData<>();
    // New variable for the audio file path
    private final MutableLiveData<String> audioFilePath = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

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
     * Gets the audio file path LiveData.
     *
     * @return The audio file path LiveData.
     */
    public LiveData<String> getAudioFilePath() {
        return audioFilePath;
    }

    /**
     * Sets the audio file path.
     *
     * @param path The audio file path.
     */
    public void setAudioFilePath(String path) {
        audioFilePath.setValue(path);
    }

    /**
     * Gets the date and time LiveData.
     *
     * @return The date and time LiveData.
     */
    public LiveData<Date> getDateTime() {
        return dateTime;
    }

    /**
     * Sets the date and time.
     *
     * @param newDateTime The new date and time.
     */
    public void setDateTime(Date newDateTime) {
        dateTime.setValue(newDateTime);
    }

    /**
     * Adds a new report.
     */
    public void addReport() {
        isLoading.setValue(true);

        String reportDescription = description.getValue();
        double reportLongitude = 0.0;
        double reportLatitude = 0.0;
        if (location.getValue() != null) {
            reportLongitude = location.getValue().longitude;
            reportLatitude = location.getValue().latitude;
        }
        // Get the date and time from the LiveData
        Date reportDateTime = dateTime.getValue() != null ? dateTime.getValue() : new Date();
        String reportImage = imageUri.getValue() != null ? imageUri.getValue().toString() : "";
        // Get the audio file path
        String reportAudio = audioFilePath.getValue() != null ? audioFilePath.getValue() : "";
        File audioFile = audioFilePath.getValue() != null ? new File(audioFilePath.getValue()) : null;

        Report report = new Report(reportLongitude, reportLatitude, reportDateTime, reportDescription, reportImage, reportAudio);

        repository.addReport(report, imageUri.getValue(), audioFile, new ReportRepository.AddReportCallback() {
            @Override
            public void onSuccess(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
                Log.d("AddReportViewModel", "addReport onSuccess");
            }

            @Override
            public void onFailure(String errorMsg) {
                isLoading.setValue(false);
                errorMessage.setValue(errorMsg);
                Log.d("AddReportViewModel", "addReport onFailure");
            }
        });
    }
}