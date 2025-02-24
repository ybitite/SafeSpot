package ch.y.bitite.safespot.utils.buttonhelper;

import android.view.View;
import android.widget.Button;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ch.y.bitite.safespot.R;

/**
 * Helper class for managing button interactions in the HomeFragment.
 */
public class HomeButtonHelper {

    private final FloatingActionButton buttonAddReportHome;
    private final FloatingActionButton buttonCenterMap;
    private final FloatingActionButton buttonRefresh;
    private final HomeButtonCallback homeButtonCallback;

    /**
     * Constructor for HomeButtonHelper.
     *
     * @param root     The root view containing the buttons.
     * @param callback The callback to notify of button clicks.
     */
    public HomeButtonHelper(View root, HomeButtonCallback callback) {
        this.homeButtonCallback = callback;
        buttonAddReportHome = root.findViewById(R.id.buttonAddReportHome);
        buttonCenterMap = root.findViewById(R.id.buttonCenterMap);
        buttonRefresh = root.findViewById(R.id.buttonRefreshHome);
    }

    /**
     * Sets up the click listeners for the add report, center map, and refresh buttons.
     */
    public void setupHomeButtonListeners() {
        buttonAddReportHome.setOnClickListener(v -> homeButtonCallback.onAddReportClicked());
        buttonCenterMap.setOnClickListener(v -> homeButtonCallback.onCenterMapClicked());
        buttonRefresh.setOnClickListener(v -> homeButtonCallback.onRefreshClicked());
    }

    /**
     * Callback interface for button clicks.
     */
    public interface HomeButtonCallback {
        /**
         * Called when the center map button is clicked.
         */
        void onCenterMapClicked();

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