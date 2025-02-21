package ch.y.bitite.safespot.network;

import java.util.List;

import ch.y.bitite.safespot.model.ReportValidated;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Interface defining the API service for network requests.
 */
public interface ApiService {
    /**
     * Retrieves a list of validated reports.
     *
     * @return A Call object containing a list of ReportValidated.
     */
    @GET("api/reports")
    Call<List<ReportValidated>> getValidatedReports();

    /**
     * Adds a new report.
     *
     * @param image       The image part of the report.
     * @param description The description of the report.
     * @param longitude   The longitude of the report location.
     * @param latitude    The latitude of the report location.
     * @param date_Time   The date and time of the report.
     * @return A Call object.
     */
    @Multipart
    @POST("api/reports")
    Call<Void> addReport(
            @Part MultipartBody.Part image,
            @Part("description") RequestBody description,
            @Part("longitude") RequestBody longitude,
            @Part("latitude") RequestBody latitude,
            @Part("date_Time") RequestBody date_Time
    );
}