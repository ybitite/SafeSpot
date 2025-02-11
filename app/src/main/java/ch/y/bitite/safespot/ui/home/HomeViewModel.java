package ch.y.bitite.safespot.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import ch.y.bitite.safespot.model.ReportValidated;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<LatLng> currentLocation = new MutableLiveData<>();
    private final MutableLiveData<List<ReportValidated>> validatedReports = new MutableLiveData<>();

    public LiveData<LatLng> getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng location) {
        currentLocation.setValue(location);
    }

    public LiveData<List<ReportValidated>> getValidatedReports() {
        return validatedReports;
    }

    public void setValidatedReports(List<ReportValidated> reports) {
        validatedReports.setValue(reports);
    }
}