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

    @Query("SELECT * FROM reports_validated ORDER BY id DESC")
    LiveData<List<ReportValidated>> getAllValidatedReports();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertValidatedReports(List<ReportValidated> reports);

    @Query("DELETE FROM reports_validated")
    void deleteAllValidatedReports();
}
