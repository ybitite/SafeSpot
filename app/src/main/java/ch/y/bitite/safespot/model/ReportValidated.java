package ch.y.bitite.safespot.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity(tableName = "validated_reports")
public class ReportValidated {

    @PrimaryKey
    public int id;

    public double longitude;
    public double latitude ;
    public LocalDateTime date_time ;
    public String description ;
    public String image ;
    public String video ;
    public String comment ;
    public LocalDateTime date_time_validation ;
}
