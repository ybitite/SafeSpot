package ch.y.bitite.safespot.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import ch.y.bitite.safespot.model.Report;
import ch.y.bitite.safespot.repository.ReportRepository;

public class AddReportViewModel extends AndroidViewModel {

    private final ReportRepository repository;
    private final MutableLiveData<String> description = new MutableLiveData<>();
    private final MutableLiveData<LatLng> location = new MutableLiveData<>();
    private final MutableLiveData<Uri> imageUri = new MutableLiveData<>();

    public AddReportViewModel(@NonNull Application application) {
        super(application);
        repository = new ReportRepository(application);
    }

    public LiveData<String> getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        description.setValue(newDescription);
    }

    public LiveData<LatLng> getLocation() {
        return location;
    }

    public void setLocation(LatLng newLocation) {
        location.setValue(newLocation);
    }

    public void setImageUri(Uri uri) {
        imageUri.setValue(uri);
    }

    public LiveData<Uri> getImageUri() {
        return imageUri;
    }

    public void addReport(ReportRepository.AddReportCallback callback) {
        Report report = new Report();
        report.description = description.getValue();
        if (location.getValue() != null) {
            report.longitude = location.getValue().longitude;
            report.latitude = location.getValue().latitude;
        }
        report.setDate_time(new Date());
        report.image = imageUri.getValue() != null ? imageUri.getValue().toString() : "";
        report.video = "1";

        repository.addReport(report, imageUri.getValue(), callback);
    }
}