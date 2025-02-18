package ch.y.bitite.safespot.utils;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import ch.y.bitite.safespot.model.ReportClusterItem;
import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.ui.home.ReportRenderer;

/**
 * This class manages the display of markers and clusters on the Google Maps.
 */
public class MapHelper {
    private static final String TAG = "MapHelper";

    private final GoogleMap googleMap;
    private final ClusterManager<ReportClusterItem> clusterManager;
    private List<ReportValidated> currentReports;

    /**
     * Constructor for MapHelper.
     *
     * @param context The application context.
     * @param map     The GoogleMap object.
     */
    public MapHelper(Context context, GoogleMap map) {
        this.googleMap = map;

        // Initialize the ClusterManager to handle marker clusters.
        clusterManager = new ClusterManager<>(context, googleMap);

        // Set the ReportRenderer to customize the display of clusters and markers.
        clusterManager.setRenderer(new ReportRenderer(context, googleMap, clusterManager));

        // Configure the listener for camera movement.
        // When the camera is idle, we recalculate the clusters.
        googleMap.setOnCameraIdleListener(this::reCluster);

        // Configure the listener for clicking on a marker.
        googleMap.setOnMarkerClickListener(clusterManager);

        // Configure the listener for clicking on a cluster.
        // We return false to allow the default behavior (zoom on the cluster).
        clusterManager.setOnClusterClickListener(cluster -> {
            Log.d(TAG, "onClusterClick: A cluster was clicked");
            return false;
        });
    }

    /**
     * Updates the markers on the map with a new list of reports.
     *
     * @param reports The new list of validated reports.
     */
    public void updateMapMarkers(List<ReportValidated> reports) {
        Log.d(TAG, "updateMapMarkers: Updating map markers");
        currentReports = reports;
        reCluster();
    }

    /**
     * Recalculates the clusters and updates the markers on the map.
     * This method is called when the camera is idle or when the list of reports is updated.
     */
    private void reCluster() {
        if (currentReports != null) {
            // Clear all items from the ClusterManager.
            // This forces the creation of new Marker objects.
            clusterManager.clearItems();

            // Add all reports to the ClusterManager.
            for (ReportValidated report : currentReports) {
                clusterManager.addItem(new ReportClusterItem(report));
            }

            // Launch the cluster calculation.
            clusterManager.cluster();
        }
    }

    /**
     * Centers the map on a given location with a specific zoom level.
     *
     * @param location The location (LatLng) on which to center the map.
     */
    public void centerMap(LatLng location) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
    }
}