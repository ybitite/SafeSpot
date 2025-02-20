package ch.y.bitite.safespot.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import ch.y.bitite.safespot.model.Report;
import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.network.ApiService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Data source for managing Report data from the remote API.
 */
public class ReportRemoteDataSource {
    private static final String API_ERROR_TAG = "API_ERROR";
    private static final String API_SUCCESS_TAG = "API_SUCCESS";
    private static final int MAX_RETRIES = 5;
    private final ApiService apiService;

    /**
     * Constructor for ReportRemoteDataSource.
     *
     * @param apiService The ApiService for network requests.
     */
    @Inject
    public ReportRemoteDataSource(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Fetches validated reports from the API.
     *
     * @param callback The callback to notify the result of the operation.
     */
    public void fetchValidatedReports(FetchValidatedReportsCallback callback) {
        apiService.getValidatedReports().enqueue(new Callback<List<ReportValidated>>() {
            @Override
            public void onResponse(@NonNull Call<List<ReportValidated>> call, @NonNull Response<List<ReportValidated>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e(API_ERROR_TAG, "Error fetching validated reports: " + response.code());
                    callback.onFailure("Error fetching validated reports: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReportValidated>> call, @NonNull Throwable t) {
                Log.e(API_ERROR_TAG, "Error fetching validated reports", t);
                callback.onFailure("Error fetching validated reports: " + t.getMessage());
            }
        });
    }

    /**
     * Adds a new report to the API.
     *
     * @param report     The Report object to add.
     * @param file       The file of the image.
     * @param callback   The callback to notify the result of the operation.
     */
    public void addReport(Report report, File file, AddReportCallback callback) {
        addReportWithRetry(report, file, callback, 0);
    }

    /**
     * Adds a new report to the API with retry logic.
     *
     * @param report     The Report object to add.
     * @param file       The file of the image.
     * @param callback   The callback to notify the result of the operation.
     * @param retryCount The current retry count.
     */
    private void addReportWithRetry(Report report, File file, AddReportCallback callback, int retryCount) {
        RequestBody requestFile = file != null ? RequestBody.create(MediaType.parse("multipart/form-data"), file) : null;
        MultipartBody.Part imagePart = requestFile != null ? MultipartBody.Part.createFormData("image", file.getName(), requestFile) : null;

        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), report.getDescription() != null ? report.getDescription() : "");
        RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(report.getLongitude()));
        RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(report.getLatitude()));
        RequestBody dateTime = RequestBody.create(MediaType.parse("text/plain"), report.getDateTimeUtc() != null ? report.getDateTimeUtc() : "");

        Call<Void> call = apiService.addReport(imagePart, description, longitude, latitude, dateTime);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(API_SUCCESS_TAG, "Report added successfully!");
                    callback.onSuccess("Report added successfully!");
                } else {
                    Log.e(API_ERROR_TAG, "Error adding report: " + response.code());
                    if (retryCount < MAX_RETRIES) {
                        retryAddReport(report, file, callback, retryCount + 1);
                    } else {
                        callback.onFailure("Error adding report after multiple retries: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(API_ERROR_TAG, "Error adding report", t);
                if (retryCount < MAX_RETRIES) {
                    retryAddReport(report, file, callback, retryCount + 1);
                } else {
                    callback.onFailure("Error adding report after multiple retries: " + t.getMessage());
                }
            }
        });
    }

    /**
     * Retries adding a report to the API.
     *
     * @param report     The Report object to add.
     * @param file       The file of the image.
     * @param callback   The callback to notify the result of the operation.
     * @param retryCount The current retry count.
     */
    private void retryAddReport(Report report, File file, AddReportCallback callback, int retryCount) {

        new android.os.Handler().postDelayed(() -> {
            Log.d(API_SUCCESS_TAG, "Retrying to add report... (Attempt " + (retryCount + 1) + ")");
            addReportWithRetry(report, file, callback, retryCount);
        }, 3000); // Retry after 3 seconds
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