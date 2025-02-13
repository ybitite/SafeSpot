package ch.y.bitite.safespot.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "reports_validated")
public class ReportValidated {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double longitude;
    public double latitude;
    public Date date_Time; // Changed to date_Time
    public String description;
    public String image;
    public String video;
    public String comment;
    public Date date_Time_Validation; // Changed to date_Time_Validation

    public ReportValidated() {
    }
}