package ch.y.bitite.safespot.utils.buttonhelper;

import android.view.View;
import android.widget.Button;

import ch.y.bitite.safespot.R;

/**
 * Helper class for managing button interactions in the AddReportFragment.
 */
public class AddReportButtonHelper {

    private final Button buttonAddReportAddReport;
    private final Button buttonCancel;
    private final AddReportButtonCallback addReportButtonCallback;

    /**
     * Callback interface for button clicks.
     */
    public interface AddReportButtonCallback {
        /**
         * Called when the add report button is clicked.
         */
        void onAddReportClicked();

        /**
         * Called when the cancel button is clicked.
         */
        void onCancelClicked();
    }

    /**
     * Constructor for AddReportButtonHelper.
     *
     * @param root     The root view containing the buttons.
     * @param callback The callback to notify of button clicks.
     */
    public AddReportButtonHelper(View root, AddReportButtonCallback callback) {
        this.addReportButtonCallback = callback;
        buttonAddReportAddReport = root.findViewById(R.id.buttonAddReport);
        buttonCancel = root.findViewById(R.id.buttonCancel);
    }

    /**
     * Sets up the click listeners for the add report and cancel buttons.
     */
    public void setupAddReportButtonListeners() {
        buttonAddReportAddReport.setOnClickListener(v -> addReportButtonCallback.onAddReportClicked());

        buttonCancel.setOnClickListener(v -> addReportButtonCallback.onCancelClicked());
    }
}