package ch.y.bitite.safespot.repository;

import android.content.Context;
import android.util.Log;

import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.model.room.ReportDao;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

/**
 * Data source for managing Report data in the local database.
 */
public class ReportLocalDataSource {

    private final ReportDao reportDao;
    private final ExecutorService executorService;
    private final RxDataStore<Preferences> dataStore;
    private static final Preferences.Key<Boolean> DATA_INSERTED_KEY = PreferencesKeys.booleanKey("data_inserted"); // Correct line

    /**
     * Constructor for ReportLocalDataSource.
     *
     * @param reportDao       The ReportDao for database operations.
     * @param executorService The ExecutorService for background tasks.
     */
    @Inject
    public ReportLocalDataSource(ReportDao reportDao, ExecutorService executorService, Context context) {
        this.reportDao = reportDao;
        this.executorService = executorService;
        this.dataStore = new RxPreferenceDataStoreBuilder(context, "my_app_prefs").build();
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
            List<ReportValidated> existingReports = reportDao.getListReports();
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
        // Set data_inserted to false after insertion (outside of the executor)
        setDataInserted();
    }

    /**
     * Sets the data_inserted flag in DataStore.
     */
    private void setDataInserted() {
        Completable completable = dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(DATA_INSERTED_KEY, false);
            return Single.just(mutablePreferences);
        }).ignoreElement();

    }

    /**
     * Gets the data_inserted flag from DataStore.
     *
     * @return The data_inserted flag.
     */
    public boolean isDataInserted() {
        try {
            return dataStore.data().map(prefs -> prefs.get(DATA_INSERTED_KEY) != null ? prefs.get(DATA_INSERTED_KEY) : false).blockingFirst();
        } catch (Exception e) {
            Log.e("ReportLocalDataSource", "isDataInserted: Error", e);
            return false;
        }
    }
}