package ch.y.bitite.safespot.ui.dashboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.repository.Report;
import ch.y.bitite.safespot.repository.ReportRepository;
public class AddReportFragment extends Fragment {

    private EditText editTextDescription;
    private TextView textViewLatitude;
    private TextView textViewLongitude;
    private Button buttonAddReport;
    private Button buttonCancel;
    private DashboardViewModel dashboardViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude;
    private double longitude;
    private static final String TAG = "AddReportFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_report, container, false);

        editTextDescription = view.findViewById(R.id.editTextDescription);
        textViewLatitude = view.findViewById(R.id.textViewLatitude);
        textViewLongitude = view.findViewById(R.id.textViewLongitude);buttonAddReport = view.findViewById(R.id.buttonAddReport);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Start the location process
        startLocationUpdates();

        buttonAddReport.setOnClickListener(v -> {
            addReport();
        });

        buttonCancel.setOnClickListener(v -> {
            // Navigate back to DashboardFragment
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    private void startLocationUpdates() {
        // Check for permissions and request if needed
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        } else {
            // Permissions are granted, get the location
            getLocation();
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
                Toast.makeText(getContext(), "Failedto add report: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocation() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.d(TAG, "Latitude: " + latitude + " Longitude: " + longitude);
                                // Update the TextViews here, after the location is available
                                updateLocationTextViews();
                            } else {
                                // Location is null, handle this case (e.g., display a message)
                                textViewLatitude.setText("Location Unavailable");
                                textViewLongitude.setText("Location Unavailable");
                                Log.w(TAG, "Location is null");
                            }
                        }
                    });
        } catch (SecurityException e) {
            // Handle the SecurityException (e.g., permission revoked)
            textViewLatitude.setText("Permission Revoked");
            textViewLongitude.setText("Permission Revoked");
            Log.e(TAG, "SecurityException: Location permission revoked", e);
            Toast.makeText(requireContext(), "Location permission revoked", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLocationTextViews() {
        textViewLatitude.setText(String.valueOf(latitude));
        textViewLongitude.setText(String.valueOf(longitude));
    }

    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                if (permissions.get(Manifest.permission.ACCESS_FINE_LOCATION) != null && Boolean.TRUE.equals(permissions.get(Manifest.permission.ACCESS_FINE_LOCATION))) {
                    // Permission granted, get the location
                    getLocation();
                } else {
                    // Permission denied, handle this case (e.g., display a message)
                    textViewLatitude.setText("Permission Denied");
                    textViewLongitude.setText("Permission Denied");
                    Log.w(TAG, "Location permission denied");
                    Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();}
            });
}