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

public interface ApiService {
    @GET("api/reports")
    Call<List<ReportValidated>> getValidatedReports();

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