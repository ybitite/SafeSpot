package ch.y.bitite.safespot.repository;

import ch.y.bitite.safespot.model.ReportValidated;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    //@GET("reports/admin")
    //Call<AdminResponse> getValidatedReports();

    @GET("api/reports")
    Call<AdminResponse> getValidatedReports();

    @POST("api/reports")
    Call<Void> addReport(@Body ReportValidated newReport);
}