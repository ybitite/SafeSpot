package ch.y.bitite.safespot.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ReportClusterItem implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    private final float zIndex;

    public ReportClusterItem(ReportValidated report) {
        this.position = new LatLng(report.getLatitude(), report.getLongitude());
        this.title = report.getDescription();
        this.snippet = report.getDateTimeString();
        this.zIndex = 0.0f;
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
}