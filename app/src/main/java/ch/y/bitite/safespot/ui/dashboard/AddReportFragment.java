package ch.y.bitite.safespot.ui.dashboard;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.model.LatLng;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.repository.ReportRepository;
import ch.y.bitite.safespot.utils.ButtonHelper;
import ch.y.bitite.safespot.utils.LocationHelper;

public class AddReportFragment extends Fragment implements LocationHelper.LocationCallback, ButtonHelper.ButtonCallback {

    private EditText editTextDescription;
    private TextView textViewLatitude;
    private TextView textViewLongitude;
    private AddReportViewModel addReportViewModel;
    private LocationHelper locationHelper;
    private ButtonHelper buttonHelper;
    private static final String TAG = "AddReportFragment";
    private boolean isLocationRequested = false;
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Lock the orientation to portrait when the fragment is created
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View view = inflater.inflate(R.layout.fragment_add_report, container, false);

        editTextDescription = view.findViewById(R.id.editTextDescription);
        textViewLatitude = view.findViewById(R.id.textViewLatitude);
        textViewLongitude = view.findViewById(R.id.textViewLongitude);addReportViewModel = new ViewModelProvider(this).get(AddReportViewModel.class);
        locationHelper = new LocationHelper(this, this);
        buttonHelper = new ButtonHelper(this, view, this);

        if (savedInstanceState != null) {
            editTextDescription.setText(savedInstanceState.getString(KEY_DESCRIPTION));
            textViewLatitude.setText(savedInstanceState.getString(KEY_LATITUDE));
            textViewLongitude.setText(savedInstanceState.getString(KEY_LONGITUDE));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLocationRequested) {
            // Request a fresh, accurate location
            locationHelper.checkLocationPermissions();
            isLocationRequested = true;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_DESCRIPTION, editTextDescription.getText().toString());
        outState.putString(KEY_LATITUDE, textViewLatitude.getText().toString());
        outState.putString(KEY_LONGITUDE, textViewLongitude.getText().toString());
    }

    @Override
    public void onLocationResult(LatLng location) {
        if (location != null) {
            addReportViewModel.setLocation(location);
            Log.d(TAG, "Latitude: " + location.latitude + " Longitude: " + location.longitude);
            updateLocationTextViews(location);
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
        addReportViewModel.setDescription(description);
        addReportViewModel.addReport(new ReportRepository.AddReportCallback() {
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

    private void updateLocationTextViews(LatLng location) {
        textViewLatitude.setText(String.valueOf(location.latitude));
        textViewLongitude.setText(String.valueOf(location.longitude));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationHelper.stopLocationUpdates();
        // Reset the orientation to sensor-based when the fragment is destroyed
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}