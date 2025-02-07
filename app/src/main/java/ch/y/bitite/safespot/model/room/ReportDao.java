package ch.y.bitite.safespot.model.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ch.y.bitite.safespot.model.ReportValidated;

@Dao
public interface ReportDao {
    @Query("SELECT * FROM validated_reports")
    LiveData<List<ReportValidated>> getAllValidatedReports();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertValidatedReports(List<ReportValidated> reports);

    @Query("DELETE FROM validated_reports")
    void deleteAllValidatedReports();
}
