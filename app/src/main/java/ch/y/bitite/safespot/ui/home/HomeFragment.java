package ch.y.bitite.safespot.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.databinding.FragmentHomeBinding;
import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.utils.buttonhelper.HomeButtonHelper;
import ch.y.bitite.safespot.utils.LocationHelper;
import ch.y.bitite.safespot.viewmodel.AddReportViewModel;
import ch.y.bitite.safespot.viewmodel.HomeViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements OnMapReadyCallback, LocationHelper.LocationCallback, HomeButtonHelper.HomeButtonCallback {

    private FragmentHomeBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;
    private LocationHelper locationHelper;
    private HomeButtonHelper buttonHelper;
    private ProgressBar progressBar;
    private HomeViewModel homeViewModel;
    private AddReportViewModel addReportViewModel;
    private boolean isMapReady = false;
    private CustomInfoWindowAdapter customInfoWindowAdapter;

    private static final int MARKER_SIZE = 200;
    private  BitmapDescriptor customMarkerIcon;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        progressBar = binding.progressBar;

        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d("HomeFragment", "isLoading changed: " + isLoading);
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });

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

        // Initialize the CustomInfoWindowAdapter
        customInfoWindowAdapter = new CustomInfoWindowAdapter(requireContext());

        // Set the info window adapter for the GoogleMap
        googleMap.setInfoWindowAdapter(customInfoWindowAdapter);

        LatLng initialLocation = new LatLng(46.94809, 7.44744);
        centerMap(initialLocation);
        isMapReady = true;
        locationHelper.checkLocationPermissions();

        List<ReportValidated> reports = homeViewModel.getValidatedReports().getValue();
        if (reports != null) {
            updateMapMarkers(reports);
        }

        homeViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), location -> {
            if (location != null && isMapReady) {
                centerMap(location);
            }
        });

        homeViewModel.getValidatedReports().observe(getViewLifecycleOwner(), reports2 -> {
            if (isMapReady) {
                updateMapMarkers(reports2);
            }
        });
    }

    private void updateMapMarkers(List<ReportValidated> reports) {
        Log.d("HomeFragment", "updateMapMarkers: Updating map markers");
        googleMap.clear();
        for (ReportValidated report : reports) {

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(report.getLatitude(), report.getLongitude()));
            Marker marker = googleMap.addMarker(markerOptions);
            assert marker != null;
            marker.setTag(report);

            setMarkerIcon(marker);

        }
    }

    private void setMarkerIcon(Marker marker) {
        // Load the custom icon for individual markers.
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.emergency_icon);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, MARKER_SIZE, MARKER_SIZE, false);
        customMarkerIcon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
        // We set the icon here to make sure it is properly set
        marker.setIcon(customMarkerIcon);
    }

    private void centerMap(LatLng location) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
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
                centerMap(currentLocation);
            } else {
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