package ch.y.bitite.safespot.repository;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.model.room.ReportDao;

/**
 * Data source for managing Report data in the local database.
 */
public class ReportLocalDataSource {

    private final ReportDao reportDao;
    private final ExecutorService executorService;

    /**
     * Constructor for ReportLocalDataSource.
     *
     * @param reportDao       The ReportDao for database operations.
     * @param executorService The ExecutorService for background tasks.
     */
    @Inject
    public ReportLocalDataSource(ReportDao reportDao, ExecutorService executorService) {
        this.reportDao = reportDao;
        this.executorService = executorService;
    }

    /**
     * Retrieves all validated reports from the local database.
     *
     * @return A LiveData list of ReportValidated objects.
     */
    public LiveData<List<ReportValidated>> getAllValidatedReports() {
        return reportDao.getAllValidatedReports();
    }

    /**
     * Inserts a list of validated reports into the local database.
     *
     * @param reports The list of ReportValidated objects to insert.
     */
    public void insertValidatedReports(List<ReportValidated> reports) {
        executorService.execute(() -> {
            List<ReportValidated> existingReports = reportDao.getAllValidatedReportsNotLiveData();
            List<ReportValidated> reportsToInsert = new ArrayList<>();
            List<ReportValidated> reportsToUpdate = new ArrayList<>();
            List<ReportValidated> reportsToDelete = new ArrayList<>();

            // Find reports to insert or update
            for (ReportValidated report : reports) {
                boolean found = false;
                for (ReportValidated existingReport : existingReports) {
                    if (report.getId() == existingReport.getId()) {
                        found = true;
                        if (!report.equals(existingReport)) {
                            reportsToUpdate.add(report);
                        }
                        break;
                    }
                }
                if (!found) {
                    reportsToInsert.add(report);
                }
            }

            // Find reports to delete
            for (ReportValidated existingReport : existingReports) {
                boolean found = false;
                for (ReportValidated report : reports) {
                    if (report.getId() == existingReport.getId()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    reportsToDelete.add(existingReport);
                }
            }

            // Perform database operations
            if (!reportsToInsert.isEmpty()) {
                reportDao.insertValidatedReports(reportsToInsert);
            }
            if (!reportsToUpdate.isEmpty()) {
                reportDao.updateValidatedReports(reportsToUpdate);
            }
            if (!reportsToDelete.isEmpty()) {
                reportDao.deleteValidatedReports(reportsToDelete);
            }
        });
    }
}