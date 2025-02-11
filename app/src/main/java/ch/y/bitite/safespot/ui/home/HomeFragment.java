package ch.y.bitite.safespot.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import ch.y.bitite.safespot.databinding.FragmentHomeBinding;
import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.ui.dashboard.DashboardViewModel;
import ch.y.bitite.safespot.utils.ButtonHelper;
import ch.y.bitite.safespot.utils.LocationHelper;
import ch.y.bitite.safespot.utils.MapHelper;

public class HomeFragment extends Fragment implements OnMapReadyCallback, LocationHelper.LocationCallback, ButtonHelper.ButtonCallback {

    private FragmentHomeBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;
    private LocationHelper locationHelper;
    private MapHelper mapHelper;
    private ButtonHelper buttonHelper;
    private HomeViewModel homeViewModel;
    private DashboardViewModel dashboardViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        locationHelper = new LocationHelper(this, this);
        buttonHelper = new ButtonHelper(this, root, this);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), location -> {
            if (location != null && mapHelper != null) {
                mapHelper.centerMap(location);
            }
        });
        // Trigger an initial fetch of reports
        dashboardViewModel.refreshValidatedReports();

        dashboardViewModel.getValidatedReports().observe(getViewLifecycleOwner(), reports -> {
            homeViewModel.setValidatedReports(reports);
        });

        homeViewModel.getValidatedReports().observe(getViewLifecycleOwner(), reports -> {
            if (googleMap != null) {
                mapHelper.updateMapMarkers(reports);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        mapHelper = new MapHelper(requireContext(), googleMap);
        LatLng initialLocation = new LatLng(46.94809, 7.44744);
        mapHelper.initializeMap(initialLocation);
        locationHelper.checkLocationPermissions();
    }

    @Override
    public void onLocationResult(LatLng location) {
        homeViewModel.setCurrentLocation(location);
    }

    @Override
    public void onCenterMapClicked() {
        locationHelper.checkLocationPermissions();
    }

    @Override
    public void onResume(){
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