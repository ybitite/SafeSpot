package ch.y.bitite.safespot.ui.addreport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.model.LatLng;

import java.time.Duration;
import java.util.Objects;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.databinding.FragmentAddReportBinding;
import ch.y.bitite.safespot.utils.buttonhelper.AddReportButtonHelper;
import ch.y.bitite.safespot.utils.LocationHelper;
import ch.y.bitite.safespot.viewmodel.AddReportViewModel;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for adding a new report.
 * This fragment allows the user to enter a description, select an image, and submit a new report.
 */
@AndroidEntryPoint
public class AddReportFragment extends Fragment implements LocationHelper.LocationCallback, AddReportButtonHelper.AddReportButtonCallback {

    private EditText editTextDescription;
    private TextView textViewLatitude;
    private TextView textViewLongitude;
    AddReportViewModel addReportViewModel;
    private LocationHelper locationHelper;
    private static final String TAG = "AddReportFragment";
    private boolean isLocationRequested = false;
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    private ImageView imageView;
    private Uri selectedImageUri = null;
    private static final int REQUEST_IMAGE_PICK = 100;
    private FragmentAddReportBinding binding;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddReportBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        editTextDescription = binding.editTextDescription;
        textViewLatitude = binding.textViewLatitude;
        textViewLongitude = binding.textViewLongitude;
        // Remove this line
        // progressBar = binding.progressBar;
        locationHelper = new LocationHelper(this, this);
        AddReportButtonHelper buttonHelper = new AddReportButtonHelper(view, this);
        buttonHelper.setupAddReportButtonListeners();
        imageView = binding.imageView;
        Button buttonAddImage = binding.buttonAddImage;
        buttonAddImage.setOnClickListener(v -> checkPermissionAndOpenGallery());
        if (savedInstanceState != null) {
            editTextDescription.setText(savedInstanceState.getString(KEY_DESCRIPTION));
            textViewLatitude.setText(savedInstanceState.getString(KEY_LATITUDE));
            textViewLongitude.setText(savedInstanceState.getString(KEY_LONGITUDE));
        }
        return view;
    }

    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view.
     *
     * @param view               The View returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addReportViewModel = new ViewModelProvider(requireActivity()).get(AddReportViewModel.class);

        if (!isLocationRequested) {
            // Request a fresh, accurate location
            locationHelper.checkLocationPermissions();
            isLocationRequested = true;
        }
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it can later be reconstructed in a new instance of its process is restarted.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_DESCRIPTION, editTextDescription.getText().toString());
        outState.putString(KEY_LATITUDE, textViewLatitude.getText().toString());
        outState.putString(KEY_LONGITUDE, textViewLongitude.getText().toString());
    }

    /**
     * Called when a new location is available.
     *
     * @param location The new location.
     */
    @Override
    public void onLocationResult(LatLng location) {
        if (location != null) {
            addReportViewModel.setLocation(location);
            Log.d(TAG, "Latitude: " + location.latitude + " Longitude: " + location.longitude);
            updateLocationTextViews(location);
        } else {
            addReportViewModel.setLocation(new LatLng(0, 0));
            // Location is null, handle this case (e.g., display a message)
            textViewLatitude.setText(R.string.location_unavailable);
            textViewLongitude.setText(R.string.location_unavailable);
            Toast.makeText(getContext(), R.string.msg_enable_location_services, Toast.LENGTH_LONG).show();
            Log.w(TAG, "Location is null");
        }
    }

    /**
     * Checks if the app has permission to access the gallery and opens it if it does.
     */
    private void checkPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_IMAGE_PICK);
        } else {
            openGallery();
        }
    }

    /**
     * Opens the gallery to select an image.
     */
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        imagePickerLauncher.launch(galleryIntent);
    }


    /**
     * Adds a new report.
     */
    private void addReport() {
        String description = editTextDescription.getText().toString();

        if (description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill the description field", Toast.LENGTH_SHORT).show();
            return;
        }
        if (addReportViewModel.getLocation().getValue() == null) {
            // Location is null, handle this case (e.g., display a message)
            textViewLatitude.setText(R.string.location_unavailable);
            textViewLongitude.setText(R.string.location_unavailable);
            Toast.makeText(getContext(), R.string.msg_enable_location_services, Toast.LENGTH_LONG).show();
            return;
        }
        if (selectedImageUri == null) {
            Toast.makeText(getContext(), "Please upload a picketer", Toast.LENGTH_SHORT).show();
            return;
        }

        addReportViewModel.setDescription(description);
        addReportViewModel.setImageUri(selectedImageUri); // Pass the image URI to the ViewModel

        addReportViewModel.addReport();

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_addReportFragement_to_homeFragment);
    }
    /**
     * Updates the latitude and longitude TextViews with the given location.
     *
     * @param location The location to display.
     */
    private void updateLocationTextViews(LatLng location) {
        textViewLatitude.setText("La : " + location.latitude);
        textViewLongitude.setText("Lo : " + location.longitude);
    }

    /**
     * ActivityResultLauncher for picking an image.
     */
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                selectedImageUri = data.getData();
                                imageView.setImageURI(selectedImageUri);
                            }
                        }
                    });
    /**
     * Called when the add report button is clicked.
     */
    @Override
    public void onAddReportClicked() {
        addReport();
    }

    /**
     * Called when the cancel button is clicked.
     */
    @Override
    public void onCancelClicked() {
        getParentFragmentManager().popBackStack();
    }

    /**
     * Called when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationHelper.stopLocationUpdates();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        binding = null;
    }
}