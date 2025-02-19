package ch.y.bitite.safespot.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.model.ReportClusterItem;
import ch.y.bitite.safespot.utils.ImageLoader;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private final Context context;
    private final ImageLoader imageLoader;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
        this.imageLoader = new ImageLoader(context);
        mWindow = LayoutInflater.from(context).inflate(R.layout.info_window_layout, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Log.d("CustomInfoWindowAdapter", "getInfoWindow");
        render(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void render(Marker marker, View view) {
        // Get the ReportClusterItem from the Marker's tag
        ReportClusterItem reportClusterItem = (ReportClusterItem) marker.getTag();

        if (reportClusterItem != null) {
            TextView titleUi = view.findViewById(R.id.info_window_title);
            TextView descriptionUi = view.findViewById(R.id.info_window_date);
            ImageView imageUi = view.findViewById(R.id.info_window_image);
            String title = reportClusterItem.getTitle();
            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText("");
            }

            String date = reportClusterItem.getDate();
            if (date != null) {
                descriptionUi.setText(date);
            } else {
                descriptionUi.setText("");
            }
            imageLoader.loadImage(reportClusterItem.getImageFileName(), imageUi, new ImageLoader.ImageLoadCallback() {
                @Override
                public void onImageLoaded() {
                    if (marker.isInfoWindowShown()) {
                        marker.hideInfoWindow();
                        marker.showInfoWindow();
                    }
                }

                @Override
                public void onImageLoadFailed() {
                    Log.e("CustomInfoWindowAdapter", "Failed to load image");
                }
            });
        } else {
            Log.e("CustomInfoWindowAdapter", "reportValidated is null");
        }
    }
}