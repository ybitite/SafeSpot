package ch.y.bitite.safespot.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.model.ReportClusterItem;

public class ReportClusterItemRenderer extends DefaultClusterRenderer<ReportClusterItem> {

    private static final int MARKER_SIZE = 200;
    private final BitmapDescriptor customMarkerIcon;

    public ReportClusterItemRenderer(Context context, GoogleMap map, ClusterManager<ReportClusterItem> clusterManager) {
        super(context, map, clusterManager);
        // Load the custom icon for individual markers.
        Bitmap originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.emergency_icon);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, MARKER_SIZE, MARKER_SIZE, false);
        customMarkerIcon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

    @Override
    protected void onBeforeClusterItemRendered(ReportClusterItem item, MarkerOptions markerOptions) {
        // Customize the marker here
        markerOptions.icon(customMarkerIcon);
    }

    @Override
    protected void onClusterItemRendered(ReportClusterItem clusterItem, Marker marker) {
        // Set the tag here
        marker.setTag(clusterItem);
    }
}