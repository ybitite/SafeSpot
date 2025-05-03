package ch.y.bitite.safespot.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Represents a report containing location, timestamp, description, and media information.
 * This class is designed to be used for storing and transferring report data,
 * particularly in the context of a safety or incident reporting application.
 */
public class Report {

    /**
     * The longitude of the location where the report was made.
     * This field is serialized to JSON with the key "Longitude".
     */
    @SerializedName("Longitude")
    private final double longitude;

    /**
     * The latitude of the location where the report was made.
     * This field is serialized to JSON with the key "Latitude".
     */
    @SerializedName("Latitude")
    private final double latitude;

    /**
     * The date and time when the report was made, formatted as a UTC timestamp.
     * The format is "yyyy-MM-dd'T'HH:mm:ss'Z'".
     * This field is serialized to JSON with the key "Date_Time".
     */
    @SerializedName("Date_Time")
    private final String dateTimeUtc;

    /**
     * A textual description of the report.
     * This field is serialized to JSON with the key "Description".
     */
    @SerializedName("Description")
    private final String description;

    /**
     * The URL or path to an image associated with the report.
     * This field is serialized to JSON with the key "Image".
     */
    @SerializedName("Image")
    private final String image;

    /**
     * The URL or path to an audio associated with the report.
     * This field is serialized to JSON with the key "Audio".
     */
    @SerializedName("Audio")
    private final String audio;

    /**
     * Constructs a new Report object.
     *
     * @param longitude   The longitude of the report location.
     * @param latitude    The latitude of the report location.
     * @param dateTime    The date and time when the report was made.
     * @param description A description of the report.
     * @param image       The URL or path to an image associated with the report.
     * @param audio       The URL or path to an audio associated with the report.
     */
    public Report(double longitude, double latitude, Date dateTime, String description, String image, String audio) {
        this.longitude = longitude;
        this.latitude = latitude;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Explicitly set UTC
        this.dateTimeUtc = dateFormat.format(dateTime);
        this.description = description;
        this.image = image;
        this.audio = audio;
    }

    /**
     * Gets the longitude of the report location.
     *
     * @return The longitude.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Gets the latitude of the report location.
     *
     * @return The latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the UTC timestamp of the report.
     *
     * @return The UTC timestamp as a string.
     */
    public String getDateTimeUtc() {
        return dateTimeUtc;
    }

    /**
     * Gets the description of the report.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the URL or path to the image associated with the report.
     *
     * @return The image URL or path.
     */
    public String getImage() {
        return image;
    }

    /**
     * Gets the URL or path to the audio associated with the report.
     *
     * @return The audio URL or path.
     */
    public String getAudio() {
        return audio;
    }

    /**
     * Returns a string representation of the Report object.
     * This method is overridden to provide a human-readable format of the report's data.
     *
     * @return A string representation of the Report.
     */
    @NonNull
    @Override
    public String toString() {
        return "Report{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", dateTimeUtc='" + dateTimeUtc + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", audio='" + audio + '\'' +
                '}';
    }
}