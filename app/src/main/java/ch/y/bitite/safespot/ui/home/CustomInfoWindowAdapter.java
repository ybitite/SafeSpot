package ch.y.bitite.safespot.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.model.ReportClusterItem;
import ch.y.bitite.safespot.utils.ImageLoader;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private final ImageLoader imageLoader;

    @SuppressLint("InflateParams")
    public CustomInfoWindowAdapter(Context context) {
        this.imageLoader = new ImageLoader(context);
        mWindow = LayoutInflater.from(context).inflate(R.layout.info_window_layout, null);
    }

    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        Log.d("CustomInfoWindowAdapter", "getInfoWindow");
        marker.setInfoWindowAnchor(0.5f, 5.0f);
        render(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }

    private void render(Marker marker, View view) {
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
                String formattedDate = date.replace("T", " ");
                descriptionUi.setText(formattedDate);
            } else {
                descriptionUi.setText("");
            }

            imageLoader.loadImage(reportClusterItem.getImageFileName(), imageUi, new ImageLoader.ImageLoadCallback() {
                @Override
                public void onImageLoaded() {
                    // Notify the info window that the content has changed


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