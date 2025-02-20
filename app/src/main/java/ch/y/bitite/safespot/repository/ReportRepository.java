package ch.y.bitite.safespot.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import ch.y.bitite.safespot.model.Report;
import ch.y.bitite.safespot.model.ReportValidated;

/**
 * Repository for managing Report data.
 * This class coordinates interactions between the local database and the remote API.
 */
public class ReportRepository {

    private static final String TAG = "ReportRepository";
    private static final String PREFS_NAME = "ReportPrefs";
    private static final String IS_DATABASE_EMPTY_KEY = "isDatabaseEmpty";
    private final ReportLocalDataSource localDataSource;
    private final ReportRemoteDataSource remoteDataSource;
    private final ReportFileDataSource fileDataSource;
    private final MediatorLiveData<List<ReportValidated>> mediatorLiveData = new MediatorLiveData<>();
    private boolean isDatabaseEmpty;



    private boolean isFirstFetch = true;
    private final SharedPreferences sharedPreferences;

    /**
     * Constructor for ReportRepository.
     *
     * @param localDataSource  The ReportLocalDataSource for database operations.
     * @param remoteDataSource The ReportRemoteDataSource for network requests.
     * @param fileDataSource   The ReportFileDataSource for file operations.
     * @param context          The application context.
     */
    @Inject
    public ReportRepository(ReportLocalDataSource localDataSource, ReportRemoteDataSource remoteDataSource, ReportFileDataSource fileDataSource, Context context) {

        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
        this.fileDataSource = fileDataSource;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // Load the database state from SharedPreferences
        isDatabaseEmpty = sharedPreferences.getBoolean(IS_DATABASE_EMPTY_KEY, true);
        // Observe changes in the local database
        LiveData<List<ReportValidated>> localData = localDataSource.getAllValidatedReports();
        mediatorLiveData.addSource(localData, new Observer<List<ReportValidated>>() {
            @Override
            public void onChanged(List<ReportValidated> reportValidateds) {
                mediatorLiveData.setValue(reportValidateds);
                // Update the database state and save it to SharedPreferences
                boolean newIsDatabaseEmpty = reportValidateds.isEmpty();
                if (isDatabaseEmpty != newIsDatabaseEmpty) {
                    isDatabaseEmpty = newIsDatabaseEmpty;
                    sharedPreferences.edit().putBoolean(IS_DATABASE_EMPTY_KEY, isDatabaseEmpty).apply();
                }
            }
        });
    }

    /**
     * Retrieves all validated reports from the local database.
     *
     * @return A LiveData list of ReportValidated objects.
     */
    public LiveData<List<ReportValidated>> getAllValidatedReports() {
        return mediatorLiveData;
    }

    /**
     * Fetches validated reports from the API and updates the local database.
     */
    public void fetchValidatedReports(FetchValidatedReportsCallback callback) {

        remoteDataSource.fetchValidatedReports(new ReportRemoteDataSource.FetchValidatedReportsCallback() {
            @Override
            public void onSuccess(List<ReportValidated> reports) {
                localDataSource.insertValidatedReports(reports);
                callback.onSuccess(reports);
                isFirstFetch = false;
            }

            @Override
            public void onFailure(String errorMessage) {
                if (isDatabaseEmpty || isFirstFetch) {
                    callback.onFailure(errorMessage);
                } else {
                    callback.onSuccess(mediatorLiveData.getValue());
                }
                isFirstFetch = false;
            }
        });
    }

    /**
     * Adds a new report to the API.
     *
     * @param report     The Report object to add.
     * @param imageUri   The URI of the image associated with the report.
     * @param callback   The callback to notify the result of the operation.
     */
    public void addReport(Report report, Uri imageUri, AddReportCallback callback) {
        File file = fileDataSource.getFileFromUri(imageUri);
        remoteDataSource.addReport(report, file, new ReportRemoteDataSource.AddReportCallback() {
            @Override
            public void onSuccess(String message) {
                Log.d(TAG, "Report added successfully!");
                callback.onSuccess(message);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Error adding report: " + errorMessage);
                callback.onFailure(errorMessage);
            }
        });
    }
    public boolean isFirstFetch() {
        return isFirstFetch;
    }
    public interface FetchValidatedReportsCallback {
        void onSuccess(List<ReportValidated> reports);

        void onFailure(String errorMessage);
    }

    public interface AddReportCallback {
        void onSuccess(String message);

        void onFailure(String errorMessage);
    }
}