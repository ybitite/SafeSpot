package ch.y.bitite.safespot.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.model.ReportClusterItem;
import ch.y.bitite.safespot.utils.ImageLoader;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final ImageLoader imageLoader;
    private View infoWindowView;
    private final Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
        this.imageLoader = new ImageLoader(context);
        this.infoWindowView = LayoutInflater.from(context).inflate(R.layout.info_window_layout, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // Get the ReportClusterItem associated with the marker
        ReportClusterItem reportClusterItem = (ReportClusterItem) marker.getTag();

        // Find the views in the layout
        ImageView imageView = infoWindowView.findViewById(R.id.info_window_image);
        TextView titleTextView = infoWindowView.findViewById(R.id.info_window_title);
        TextView snippetTextView = infoWindowView.findViewById(R.id.info_window_snippet);
        TextView dateTextView = infoWindowView.findViewById(R.id.info_window_date);

        // Set the data to the views
        if (reportClusterItem != null) {
            titleTextView.setText(reportClusterItem.getTitle());
            snippetTextView.setText(reportClusterItem.getSnippet());
            dateTextView.setText(reportClusterItem.getDate());
            // Load the image from the URL
            imageLoader.loadImage(reportClusterItem.getImageFileName(), imageView);

        }

        return infoWindowView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null; // We don't need this anymore because we use getInfoWindow()
    }
}