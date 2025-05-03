package ch.y.bitite.safespot.ui.home;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.databinding.FragmentHomeBinding;
import ch.y.bitite.safespot.model.ReportClusterItem;
import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.ui.FullScreenImageActivity;
import ch.y.bitite.safespot.utils.buttonhelper.HomeButtonHelper;
import ch.y.bitite.safespot.utils.LocationHelper;
import ch.y.bitite.safespot.viewmodel.AddReportViewModel;
import ch.y.bitite.safespot.viewmodel.HomeViewModel;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for the home screen, displaying a map with report markers.
 */
@AndroidEntryPoint
public class HomeFragment extends Fragment implements OnMapReadyCallback, LocationHelper.LocationCallback, HomeButtonHelper.HomeButtonCallback {

    private FragmentHomeBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;
    private LocationHelper locationHelper;
    private ProgressBar progressBar;
    private HomeViewModel homeViewModel;
    private boolean isMapReady = false;
    private CustomInfoWindowAdapter customInfoWindowAdapter;
    private ClusterManager<ReportClusterItem> mClusterManager;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        progressBar = binding.progressBarHome;

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

    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view.
     *
     * @param view               The View returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        AddReportViewModel addReportViewModel = new ViewModelProvider(requireActivity()).get(AddReportViewModel.class);

        progressBar = binding.progressBarHome;

        // Observe loading state of homeViewModel
        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d("homeViewModel", "isLoading changed: " + isLoading);
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe error messages from homeViewModel
        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && homeViewModel.isDataInserted()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe loading state of addReportViewModel
        addReportViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d("addReportViewModel", "isLoading changed: " + isLoading);
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe error messages from addReportViewModel
        addReportViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Called when the map is ready to be used.
     *
     * @param map The GoogleMap instance.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        Log.d("HomeFragment", "onMapReady");
        googleMap = map;

        // Initialize the ClusterManager
        mClusterManager = new ClusterManager<>(requireContext(), googleMap);

        // Set the custom renderer for the ClusterManager
        ReportClusterItemRenderer renderer = new ReportClusterItemRenderer(getContext(), googleMap, mClusterManager);
        mClusterManager.setRenderer(renderer);
        googleMap.setOnCameraIdleListener(mClusterManager);

        // Apply custom map style
        try {
            // Load the JSON file from the raw folder
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            requireContext(), R.raw.style_google_map));

            if (!success) {
                Log.e("MapFragment", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapFragment", "Can't find style. Error: ", e);
        }
        // Set the custom InfoWindowAdapter to the ClusterManager
        mClusterManager.getMarkerCollection().setInfoWindowAdapter(customInfoWindowAdapter);

        // Set listeners for info window clicks
        mClusterManager.setOnClusterItemInfoWindowClickListener(marker -> {
            if (marker != null) {
                String imageUrl = marker.getImageFileName();

                // Get the context from the fragment or activity
                Context context = getContext();

                if (context != null) {
                    Intent intent = new Intent(context, FullScreenImageActivity.class);
                    intent.putExtra(FullScreenImageActivity.EXTRA_IMAGE_URL, imageUrl);
                    context.startActivity(intent);
                } else {
                    Log.e("MapFragment", "Context is null, cannot start FullScreenImageActivity");
                }
            } else {
                Log.e("MapFragment", "Marker is not an instance of MyClusterItem");
            }
        });

        // Set initial map location and check for location permissions
        LatLng initialLocation = new LatLng(46.94809, 7.44744);
        centerMap(initialLocation);
        locationHelper.checkLocationPermissions();

        // Update map markers if reports are available
        List<ReportValidated> reports = homeViewModel.getValidatedReports().getValue();
        if (reports != null) {
            updateMapMarkers(reports);
        }

        // Observe current location and center the map if it changes
        homeViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), location -> {
            if (location != null && isMapReady) {
                centerMap(location);
            }
        });

        // Observe validated reports and update map markers if they change
        homeViewModel.getValidatedReports().observe(getViewLifecycleOwner(), reports2 -> {
            if (isMapReady) {
                updateMapMarkers(reports2);
            }
        });
        isMapReady = true;
    }

    /**
     * Updates the map markers with the given list of reports.
     *
     * @param reports The list of ReportValidated objects to display on the map.
     */
    private void updateMapMarkers(List<ReportValidated> reports) {
        mClusterManager.clearItems();
        for (ReportValidated report : reports) {
            // Use ReportClusterItem to wrap the ReportValidated object
            mClusterManager.addItem(new ReportClusterItem(report));
        }
        mClusterManager.cluster();
    }

    /**
     * Centers the map on the given location.
     *
     * @param location The location to center the map on.
     */
    private void centerMap(LatLng location) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13.5F));
    }

    /**
     * Called when a new location is available.
     *
     * @param location The new location.
     */
    @Override
    public void onLocationResult(LatLng location) {
        homeViewModel.setCurrentLocation(location);
    }

    /**
     * Called when the center map button is clicked.
     */
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

    /**
     * Called when the fragment is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d("HomeFragment", "onResume");
        mapView.onResume();
    }

    /**
     * Called when the fragment is paused.
     */
    @Override
    public void onPause() {
        Log.d("HomeFragment", "onPause");
        super.onPause();
        mapView.onPause();
    }

    /**
     * Called when the fragment is destroyed.
     */
    @Override
    public void onDestroy() {
        Log.d("HomeFragment", "onDestroy");
        super.onDestroy();
        // mapView.onDestroy(); // Commented out as it might cause issues
    }

    /**
     * Called when the system is running low on memory.
     */
    @Override
    public void onLowMemory() {
        Log.d("HomeFragment", "onLowMemory");
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * Called when the fragment's view is destroyed.
     */
    @Override
    public void onDestroyView() {
        Log.d("HomeFragment", "onDestroyView");
        super.onDestroyView();
        binding = null;
    }

    /**
     * Called when the add report button is clicked.
     */
    @Override
    public void onAddReportClicked() {
        Log.d("HomeFragment", "onAddReportClicked");
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_homeFragment_to_addReportFragment);
    }

    /**
     * Called when the refresh button is clicked.
     */
    @Override
    public void onRefreshClicked() {
        if (homeViewModel.getValidatedReports().getValue() != null) {
            homeViewModel.fetchValidatedReports();
        }
    }
}