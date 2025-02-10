package ch.y.bitite.safespot.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.databinding.FragmentHomeBinding;
import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.ui.dashboard.DashboardViewModel;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentHomeBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;
    private DashboardViewModel dashboardViewModel;
    private Button buttonAddReport;


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
        dashboardViewModel.getValidatedReports().observe(getViewLifecycleOwner(), this::updateMapMarkers);
    }

    private void updateMapMarkers(List<ReportValidated> reports) {
        googleMap.clear(); // Clear existing markers
        for (ReportValidated report : reports) {
            LatLng location = new LatLng(report.latitude, report.longitude);
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(report.description));
        }
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