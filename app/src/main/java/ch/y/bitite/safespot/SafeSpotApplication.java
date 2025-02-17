package ch.y.bitite.safespot;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class SafeSpotApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialisation globale de l'application
    }
}