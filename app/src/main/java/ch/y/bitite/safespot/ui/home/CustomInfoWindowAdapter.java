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

/**
 * Custom InfoWindowAdapter for displaying custom info windows on Google Map markers.
 */
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private final ImageLoader imageLoader;

    /**
     * Constructor for CustomInfoWindowAdapter.
     *
     * @param context The application context.
     */
    @SuppressLint("InflateParams")
    public CustomInfoWindowAdapter(Context context) {
        this.imageLoader = new ImageLoader(context);
        mWindow = LayoutInflater.from(context).inflate(R.layout.info_window_layout, null);
    }

    /**
     * Provides the custom info window for a marker.
     *
     * @param marker The marker for which to provide the info window.
     * @return The custom info window view.
     */
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        Log.d("CustomInfoWindowAdapter", "getInfoWindow");
        render(marker, mWindow);
        return mWindow;
    }

    /**
     * Provides the content view for the info window.
     *
     * @param marker The marker for which to provide the info window content.
     * @return Null, as we are using a custom info window.
     */
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }

    /**
     * Renders the content of the info window.
     *
     * @param marker The marker associated with the info window.
     * @param view   The view to render the content into.
     */
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
                    // Refresh the info window to display the loaded image
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