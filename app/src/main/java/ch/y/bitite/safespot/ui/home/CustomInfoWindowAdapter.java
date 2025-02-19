package ch.y.bitite.safespot.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.model.ReportValidated;
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
        // Corrected line: Cast to ReportValidated instead of ReportClusterItem
        ReportValidated reportValidated = (ReportValidated) marker.getTag();

        if (reportValidated != null) {
            TextView titleUi = view.findViewById(R.id.info_window_title);
            TextView descriptionUi = view.findViewById(R.id.info_window_date);
            ImageView imageUi = view.findViewById(R.id.info_window_image);

            String title = reportValidated.getDescription();
            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText("");
            }

            String description = reportValidated.getDateTimeString();
            if (description != null) {
                descriptionUi.setText(description);
            } else {
                descriptionUi.setText("");
            }
            imageLoader.loadImage(reportValidated.getImage(), imageUi, new ImageLoader.ImageLoadCallback() {
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