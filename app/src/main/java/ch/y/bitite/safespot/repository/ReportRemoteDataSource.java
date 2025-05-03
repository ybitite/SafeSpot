package ch.y.bitite.safespot.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private static final int MAX_IMAGE_WIDTH = 1024; // Maximum width for the compressed image
    private static final int MAX_IMAGE_HEIGHT = 768; // Maximum height for the compressed image
    private static final int IMAGE_QUALITY = 80; // Quality of the compressed image (0-100)
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
     * @param imageFile  The file of the image.
     * @param audioFile The file of the audio.
     * @param callback   The callback to notify the result of the operation.
     */
    public void addReport(Report report, File imageFile, File audioFile, AddReportCallback callback) {
        File compressedImageFile = null;
        if (imageFile != null) {
            compressedImageFile = compressImage(imageFile);
        }
        if (compressedImageFile != null || imageFile == null) {
            addReportWithRetry(report, compressedImageFile, audioFile, callback, 0);
        } else {
            callback.onFailure("erreur");
        }
    }

    /**
     * Adds a new report to the API with retry logic.
     *
     * @param report     The Report object to add.
     * @param imageFile  The file of the image.
     * @param audioFile The file of the audio.
     * @param callback   The callback to notify the result of the operation.
     * @param retryCount The current retry count.
     */
    private void addReportWithRetry(Report report, File imageFile, File audioFile, AddReportCallback callback, int retryCount) {
        MultipartBody.Part imagePart = null;
        if (imageFile != null) {
            RequestBody requestImageFile = RequestBody.create(imageFile, MediaType.parse("multipart/form-data"));
            imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestImageFile);
        }
        MultipartBody.Part audioPart = null;
        if (audioFile != null) {
            RequestBody requestAudioFile = RequestBody.create(audioFile, MediaType.parse("multipart/form-data"));
            audioPart = MultipartBody.Part.createFormData("audio", audioFile.getName(), requestAudioFile);
        }

        RequestBody description = RequestBody.create(report.getDescription() != null ? report.getDescription() : "", MediaType.parse("text/plain"));
        RequestBody longitude = RequestBody.create(String.valueOf(report.getLongitude()), MediaType.parse("text/plain"));
        RequestBody latitude = RequestBody.create(String.valueOf(report.getLatitude()), MediaType.parse("text/plain"));
        RequestBody dateTime = RequestBody.create(report.getDateTimeUtc() != null ? report.getDateTimeUtc() : "", MediaType.parse("text/plain"));

        Call<Void> call = apiService.addReport(imagePart, audioPart, description, longitude, latitude, dateTime);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(API_SUCCESS_TAG, "Report added successfully!");
                    callback.onSuccess(context.getString(R.string.report_added_successfully));
                } else {
                    Log.e(API_ERROR_TAG, "Error adding report: " + response.code());
                    if (retryCount < MAX_RETRIES) {
                        retryAddReport(report, imageFile, audioFile, callback, retryCount + 1);
                    } else {
                        callback.onFailure(context.getString(R.string.error_adding_report_after_multiple_retries) + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(API_ERROR_TAG, "Error adding report", t);
                if (retryCount < MAX_RETRIES) {
                    retryAddReport(report, imageFile, audioFile, callback, retryCount + 1);
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
     * @param imageFile  The file of the image.
     * @param audioFile The file of the audio.
     * @param callback   The callback to notify the result of the operation.
     * @param retryCount The current retry count.
     */
    private void retryAddReport(Report report, File imageFile, File audioFile, AddReportCallback callback, int retryCount) {

        new android.os.Handler().postDelayed(() -> {
            Log.d(API_SUCCESS_TAG, "Retrying to add report... (Attempt " + (retryCount + 1) + ")");
            addReportWithRetry(report, imageFile, audioFile, callback, retryCount);
        }, 3000); // Retry after 3 seconds
    }

    /**
     * Compresses the image file.
     *
     * @param file The original image file.
     * @return The compressed image file, or null if an error occurred.
     */
    private File compressImage(File file) {
        if (file == null) {
            return null;
        }
        try {
            // Decode the file into a Bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            // Calculate the scaling factor
            int scale = 1;
            while (options.outWidth / scale / 2 >= MAX_IMAGE_WIDTH &&
                    options.outHeight / scale / 2 >= MAX_IMAGE_HEIGHT) {
                scale *= 2;
            }

            // Decode the file with the scaling factor
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            // Resize the bitmap if necessary
            bitmap = resizeBitmap(bitmap, MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);

            // Compress the bitmap
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, bos);
            byte[] bitmapData = bos.toByteArray();

            // Create a new file for the compressed image
            File compressedFile = new File(file.getParentFile(), "compressed_" + file.getName());
            FileOutputStream fos = new FileOutputStream(compressedFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();

            // Recycle the bitmap to free memory
            bitmap.recycle();

            return compressedFile;
        } catch (IOException e) {
            Log.e(API_ERROR_TAG, "Error compressing image", e);
            return null;
        }
    }

    /**
     * Resizes the bitmap to the specified maximum width and height.
     *
     * @param bitmap    The bitmap to resize.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return The resized bitmap.
     */
    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap;
        }

        float ratio = Math.min((float) maxWidth / width, (float) maxHeight / height);
        int newWidth = Math.round(ratio * width);
        int newHeight = Math.round(ratio * height);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        bitmap.recycle();
        return resizedBitmap;
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