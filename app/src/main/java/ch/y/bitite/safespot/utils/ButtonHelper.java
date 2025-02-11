package ch.y.bitite.safespot.utils;

import android.view.View;
import android.widget.Button;

import androidx.navigation.fragment.NavHostFragment;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.ui.dashboard.AddReportFragment;
import ch.y.bitite.safespot.ui.home.HomeFragment;

public class ButtonHelper {

    private HomeFragment homeFragment;
    private AddReportFragment addReportFragment;
    private Button buttonAddReport;
    private Button buttonCenterMap;
    private Button buttonCancel;
    private ButtonCallback buttonCallback;

    public interface ButtonCallback {
        default void onCenterMapClicked() {}
        default void onAddReportClicked() {}
        default void onCancelClicked() {}
    }

    public ButtonHelper(HomeFragment homeFragment, View root, ButtonCallback callback) {
        this.homeFragment = homeFragment;
        this.buttonCallback = callback;
        buttonAddReport = root.findViewById(R.id.buttonAddReportHome);
        buttonCenterMap = root.findViewById(R.id.buttonCenterMap);
        setupHomeButtonListeners();
    }

    public ButtonHelper(AddReportFragment addReportFragment, View root, ButtonCallback callback) {
        this.addReportFragment = addReportFragment;
        this.buttonCallback = callback;
        buttonAddReport = root.findViewById(R.id.buttonAddReport);buttonCancel = root.findViewById(R.id.buttonCancel);
        setupAddReportButtonListeners();
    }

    private void setupHomeButtonListeners() {
        buttonAddReport.setOnClickListener(v -> {
            // Navigate to AddReportFragment
            NavHostFragment.findNavController(homeFragment)
                    .navigate(R.id.action_homeFragment_to_addReportFragment);
        });

        buttonCenterMap.setOnClickListener(v -> {
            buttonCallback.onCenterMapClicked();
        });
    }

    private void setupAddReportButtonListeners() {
        buttonAddReport.setOnClickListener(v -> {
            buttonCallback.onAddReportClicked();
        });

        buttonCancel.setOnClickListener(v -> {
            buttonCallback.onCancelClicked();
        });
    }
}