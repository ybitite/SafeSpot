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
    private ProgressBar progressBar;
    private HomeViewModel homeViewModel;
    private AddReportViewModel addReportViewModel;
    private boolean isMapReady = false;
    private CustomInfoWindowAdapter customInfoWindowAdapter;



    private ClusterManager<ReportClusterItem> mClusterManager;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        progressBar = binding.progressBar;


        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        HomeButtonHelper buttonHelper = new HomeButtonHelper(root, this);
        buttonHelper.setupHomeButtonListeners();

        locationHelper = new LocationHelper(this, this);
        // Initialize the customInfoWindowAdapter here
        customInfoWindowAdapter = new CustomInfoWindowAdapter(getContext());



        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        addReportViewModel = new ViewModelProvider(requireActivity()).get(AddReportViewModel.class);

        progressBar = binding.progressBar;

        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d("homeViewModel", "isLoading changed: " + isLoading);
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        homeViewModel.fetchValidatedReports();

        addReportViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d("addReportViewModel", "isLoading changed: " + isLoading);
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        addReportViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        Log.d("HomeFragment", "onMapReady");
        googleMap = map;

        mClusterManager = new ClusterManager<>(getContext(), googleMap);

        // Set the custom renderer
        ReportClusterItemRenderer renderer = new ReportClusterItemRenderer(getContext(), googleMap, mClusterManager);
        mClusterManager.setRenderer(renderer);
        googleMap.setOnCameraIdleListener(mClusterManager);

        // Set the custom InfoWindowAdapter to the ClusterManager
        mClusterManager.getMarkerCollection().setInfoWindowAdapter(customInfoWindowAdapter);

        mClusterManager.setOnClusterItemInfoWindowClickListener(marker ->
                Toast.makeText(getContext(),
                        "Info window clicked.",
                        Toast.LENGTH_SHORT).show());
        mClusterManager.setOnClusterItemInfoWindowLongClickListener(marker ->
                Toast.makeText(getContext(),
                        "Info window long pressed.",
                        Toast.LENGTH_SHORT).show());


        LatLng initialLocation = new LatLng(46.94809, 7.44744);
        centerMap(initialLocation);
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
        isMapReady = true;
    }

    private void updateMapMarkers(List<ReportValidated> reports) {

        mClusterManager.clearItems();
        for (ReportValidated report : reports) {
            // Use ReportClusterItem to wrap the ReportValidated object
            mClusterManager.addItem(new ReportClusterItem(report));
        }
        mClusterManager.cluster();
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
        Log.d("HomeFragment","onLowMemory");
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