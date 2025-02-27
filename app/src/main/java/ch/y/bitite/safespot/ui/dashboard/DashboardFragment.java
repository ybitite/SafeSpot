package ch.y.bitite.safespot.ui.dashboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Importation de Button
import android.widget.EditText; // Importation de EditText

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import javax.inject.Inject;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.databinding.FragmentDashboardBinding;
import ch.y.bitite.safespot.utils.buttonhelper.DashboardButtonCallback;
import ch.y.bitite.safespot.utils.buttonhelper.DashboardButtonHelper;
import ch.y.bitite.safespot.viewmodel.DashboardViewModel;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for the dashboard screen.
 * This fragment displays a list of validated reports.
 */
@AndroidEntryPoint
public class DashboardFragment extends Fragment implements DashboardButtonCallback {
    private DashboardViewModel dashboardViewModel;
    @Inject
    ReportAdapter adapter;
    private FragmentDashboardBinding binding;
    private Button sortButton; // Déclaration du bouton de tri
    private EditText searchEditText; // Déclaration du champ de recherche

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

        sortButton = root.findViewById(R.id.sort_by_date_button);
        searchEditText = root.findViewById(R.id.search_edit_text);

        // Set up the RecyclerView with a LinearLayoutManager and the ReportAdapter
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        // Set up the button listeners using DashboardButtonHelper
        DashboardButtonHelper buttonHelper = new DashboardButtonHelper(root, (DashboardButtonCallback) this);
        buttonHelper.setupDashboardButtonListeners();

        // Set up the search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dashboardViewModel.filterReports(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return root;
    }

    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view.
     *
     * @param view               The View returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Instantiate DashboardViewModel using ViewModelProvider
        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        // Observe the LiveData of validated reports and update the adapter when the data changes
        dashboardViewModel.getValidatedReports().observe(getViewLifecycleOwner(), reports -> adapter.setReports(reports));
    }

    /**
     * Called when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Called when the add report button is clicked.
     */
    @Override
    public void onAddReportClicked() {
        // Navigate to AddReportFragment
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_dashboardFragment_to_addReportFragment);
    }

    /**
     * Called when the refresh button is clicked.
     */
    @Override
    public void onRefreshClicked() {
        // Check if there are existing validated reports and fetch new ones if there are
        if(dashboardViewModel.getValidatedReports().getValue() != null){
            dashboardViewModel.fetchValidatedReports();
        }
    }

    // Ajout de la méthode onSortClicked
    @Override
    public void onSortClicked() {
        dashboardViewModel.sortReportsByDate();
    }
}