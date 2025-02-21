package ch.y.bitite.safespot;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;

/**
 * The main application class for the SafeSpot app.
 * This class is annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
public class SafeSpotApplication extends Application {

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // Global application initialization
    }
}