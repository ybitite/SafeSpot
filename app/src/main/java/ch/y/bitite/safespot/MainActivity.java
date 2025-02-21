package ch.y.bitite.safespot;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

import ch.y.bitite.safespot.databinding.ActivityMainBinding;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * The main activity of the SafeSpot application.
 * This activity serves as the entry point and sets up the navigation.
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using View Binding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        // Get the NavController from the NavHostFragment
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        // Set up the BottomNavigationView with the NavController
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}