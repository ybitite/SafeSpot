package ch.y.bitite.safespot.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ReportClusterItem implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    private final float zIndex;
    private final String date;
    private final String imageFileName;


    public ReportClusterItem(ReportValidated report) {
        this.position = new LatLng(report.getLatitude(), report.getLongitude());
        this.title = report.getDescription();
        this.snippet = report.getDateTimeString();
        this.zIndex = 0.0f;
        this.date = report.getDateTimeString();
        this.imageFileName = report.getImage();
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    @Override
    public Float getZIndex() {
        return zIndex;
    }

    public String getDate() {
        return date;
    }

    public int getImage() {
        return 0;
    }

    public String getImageFileName() {
        return imageFileName;
    }
}