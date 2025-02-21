package ch.y.bitite.safespot.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;

/**
 * Helper class for managing location-related operations.
 */
public class LocationHelper {

    private static final String TAG = "LocationHelper";
    private final Fragment fragment;
    private final LocationCallback externalLocationCallback;
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationRequest locationRequest;
    private final com.google.android.gms.location.LocationCallback internalLocationCallback;
    private final ActivityResultLauncher<String[]> locationPermissionRequest;

    /**
     * Constructor for LocationHelper.
     *
     * @param fragment The fragment that is using this helper.
     * @param callback The callback to notify of location results.
     */
    public LocationHelper(Fragment fragment, LocationCallback callback) {
        this.fragment = fragment;
        this.externalLocationCallback = callback;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(fragment.requireContext());
        this.locationRequest = createLocationRequest();
        this.internalLocationCallback = createInternalLocationCallback();
        this.locationPermissionRequest = fragment.registerForActivityResult(new ActivityResultContracts
                .RequestMultiplePermissions(), result -> {
            Boolean fineLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
            Boolean coarseLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
            if (fineLocationGranted != null && fineLocationGranted) {
                // Precise location access granted.
                requestLocationUpdates();
            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                // Only approximate location access granted.
                requestLocationUpdates();
            } else {
                // No location access granted.
                externalLocationCallback.onLocationResult(null);
            }
        });
    }

    /**
     * Creates a LocationRequest with high accuracy and specific update intervals.
     *
     * @return The created LocationRequest.
     */
    private LocationRequest createLocationRequest() {
        return new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMinUpdateDistanceMeters(10)
                .build();
    }

    /**
     * Creates an internal LocationCallback to handle location updates.
     *
     * @return The created LocationCallback.
     */
    private com.google.android.gms.location.LocationCallback createInternalLocationCallback() {
        return new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());
                    externalLocationCallback.onLocationResult(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        };
    }

    /**
     * Checks if the necessary location permissions are granted and requests them if not.
     */
    public void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            requestLocationUpdates();
        }
    }

    /**
     * Requests location permissions from the user.
     */
    private void requestLocationPermission() {
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    /**
     * Requests location updates from the FusedLocationProviderClient.
     */
    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, internalLocationCallback, Looper.getMainLooper());
    }

    /**
     * Stops location updates from the FusedLocationProviderClient.
     */
    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(internalLocationCallback);
    }

    /**
     * Callback interface for location results.
     */
    public interface LocationCallback {
        /**
         * Called when a new location is available.
         *
         * @param location The new location.
         */
        void onLocationResult(LatLng location);
    }
}