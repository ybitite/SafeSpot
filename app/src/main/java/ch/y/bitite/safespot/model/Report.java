package ch.y.bitite.safespot.model;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Report {

    @SerializedName("Longitude")
    private final double longitude;

    @SerializedName("Latitude")
    private final double latitude;

    @SerializedName("Date_Time")
    private final String dateTimeUtc;

    @SerializedName("Description")
    private final String description;

    @SerializedName("Image")
    private final String image;

    @SerializedName("Video")
    private final String video;

    public Report(double longitude, double latitude, Date dateTime, String description, String image, String video) {
        this.longitude = longitude;
        this.latitude = latitude;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Explicitly set UTC
        this.dateTimeUtc = dateFormat.format(dateTime);
        this.description = description;
        this.image = image;
        this.video = video;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getDateTimeUtc() {
        return dateTimeUtc;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getVideo() {
        return video;
    }

    @Override
    public String toString() {
        return "Report{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", dateTimeUtc='" + dateTimeUtc + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", video='" + video + '\'' +
                '}';
    }
}