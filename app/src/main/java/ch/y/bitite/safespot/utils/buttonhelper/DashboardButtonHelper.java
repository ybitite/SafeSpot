package ch.y.bitite.safespot.utils.buttonhelper;

import android.view.View;
import android.widget.Button; // Importation de Button
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ch.y.bitite.safespot.R;

/**
 * Helper class for managing button interactions in the DashboardFragment.
 */
public class DashboardButtonHelper {
    private final FloatingActionButton buttonAddReport;
    private final FloatingActionButton refreshButton;
    private final DashboardButtonCallback dashboardButtonCallback;
    private final Button sortButton; // Déclaration du bouton de tri

    /**
     * Constructor for DashboardButtonHelper.
     *
     * @param root     The root view containing the buttons.
     * @param callback The callback to notify of button clicks.
     */
    public DashboardButtonHelper(View root, DashboardButtonCallback callback) {
        this.dashboardButtonCallback = callback;
        buttonAddReport = root.findViewById(R.id.buttonAddReport);
        refreshButton = root.findViewById(R.id.refreshButton);
        sortButton = root.findViewById(R.id.sort_by_date_button);
    }

    /**
     * Sets up the click listeners for the add report, refresh, and sort buttons.
     */
    public void setupDashboardButtonListeners() {
        buttonAddReport.setOnClickListener(v -> dashboardButtonCallback.onAddReportClicked());
        refreshButton.setOnClickListener(v -> dashboardButtonCallback.onRefreshClicked());
        sortButton.setOnClickListener(v -> dashboardButtonCallback.onSortClicked());
    }
}
