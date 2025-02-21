package ch.y.bitite.safespot.utils.buttonhelper;

import android.view.View;
import android.widget.Button;

import ch.y.bitite.safespot.R;

/**
 * Helper class for managing button interactions in the DashboardFragment.
 */
public class DashboardButtonHelper {
    private final Button buttonAddReport;
    private final Button refreshButton;
    private final DashboardButtonCallback dashboardButtonCallback;

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
    }

    /**
     * Sets up the click listeners for the add report and refresh buttons.
     */
    public void setupDashboardButtonListeners() {
        buttonAddReport.setOnClickListener(v -> dashboardButtonCallback.onAddReportClicked());
        refreshButton.setOnClickListener(v -> dashboardButtonCallback.onRefreshClicked());
    }

    /**
     * Callback interface for button clicks.
     */
    public interface DashboardButtonCallback {
        /**
         * Called when the add report button is clicked.
         */
        void onAddReportClicked();

        /**
         * Called when the refresh button is clicked.
         */
        void onRefreshClicked();
    }
}