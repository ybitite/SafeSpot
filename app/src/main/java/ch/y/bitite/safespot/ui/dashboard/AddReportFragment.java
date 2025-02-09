package ch.y.bitite.safespot.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.repository.Report;
import ch.y.bitite.safespot.repository.ReportRepository;

public class AddReportFragment extends Fragment {

    private EditText editTextDescription;
    private EditText editTextLongitude;
    private EditText editTextLatitude;
    private Button buttonAddReport;
    private Button buttonCancel;
    private DashboardViewModel dashboardViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_report, container, false);

        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextLongitude = view.findViewById(R.id.editTextLongitude);
        editTextLatitude = view.findViewById(R.id.editTextLatitude);
        buttonAddReport = view.findViewById(R.id.buttonAddReport);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        buttonAddReport.setOnClickListener(v -> {
            addReport();
        });

        buttonCancel.setOnClickListener(v -> {
            // Navigate back to DashboardFragment
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    private void addReport() {
        String description = editTextDescription.getText().toString();
        String longitudeStr = editTextLongitude.getText().toString();
        String latitudeStr = editTextLatitude.getText().toString();

        if (description.isEmpty() || longitudeStr.isEmpty() || latitudeStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double longitude = Double.parseDouble(longitudeStr);
        double latitude = Double.parseDouble(latitudeStr);

        Report report = new Report();
        report.description = description;
        report.longitude = longitude;
        report.latitude = latitude;
        report.setDate_time(new Date());
        report.image="q";
        report.video="1";

        Log.d("AddReportFragment", "Report to send: " + report.toString());
        dashboardViewModel.addReport(report, new ReportRepository.AddReportCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Report added successfully", Toast.LENGTH_SHORT).show();
                // Navigate back to DashboardFragment
                getParentFragmentManager().popBackStack();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Failed to add report: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}