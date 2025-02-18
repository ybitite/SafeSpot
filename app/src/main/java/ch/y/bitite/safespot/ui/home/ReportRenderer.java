package ch.y.bitite.safespot.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.model.ReportClusterItem;

/**
 * This class manages the custom display of clusters and markers on the map.
 */
public class ReportRenderer extends DefaultClusterRenderer<ReportClusterItem> {
    private static final String TAG = "ReportRenderer";

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final BitmapDescriptor customMarkerIcon;
    private final Paint haloPaint;
    private final Context context;

    /**
     * Constructor for ReportRenderer.
     *
     * @param context        The application context.
     * @param map            The GoogleMap object.
     * @param clusterManager The associated ClusterManager.
     */
    public ReportRenderer(Context context, GoogleMap map, ClusterManager<ReportClusterItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;

        // Initialize the paint for the text in the clusters.
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(30f);

        // Initialize the paint for the halo around the clusters.
        haloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        haloPaint.setStyle(Paint.Style.FILL);

        // Load the custom icon for individual markers.
        Bitmap originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.emergency_icon);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 80, 80, false);
        customMarkerIcon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

    /**
     * Called before rendering an individual item (not clustered).
     *
     * @param item          The item to render.
     * @param markerOptions The marker options.
     */
    @Override
    protected void onBeforeClusterItemRendered(@NonNull ReportClusterItem item, @NonNull MarkerOptions markerOptions) {
        Log.d(TAG, "onBeforeClusterItemRendered: Rendering a single item");
        markerOptions.title(item.getTitle());
        markerOptions.snippet(item.getSnippet());
        // We no longer set the icon here because we do it in onClusterItemRendered
    }

    /**
     * Called after rendering an individual item (not clustered).
     *
     * @param clusterItem The rendered item.
     * @param marker      The rendered marker.
     */
    @Override
    protected void onClusterItemRendered(@NonNull ReportClusterItem clusterItem, @NonNull Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        // We set the icon here to make sure it is properly set
        marker.setIcon(customMarkerIcon);
    }

    /**
     * Called before rendering a cluster.
     *
     * @param cluster       The cluster to render.
     * @param markerOptions The marker options.
     */
    @Override
    protected void onBeforeClusterRendered(@NonNull Cluster<ReportClusterItem> cluster, @NonNull MarkerOptions markerOptions) {
        Log.d(TAG, "onBeforeClusterRendered: Rendering a cluster");
        int clusterSize = cluster.getSize();
        int color = getClusterColor(clusterSize);
        markerOptions.icon(createClusterIcon(clusterSize, color));
    }

    /**
     * Called to determine if a cluster should be rendered or not.
     *
     * @param cluster The cluster to check.
     * @return True if the cluster should be rendered, false otherwise.
     */
    @Override
    protected boolean shouldRenderAsCluster(@NonNull Cluster<ReportClusterItem> cluster) {
        // We display a cluster if there is more than one element
        return cluster.getSize() > 1;
    }

    /**
     * Creates the custom icon for a cluster.
     *
     * @param clusterSize The size of the cluster.
     * @param color       The color of the cluster.
     * @return The custom icon (BitmapDescriptor).
     */
    private BitmapDescriptor createClusterIcon(int clusterSize, int color) {
        Log.d(TAG, "createClusterIcon: Creating cluster icon for size: " + clusterSize);
        int radius = 50;
        int outlineRadius = radius * 2;
        int size = outlineRadius * 2;

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the transparent halo.
        haloPaint.setColor(color);
        haloPaint.setAlpha(80);
        canvas.drawCircle(size / 2, size / 2, outlineRadius, haloPaint);

        // Draw the inner circle.
        Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCircle.setColor(color);
        canvas.drawCircle(size / 2, size / 2, radius, paintCircle);

        // Draw the text (cluster size).
        String text = String.valueOf(clusterSize);
        float textX = size / 2;
        float textY = size / 2 - (paint.descent() + paint.ascent()) / 2;
        canvas.drawText(text, textX, textY, paint);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Determines the color of the cluster based on its size.
     *
     * @param clusterSize The size of the cluster.
     * @return The color of the cluster.
     */
    private int getClusterColor(int clusterSize) {
        if (clusterSize <= 5) {
            return ContextCompat.getColor(context, R.color.green);
        } else if (clusterSize <= 10) {
            return ContextCompat.getColor(context, R.color.yellow);
        } else {
            return ContextCompat.getColor(context, R.color.red);
        }
    }
}