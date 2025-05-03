package ch.y.bitite.safespot.repository;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;

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
    private final ReportLocalDataSource localDataSource;
    private final ReportRemoteDataSource remoteDataSource;
    private final ReportFileDataSource fileDataSource;

    /**
     * Constructor for ReportRepository.
     *
     * @param localDataSource  The ReportLocalDataSource for database operations.
     * @param remoteDataSource The ReportRemoteDataSource for network requests.
     * @param fileDataSource   The ReportFileDataSource for file operations.
     */
    @Inject
    public ReportRepository(ReportLocalDataSource localDataSource, ReportRemoteDataSource remoteDataSource, ReportFileDataSource fileDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
        this.fileDataSource = fileDataSource;
    }

    /**
     * Retrieves all validated reports from the local data source.
     *
     * @return A LiveData list of ReportValidated objects.
     */
    public LiveData<List<ReportValidated>> getAllValidatedReports() {
        return localDataSource.getAllValidatedReports();
    }


    /**
     * Fetches validated reports from the API and updates the local database.
     *
     * @param callback The callback to notify the result of the operation.
     */
    public void fetchValidatedReports(FetchValidatedReportsCallback callback) {
        remoteDataSource.fetchValidatedReports(new ReportRemoteDataSource.FetchValidatedReportsCallback() {
            @Override
            public void onSuccess(List<ReportValidated> reports) {
                localDataSource.insertValidatedReports(reports);
                callback.onSuccess(reports);
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }

    /**
     * Adds a new report to the API.
     *
     * @param report     The Report object to add.
     * @param imageUri   The URI of the image associated with the report.
     * @param callback   The callback to notify the result of the operation.
     * @param audioFile The audio file associated with the report.
     */
    public void addReport(Report report, Uri imageUri, File audioFile, AddReportCallback callback) {
        File imageFile = null;
        if (imageUri != null) {
            imageFile = fileDataSource.getFileFromUri(imageUri);
        }
        remoteDataSource.addReport(report, imageFile, audioFile, new ReportRemoteDataSource.AddReportCallback() {
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

    /**
     * Checks if data has been inserted into the local data source.
     *
     * @return True if data has been inserted, false otherwise.
     */
    public boolean isDataInserted(){
        return localDataSource.isDataInserted();
    }

    /**
     * Callback interface for the fetchValidatedReports operation.
     */
    public interface FetchValidatedReportsCallback {
        void onSuccess(List<ReportValidated> reports);
        void onFailure(String errorMessage);
    }

    /**
     * Callback interface for the addReport operation.
     */
    public interface AddReportCallback {
        void onSuccess(String message);

        void onFailure(String errorMessage);
    }
}