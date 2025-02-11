package ch.y.bitite.safespot.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.model.Report;
import ch.y.bitite.safespot.repository.ReportRepository;
import ch.y.bitite.safespot.utils.ButtonHelper;
import ch.y.bitite.safespot.utils.LocationHelper;

public class AddReportFragment extends Fragment implements LocationHelper.LocationCallback, ButtonHelper.ButtonCallback {

    private EditText editTextDescription;
    private TextView textViewLatitude;
    private TextView textViewLongitude;
    private DashboardViewModel dashboardViewModel;
    private LocationHelper locationHelper;
    private ButtonHelper buttonHelper;
    private double latitude;
    private double longitude;
    private static final String TAG = "AddReportFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_report, container, false);

        editTextDescription = view.findViewById(R.id.editTextDescription);
        textViewLatitude = view.findViewById(R.id.textViewLatitude);
        textViewLongitude = view.findViewById(R.id.textViewLongitude);

        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);locationHelper = new LocationHelper(this, this);
        buttonHelper = new ButtonHelper(this, view, this);

        // Request a fresh, accurate location
        locationHelper.checkLocationPermissions();

        return view;
    }

    @Override
    public void onLocationResult(LatLng location) {
        if (location != null) {
            latitude = location.latitude;
            longitude = location.longitude;
            Log.d(TAG, "Latitude: " + latitude + " Longitude: " + longitude);
            updateLocationTextViews();
        } else {
            // Location is null, handle this case (e.g., display a message)
            textViewLatitude.setText("Location Unavailable");
            textViewLongitude.setText("Location Unavailable");
            Log.w(TAG, "Location is null");
        }
    }

    private void addReport() {
        String description = editTextDescription.getText().toString();

        if (description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill the description field", Toast.LENGTH_SHORT).show();
            return;
        }

        Report report = new Report();
        report.description = description;
        report.longitude = longitude;
        report.latitude = latitude;
        report.setDate_time(new Date());
        report.image = "q";
        report.video = "1";

        Log.d(TAG, "Report to send: " + report.toString());
        dashboardViewModel.addReport(report, new ReportRepository.AddReportCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Report added successfully", Toast.LENGTH_SHORT).show();
                // Navigate back to DashboardFragment
                getParentFragmentManager().popBackStack();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Failed to add report: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLocationTextViews() {
        textViewLatitude.setText(String.valueOf(latitude));
        textViewLongitude.setText(String.valueOf(longitude));
    }

    @Override
    public void onCenterMapClicked() {
        // Not used in this fragment
    }

    @Override
    public void onAddReportClicked() {
        addReport();
    }

    @Override
    public void onCancelClicked() {
        getParentFragmentManager().popBackStack();
    }
}