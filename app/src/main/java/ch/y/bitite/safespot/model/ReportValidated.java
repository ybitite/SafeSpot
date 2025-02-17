package ch.y.bitite.safespot.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.time.Instant;
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
    private String dateTimeString;
    private String description;
    private String image;
    private String video;
    private String comment;
    @SerializedName("date_Time_Validation")
    private String dateTimeValidationString;
    @Ignore
    private Instant dateTime;
    @Ignore
    private Instant dateTimeValidation;

    public ReportValidated(double longitude, double latitude, String dateTimeString, String description, String image, String video, String comment, String dateTimeValidationString) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.dateTimeString = dateTimeString;
        this.description = description;
        this.image = image;
        this.video = video;
        this.comment = comment;
        this.dateTimeValidationString = dateTimeValidationString;
        this.dateTime = parseDateTime(dateTimeString);
        this.dateTimeValidation = parseDateTime(dateTimeValidationString);
    }
    @Ignore
    private Instant parseDateTime(String dateTimeString) {
        if (dateTimeString == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withLocale(Locale.getDefault()).withZone(ZoneId.of("UTC"));
            return ZonedDateTime.parse(dateTimeString, formatter).toInstant();
        } catch (DateTimeParseException e) {
            return null;
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

    public void setVideo(String video) {
        this.video = video;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDateTimeString() {
        return dateTimeString;
    }

    public String getDateTimeValidationString() {
        return dateTimeValidationString;
    }
    @Ignore
    public Instant getDateTime() {
        return dateTime;
    }
    @Ignore
    public Instant getDateTimeValidation() {
        return dateTimeValidation;
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