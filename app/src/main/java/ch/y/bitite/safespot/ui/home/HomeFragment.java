package ch.y.bitite.safespot.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.databinding.FragmentHomeBinding;
import ch.y.bitite.safespot.model.ReportClusterItem;
import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.ui.dashboard.DashboardViewModel;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentHomeBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;
    private DashboardViewModel dashboardViewModel;
    private Button buttonAddReport;
    private Button buttonCenterMap;
    private ClusterManager<ReportClusterItem> clusterManager;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String[]> locationPermissionRequest;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        buttonAddReport = root.findViewById(R.id.buttonAddReportHome);
        buttonAddReport.setOnClickListener(v -> {
            // Navigate to AddReportFragment
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_homeFragment_to_addReportFragment);
        });

        buttonCenterMap = root.findViewById(R.id.buttonCenterMap);
        buttonCenterMap.setOnClickListener(v -> {
            // Center the map on the user's current location
            getCurrentLocation();
        });

        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    if (Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) ||
                            Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false))) {
                        // Permission is granted. Continue the action or workflow in your app.
                        getCurrentLocation();
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied.
                    }
                }
        );

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dashboardViewModel.getValidatedReports().observe(getViewLifecycleOwner(), reports -> {
            if (googleMap != null) {
                updateMapMarkers(reports);
            }
        });
    }

    @Override public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        // Set initial zoom level and position
        LatLng initialLocation = new LatLng(46.94809, 7.44744);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 13));

        // Initialiser le ClusterManager
        clusterManager = new ClusterManager<>(requireContext(), googleMap);
        clusterManager.setRenderer(new ReportRenderer(requireContext(), googleMap, clusterManager));
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        dashboardViewModel.getValidatedReports().observe(getViewLifecycleOwner(), this::updateMapMarkers);

        // Vérifier et demander les permissions
        checkLocationPermissions();
    }

    private void updateMapMarkers(List<ReportValidated> reports) {
        clusterManager.clearItems();
        for (ReportValidated report : reports) {
            ReportClusterItem item = new ReportClusterItem(report);
            clusterManager.addItem(item);
        }
        clusterManager.cluster();
    }

    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Demander les permissions
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            // Les permissions sont déjà accordées
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}