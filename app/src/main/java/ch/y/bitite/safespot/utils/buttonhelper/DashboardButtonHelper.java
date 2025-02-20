package ch.y.bitite.safespot.utils.buttonhelper;

import android.view.View;
import android.widget.Button;

import ch.y.bitite.safespot.R;

public class DashboardButtonHelper {
    private Button buttonAddReport;
    private Button refreshButton;
    private DashboardButtonCallback dashboardButtonCallback;

    public interface DashboardButtonCallback {
        void onAddReportClicked();
        void onRefreshClicked();
    }

    public DashboardButtonHelper(View root, DashboardButtonCallback callback) {
        this.dashboardButtonCallback = callback;
        buttonAddReport = root.findViewById(R.id.buttonAddReport);
        refreshButton = root.findViewById(R.id.refreshButton);
    }

    public void setupDashboardButtonListeners() {
        buttonAddReport.setOnClickListener(v -> {
            dashboardButtonCallback.onAddReportClicked();
        });
        refreshButton.setOnClickListener(v -> {
            dashboardButtonCallback.onRefreshClicked();
        });
    }
}