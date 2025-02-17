package ch.y.bitite.safespot.utils;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import ch.y.bitite.safespot.model.ReportClusterItem;
import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.ui.home.ReportRenderer;

public class MapHelper {

    private GoogleMap googleMap;
    private ClusterManager<ReportClusterItem> clusterManager;
    private Context context;

    public MapHelper(Context context, GoogleMap map) {
        this.context = context;
        this.googleMap = map;
        clusterManager = new ClusterManager<>(context, googleMap);
        clusterManager.setRenderer(new ReportRenderer(context, googleMap, clusterManager));
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
    }


    public void updateMapMarkers(List<ReportValidated> reports) {
        if (reports != null) {
            clusterManager.clearItems(); // Clear the clusterManager
            for (ReportValidated report : reports) {
                ReportClusterItem item = new ReportClusterItem(report);
                clusterManager.addItem(item);
            }
            clusterManager.cluster();
        }
    }

    public void centerMap(LatLng location) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
    }
}