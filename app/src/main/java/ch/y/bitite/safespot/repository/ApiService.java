package ch.y.bitite.safespot.repository;

import java.util.List;

import ch.y.bitite.safespot.model.ReportValidated;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("api/reports")
    Call<List<ReportValidated>> getValidatedReports(); // Changed to List<ReportValidated>

    @POST("api/reports")
    Call<Void> addReport(@Body ReportValidated newReport);
}