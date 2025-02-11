package ch.y.bitite.safespot.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ReportClusterItem implements ClusterItem {
    private final ReportValidated report;
    private final LatLng position;
    private final String title;

    public ReportClusterItem(ReportValidated report) {
        this.report = report;
        this.position = new LatLng(report.latitude, report.longitude);
        this.title = report.description;
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
        return null;
    }

    @Override
    public Float getZIndex() {
        return 0.0f;
    }
}