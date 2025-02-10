package ch.y.bitite.safespot.model;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Report {

    @SerializedName("Longitude")
    public double longitude;

    @SerializedName("Latitude")
    public double latitude;

    @SerializedName("Date_Time")
    public String date_time;

    @SerializedName("Description")
    public String description;

    @SerializedName("Image")
    public String image;

    @SerializedName("Video")
    public String video;

    public Report() {
    }

    @Override
    public String toString() {
        return "Report{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", date_time='" + date_time + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", video='" + video + '\'' +
                '}';
    }

    public void setDate_time(Date date_time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        this.date_time = dateFormat.format(date_time);
    }
}