package ch.y.bitite.safespot.utils;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.model.ReportClusterItem;
import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.ui.home.CustomInfoWindowAdapter;
import ch.y.bitite.safespot.ui.home.ReportRenderer;

/**
 * This class is a helper class for managing the Google Map, including:
 * - Adding markers to the map.
 * - Clustering markers for better visualization.
 * - Customizing the appearance of clusters and individual markers.
 * - Handling camera movements and cluster updates.
 * - Handling marker and cluster clicks.
 */
public class MapHelper {
    private static final String TAG = "MapHelper";

    private final GoogleMap googleMap;
    private final ClusterManager<ReportClusterItem> clusterManager;
    private final Context context;

    /**
     * Constructor for the MapHelper class.
     *
     * @param context The application context.
     * @param map     The GoogleMap object to manage.
     */
    public MapHelper(Context context, GoogleMap map) {
        this.googleMap = map;
        this.context = context;

        // Initialize the ClusterManager, which handles the clustering of markers.
        clusterManager = new ClusterManager<>(context, googleMap);

        // Set the custom renderer for clusters and markers.
        // This allows us to define how clusters and individual markers look.
        clusterManager.setRenderer(new ReportRenderer(context, googleMap, clusterManager));

        // Set the ClusterManager as the listener for camera idle events.
        // This means the ClusterManager will be notified when the camera stops moving.
        // It will then handle the necessary cluster updates.
        googleMap.setOnCameraIdleListener(clusterManager);

        // Set a listener for cluster clicks.
        // When a cluster is clicked, we log the event and return false.
        // Returning false allows the default behavior (zooming in on the cluster) to occur.
        clusterManager.setOnClusterClickListener(cluster -> {
            return false; // Allow the default behavior (zoom on the cluster).
        });

        // Set the ClusterManager as the listener for marker clicks.
        // This means the ClusterManager will be notified when a marker is clicked.
        googleMap.setOnMarkerClickListener(clusterManager);


    }

    /**
     * Updates the markers on the map with a new list of reports.
     *
     * @param reports The list of validated reports to display on the map.
     */
    public void updateMapMarkers(List<ReportValidated> reports) {
        Log.d(TAG, "updateMapMarkers: Updating map markers");

        // Add each report to the ClusterManager as a ReportClusterItem.
        for (ReportValidated report : reports) {
            clusterManager.addItem(new ReportClusterItem(report));
        }

        // Tell the ClusterManager to recalculate and redraw the clusters.
        clusterManager.cluster();
        // Set the custom info window adapter.
        // This allows us to customize the content and appearance of the info window.
        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(context));
    }

    /**
     * Centers the map on a given location with a specific zoom level.
     *
     * @param location The LatLng location to center the map on.
     */
    public void centerMap(LatLng location) {
        // Animate the camera to the specified location with a zoom level of 14.
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
    }
}