package ch.y.bitite.safespot.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.databinding.FragmentDashboardBinding;


public class DashboardFragment extends Fragment {
    private DashboardViewModel dashboardViewModel;
    private ReportAdapter adapter;
    private Button buttonAddReport;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentDashboardBinding binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
        adapter = new ReportAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        buttonAddReport = root.findViewById(R.id.buttonAddReport);

        dashboardViewModel.getValidatedReports().observe(getViewLifecycleOwner(), reports -> {
            adapter.setReports(reports);
        });

        binding.refreshButton.setOnClickListener(v -> dashboardViewModel.refreshValidatedReports());
        buttonAddReport.setOnClickListener(v -> {
            // Navigate to AddReportFragment
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_dashboardFragment_to_addReportFragment);
        });

        return root;
    }
}