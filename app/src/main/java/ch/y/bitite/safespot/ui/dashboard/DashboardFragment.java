package ch.y.bitite.safespot.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import ch.y.bitite.safespot.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {
    private DashboardViewModel dashboardViewModel;
    private ReportAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentDashboardBinding binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        adapter = new ReportAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        dashboardViewModel.getValidatedReports().observe(getViewLifecycleOwner(), reports -> {
            adapter.setReports(reports);
        });

        binding.refreshButton.setOnClickListener(v -> dashboardViewModel.refreshValidatedReports());

        return root;
    }
}