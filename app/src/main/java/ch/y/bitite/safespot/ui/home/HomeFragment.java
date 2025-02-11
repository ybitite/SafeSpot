package ch.y.bitite.safespot.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

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
    private ClusterManager<ReportClusterItem> clusterManager;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);mapView.getMapAsync(this);

        buttonAddReport = root.findViewById(R.id.buttonAddReportHome);
        buttonAddReport.setOnClickListener(v -> {
            // Navigate to AddReportFragment
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_homeFragment_to_addReportFragment);
        });

        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

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

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
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
    }

    private void updateMapMarkers(List<ReportValidated> reports) {
        clusterManager.clearItems();
        for (ReportValidated report : reports) {
            ReportClusterItem item = new ReportClusterItem(report);
            clusterManager.addItem(item);
        }
        clusterManager.cluster();
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