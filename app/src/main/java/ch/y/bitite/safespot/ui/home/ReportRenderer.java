package ch.y.bitite.safespot.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.model.ReportClusterItem;

public class ReportRenderer extends DefaultClusterRenderer<ReportClusterItem> {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private BitmapDescriptor customMarkerIcon;
    private Context context;
    private final Paint haloPaint; // Paint for the halo

    public ReportRenderer(Context context, GoogleMap map, ClusterManager<ReportClusterItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(30f);

        // Initialize the halo paint
        haloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        haloPaint.setStyle(Paint.Style.FILL); // Set the style tofill for a filled shape

        // Load the custom marker icon
        Bitmap originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.emergency_icon);
        int newWidth = 80;
        int newHeight = 80;
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);
        customMarkerIcon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull ReportClusterItem item, @NonNull MarkerOptions markerOptions) {
        markerOptions.title(item.getTitle());
        markerOptions.icon(customMarkerIcon);
    }

    @Override
    protected void onBeforeClusterRendered(@NonNull Cluster<ReportClusterItem> cluster, @NonNull MarkerOptions markerOptions) {
        int clusterSize = cluster.getSize();
        int color;
        if (clusterSize <= 5) {
            color = ContextCompat.getColor(context, R.color.green);
        } else if (clusterSize <= 10) {
            color = ContextCompat.getColor(context, R.color.yellow);
        } else {
            color = ContextCompat.getColor(context, R.color.red);
        }
        BitmapDescriptor icon = createClusterIcon(clusterSize, color);
        markerOptions.icon(icon);
    }

    @Override
    protected boolean shouldRenderAsCluster(@NonNull Cluster<ReportClusterItem> cluster) {
        return cluster.getSize() > 1;
    }

    private BitmapDescriptor createClusterIcon(int clusterSize, int color) {
        int circleRadius = 50;
        int outlineRadius = circleRadius * 2; // Double the radius for the outline
        int bitmapSize = outlineRadius * 2; // Size of the bitmap to accommodate the outline

        Bitmap bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the transparent halo
        haloPaint.setColor(color);
        haloPaint.setAlpha(80); // Set the transparency (0-255, 80 is about 30% transparent)
        canvas.drawCircle(bitmapSize / 2,bitmapSize / 2, outlineRadius, haloPaint);

        // Draw the inner circle
        Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCircle.setColor(color);
        canvas.drawCircle(bitmapSize / 2, bitmapSize / 2, circleRadius, paintCircle);

        // Draw the text
        String text = String.valueOf(clusterSize);
        float textX = bitmapSize / 2;
        float textY = bitmapSize / 2 - (paint.descent() + paint.ascent()) / 2;
        canvas.drawText(text, textX, textY, paint);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}