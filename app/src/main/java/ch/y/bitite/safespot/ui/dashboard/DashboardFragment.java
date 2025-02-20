package ch.y.bitite.safespot.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import javax.inject.Inject;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.databinding.FragmentDashboardBinding;
import ch.y.bitite.safespot.utils.buttonhelper.DashboardButtonHelper;
import ch.y.bitite.safespot.viewmodel.DashboardViewModel;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for the dashboard screen.
 * This fragment displays a list of validated reports.
 */
@AndroidEntryPoint
public class DashboardFragment extends Fragment implements DashboardButtonHelper.DashboardButtonCallback {
    private DashboardViewModel dashboardViewModel;
    @Inject
    ReportAdapter adapter;
    private FragmentDashboardBinding binding;
    private DashboardButtonHelper buttonHelper;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Obtenir l'instance du ViewModel via ViewModelProvider
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        buttonHelper = new DashboardButtonHelper(root, this);
        buttonHelper.setupDashboardButtonListeners();

        dashboardViewModel.getValidatedReports().observe(getViewLifecycleOwner(), reports -> {
            adapter.updateReports(reports);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAddReportClicked() {
        // Navigate to AddReportFragment
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_dashboardFragment_to_addReportFragment);
    }

    @Override
    public void onRefreshClicked() {
        dashboardViewModel.fetchValidatedReports();
    }
}