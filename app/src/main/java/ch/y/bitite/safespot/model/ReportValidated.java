package ch.y.bitite.safespot.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.Date;

@Entity(tableName = "reports_validated")
public class ReportValidated {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public double longitude;
    public double latitude;
    public String date_time;
    public String description;
    public String image;
    public String video;
    public String comment;
    public String date_time_validation;

    public ReportValidated() {
    }
}