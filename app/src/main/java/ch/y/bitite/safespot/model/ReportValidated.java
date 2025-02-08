package ch.y.bitite.safespot.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.Date;

@Entity(tableName = "validated_reports")
public class ReportValidated {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public double longitude;
    public double latitude;
    public String description;
    public String image;
    public String video;
    public String comment;

    public Date date_time; //  compatible avec toutes les versions Android
    public Date date_time_validation; // Idem
}