package ch.y.bitite.safespot.model;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Represents a report item that can be displayed as a cluster item on a map.
 * This class implements the {@link ClusterItem} interface from the Google Maps Android API
 * utility library, allowing it to be used with a {@link com.google.maps.android.clustering.ClusterManager}.
 * It encapsulates the data needed to represent a report as a marker on the map, including its
 * location, title, snippet, z-index, date, and associated image file name.
 */
public class ReportClusterItem implements ClusterItem {
    /**
     * The geographical location of the report.
     */
    private final LatLng position;
    /**
     * The title of the report, typically a short description.
     */
    private final String title;
    /**
     * A short summary or additional information about the report.
     */
    private final String snippet;
    /**
     * The z-index of the report marker, used for layering on the map.
     */
    private final float zIndex;
    /**
     * The date associated with the report.
     */
    private final String date;
    /**
     * The file name of the image associated with the report.
     */
    private final String imageFileName;


    /**
     * Constructs a new ReportClusterItem from a ReportValidated object.
     *
     * @param report The ReportValidated object containing the data for this cluster item.
     */
    public ReportClusterItem(ReportValidated report) {
        this.position = new LatLng(report.getLatitude(), report.getLongitude());
        this.title = report.getDescription();
        this.snippet = report.getDateTimeString();
        this.zIndex = 0.0f;
        this.date = report.getDateTimeString();
        this.imageFileName = report.getImage();
    }

    /**
     * Returns the geographical location of this cluster item.
     *
     * @return The LatLng representing the position of the report.
     */
    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    /**
     * Returns the title of this cluster item.
     *
     * @return The title of the report.
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Returns the snippet of this cluster item.
     *
     * @return The snippet of the report.
     */
    @Override
    public String getSnippet() {
        return snippet;
    }

    /**
     * Returns the z-index of this cluster item.
     *
     * @return The z-index of the report marker.
     */
    @Override
    public Float getZIndex() {
        return zIndex;
    }

    /**
     * Returns the date associated with this report.
     *
     * @return The date of the report.
     */
    public String getDate() {
        return date;
    }


    /**
     * Returns the file name of the image associated with this report.
     *
     * @return The image file name.
     */
    public String getImageFileName() {
        return imageFileName;
    }
}