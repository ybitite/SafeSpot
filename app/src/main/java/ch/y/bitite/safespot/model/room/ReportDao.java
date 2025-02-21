package ch.y.bitite.safespot.model.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.y.bitite.safespot.model.ReportValidated;

/**
 * Data Access Object (DAO) for the ReportValidated entity.
 * This interface provides methods to interact with the 'reports_validated' table in the database.
 */
@Dao
public interface ReportDao {
    /**
     * Retrieves all validated reports from the database.
     *
     * @return A list of ReportValidated objects.
     */
    @Query("SELECT * FROM reports_validated ORDER BY id DESC")
    List<ReportValidated> getListReports();

    /**
     * Retrieves all validated reports from the database.
     *
     * @return A LiveData of ReportValidated objects.
     */
    @Query("SELECT * FROM reports_validated ORDER BY id DESC")
    LiveData<List<ReportValidated>> getAllValidatedReports();

    /**
     * Inserts a list of validated reports into the database.
     * If a report with the same ID already exists, it will be ignored.
     *
     * @param reports The list of ReportValidated objects to insert.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertValidatedReports(List<ReportValidated> reports);

    /**
     * Update a list of validated reports in the database.
     *
     * @param reports The list of ReportValidated objects to update.
     */
    @Update
    void updateValidatedReports(List<ReportValidated> reports);

    /**
     * Delete a list of validated reports in the database.
     *
     * @param reports The list of ReportValidated objects to delete.
     */
    @Delete
    void deleteValidatedReports(List<ReportValidated> reports);

    /**
     * Deletes all validated reports from the database.
     */
    @Query("DELETE FROM reports_validated")
    void deleteAllValidatedReports();
}