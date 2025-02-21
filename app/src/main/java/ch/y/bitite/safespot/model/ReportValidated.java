package ch.y.bitite.safespot.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;

@Entity(tableName = "reports_validated")
public class ReportValidated {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private double longitude;
    private double latitude;
    @SerializedName("date_Time")
    private final String dateTimeString;
    private String description;
    private String image;
    private final String video;
    private final String comment;
    @SerializedName("date_Time_Validation")
    private final String dateTimeValidationString;

    public ReportValidated(double longitude, double latitude, String dateTimeString, String description, String image, String video, String comment, String dateTimeValidationString) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.dateTimeString = dateTimeString;
        this.description = description;
        this.image = image;
        this.video = video;
        this.comment = comment;
        this.dateTimeValidationString = dateTimeValidationString;
        parseDateTime(dateTimeString);
        parseDateTime(dateTimeValidationString);
    }
    @Ignore
    private void parseDateTime(String dateTimeString) {
        if (dateTimeString == null) {
            return;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withLocale(Locale.getDefault()).withZone(ZoneId.of("UTC"));
            ZonedDateTime.parse(dateTimeString, formatter).toInstant();
        } catch (DateTimeParseException ignored) {
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public String getComment() {
        return comment;
    }

    public String getDateTimeString() {
        return dateTimeString;
    }

    public String getDateTimeValidationString() {
        return dateTimeValidationString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportValidated that = (ReportValidated) o;
        return id == that.id && Double.compare(that.longitude, longitude) == 0 && Double.compare(that.latitude, latitude) == 0 && Objects.equals(description, that.description) && Objects.equals(dateTimeString, that.dateTimeString) && Objects.equals(image, that.image) && Objects.equals(video, that.video);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, longitude, latitude, description, dateTimeString, image, video);
    }
}