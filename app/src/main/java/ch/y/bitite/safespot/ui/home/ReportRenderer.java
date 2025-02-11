package ch.y.bitite.safespot.ui.home;

import android.content.Context;
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

    public ReportRenderer(Context context, GoogleMap map, ClusterManager<ReportClusterItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(30f);

        // Charger l'image personnalisée
        Bitmap originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.emergency_icon);

        // Définir la nouvelle taille souhaitée
        int newWidth = 80;
        int newHeight = 80;

        // Redimensionner l'image
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);

        // Créer le BitmapDescriptor à partir de l'image redimensionnée
        customMarkerIcon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull ReportClusterItem item, @NonNull MarkerOptions markerOptions) {
        markerOptions.title(item.getTitle());
        // Définir l'icône personnalisée pour les éléments individuels
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
        int circleRadius = 40;
        Bitmap bitmap = Bitmap.createBitmap(circleRadius * 2, circleRadius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCircle.setColor(color);
        canvas.drawCircle(circleRadius, circleRadius, circleRadius, paintCircle);
        String text = String.valueOf(clusterSize);
        float textX = circleRadius;
        float textY = circleRadius - (paint.descent() + paint.ascent()) / 2;
        canvas.drawText(text, textX, textY, paint);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}