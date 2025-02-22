package ch.y.bitite.safespot.repository;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import ch.y.bitite.safespot.R;
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
    private final Context context;

    /**
     * Constructor for ReportRemoteDataSource.
     *
     * @param apiService The ApiService for network requests.
     */
    @Inject
    public ReportRemoteDataSource(ApiService apiService, Context context) {

        this.apiService = apiService;
        this.context = context;
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
                    callback.onFailure(context.getString(R.string.error_fetching_validated_reports) + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReportValidated>> call, @NonNull Throwable t) {
                callback.onFailure(context.getString(R.string.error_fetching_validated_reports) + t.getMessage());
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
        MultipartBody.Part imagePart = null;
        if (file != null) {
            RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
            imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }

        RequestBody description = RequestBody.create(report.getDescription() != null ? report.getDescription() : "", MediaType.parse("text/plain"));
        RequestBody longitude = RequestBody.create(String.valueOf(report.getLongitude()), MediaType.parse("text/plain"));
        RequestBody latitude = RequestBody.create(String.valueOf(report.getLatitude()), MediaType.parse("text/plain"));
        RequestBody dateTime = RequestBody.create(report.getDateTimeUtc() != null ? report.getDateTimeUtc() : "", MediaType.parse("text/plain"));

        Call<Void> call = apiService.addReport(imagePart, description, longitude, latitude, dateTime);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(API_SUCCESS_TAG, "Report added successfully!");
                    callback.onSuccess(context.getString(R.string.report_added_successfully));
                } else {
                    Log.e(API_ERROR_TAG, "Error adding report: " + response.code());
                    if (retryCount < MAX_RETRIES) {
                        retryAddReport(report, file, callback, retryCount + 1);
                    } else {
                        callback.onFailure(context.getString(R.string.error_adding_report_after_multiple_retries) + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(API_ERROR_TAG, "Error adding report", t);
                if (retryCount < MAX_RETRIES) {
                    retryAddReport(report, file, callback, retryCount + 1);
                } else {
                    callback.onFailure(context.getString(R.string.error_adding_report_after_multiple_retries) + t.getMessage());
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