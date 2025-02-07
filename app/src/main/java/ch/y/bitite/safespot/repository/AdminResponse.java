package ch.y.bitite.safespot.repository;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ch.y.bitite.safespot.model.ReportValidated;

public class AdminResponse {
    @SerializedName("validatedReports")
    public List<ReportValidated> validatedReports;
}