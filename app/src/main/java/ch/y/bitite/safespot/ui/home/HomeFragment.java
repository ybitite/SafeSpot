package ch.y.bitite.safespot.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.databinding.FragmentHomeBinding;
import ch.y.bitite.safespot.utils.buttonhelper.HomeButtonHelper;
import ch.y.bitite.safespot.utils.LocationHelper;
import ch.y.bitite.safespot.utils.MapHelper;
import ch.y.bitite.safespot.viewmodel.AddReportViewModel;
import ch.y.bitite.safespot.viewmodel.HomeViewModel;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for the home screen.
 * This fragment displays a map and markers for validated reports.
 */
@AndroidEntryPoint
public class HomeFragment extends Fragment implements OnMapReadyCallback, LocationHelper.LocationCallback, HomeButtonHelper.HomeButtonCallback {

    private FragmentHomeBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;
    private LocationHelper locationHelper;
    private MapHelper mapHelper;
    private HomeButtonHelper buttonHelper;
    private ProgressBar progressBar;
    private HomeViewModel homeViewModel;
    private AddReportViewModel addReportViewModel;
    private boolean isMapReady = false; // Flag to track if the map is ready

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        buttonHelper = new HomeButtonHelper(root, this);
        buttonHelper.setupHomeButtonListeners(this);

        locationHelper = new LocationHelper(this, this);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        addReportViewModel = new ViewModelProvider(requireActivity()).get(AddReportViewModel.class);

        // Initialize the ProgressBar
        progressBar = binding.progressBar;

        // Observe the loading state
        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d("HomeFragment", "isLoading changed: " + isLoading);
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });
        // Observe the error message
        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        // Trigger an initial fetch of reports
        homeViewModel.fetchValidatedReports();

        addReportViewModel.getGlobalReportState().observe(getViewLifecycleOwner(), globalReportState -> {
            Log.d("HomeFragment", "globalReportState changed: " + globalReportState);
            switch (globalReportState) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Report added successfully", Toast.LENGTH_SHORT).show();
                    break;
                case FAILURE:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to add report", Toast.LENGTH_SHORT).show();
                    break;
                case IDLE:
                    break;
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        Log.d("HomeFragment", "onMapReady");
        googleMap = map;
        mapHelper = new MapHelper(requireContext(), googleMap);
        LatLng initialLocation = new LatLng(46.94809, 7.44744);
        mapHelper.centerMap(initialLocation);
        isMapReady = true; // Set the flag to true when the map is ready
        locationHelper.checkLocationPermissions();


        homeViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), location -> {
            if (location != null && mapHelper != null && isMapReady) {
                // Only center the map if it's ready and a new location is received
                mapHelper.centerMap(location);
            }
        });

        homeViewModel.getValidatedReports().observe(getViewLifecycleOwner(), reports -> {
            Log.d("HomeFragment", "getValidatedReports.observe");
            if (googleMap != null) {
                mapHelper.updateMapMarkers(reports);
            }
        });

    }

    @Override
    public void onLocationResult(LatLng location) {
        homeViewModel.setCurrentLocation(location);
    }

    @Override
    public void onCenterMapClicked() {
        if (isMapReady) {
            LatLng currentLocation = homeViewModel.getCurrentLocation().getValue();
            if (currentLocation != null) {
                mapHelper.centerMap(currentLocation);
            } else {
                // If the location is null, request location updates
                locationHelper.checkLocationPermissions();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("HomeFragment", "onResume");
        mapView.onResume();
    }

    @Override
    public void onPause() {
        Log.d("HomeFragment", "onPause");
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d("HomeFragment", "onDestroy");
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        Log.d("HomeFragment", "onLowMemory");
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        Log.d("HomeFragment", "onDestroyView");
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAddReportClicked() {
        Log.d("HomeFragment", "onAddReportClicked");
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_homeFragment_to_addReportFragment);
    }
}