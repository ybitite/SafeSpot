package ch.y.bitite.safespot.utils.buttonhelper;

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

    /**
     * Called when the sort button is clicked.
     */
    void onSortClicked();

}
