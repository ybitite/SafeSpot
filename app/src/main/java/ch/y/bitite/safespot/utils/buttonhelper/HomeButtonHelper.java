package ch.y.bitite.safespot.utils.buttonhelper;

import android.view.View;
import android.widget.Button;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.ui.home.HomeFragment;

public class HomeButtonHelper {

    private Button buttonAddReportHome;
    private Button buttonCenterMap;
    private HomeButtonCallback homeButtonCallback;

    public interface HomeButtonCallback {
        void onCenterMapClicked();
        void onAddReportClicked();
    }

    public HomeButtonHelper(View root, HomeButtonCallback callback) {
        this.homeButtonCallback = callback;
        buttonAddReportHome = root.findViewById(R.id.buttonAddReportHome);
        buttonCenterMap = root.findViewById(R.id.buttonCenterMap);
    }

    public void setupHomeButtonListeners() {
        buttonAddReportHome.setOnClickListener(v -> {
            homeButtonCallback.onAddReportClicked();
        });

        buttonCenterMap.setOnClickListener(v -> {
            homeButtonCallback.onCenterMapClicked();
        });
    }
}