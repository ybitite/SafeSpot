package ch.y.bitite.safespot.utils.buttonhelper;

import android.view.View;
import android.widget.Button;

import ch.y.bitite.safespot.R;

public class AddReportButtonHelper {

    private Button buttonAddReportAddReport;
    private Button buttonCancel;
    private AddReportButtonCallback addReportButtonCallback;

    public interface AddReportButtonCallback {
        void onAddReportClicked();
        void onCancelClicked();
    }

    public AddReportButtonHelper(View root, AddReportButtonCallback callback) {
        this.addReportButtonCallback = callback;
        buttonAddReportAddReport = root.findViewById(R.id.buttonAddReport);
        buttonCancel = root.findViewById(R.id.buttonCancel);
    }

    public void setupAddReportButtonListeners() {
        buttonAddReportAddReport.setOnClickListener(v -> {
            addReportButtonCallback.onAddReportClicked();
        });

        buttonCancel.setOnClickListener(v -> {
            addReportButtonCallback.onCancelClicked();
        });
    }
}