package ch.y.bitite.safespot.ui.addreport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private TextView textViewCurrentAddress;
    private EditText editTextStreet;
    private EditText editTextNumber;
    private EditText editTextPostalCode;
    private EditText editTextCity;
    private LinearLayout manualAddressLayout;
    private RadioGroup locationRadioGroup;
    private RadioButton radioCurrentLocation;
    private RadioButton radioManualAddress;
    private LocationHelper locationHelper;
    private static final String TAG = "AddReportFragment";
    private boolean isLocationRequested = false;
    private static final String KEY_DESCRIPTION = "description";

    private ImageView imageView;
    private Uri selectedImageUri = null;
    private static final int REQUEST_IMAGE_PICK = 100;
    private FragmentAddReportBinding binding;
    private AddReportViewModel addReportViewModel;
    private boolean isCurrentLocationSelected = true;
    // New variables for voice memo
    private RadioGroup descriptionRadioGroup;
    private RadioButton radioTextDescription;
    private RadioButton radioVoiceDescription;
    private LinearLayout voiceMemoLayout;
    private Button buttonRecord;
    private TextView textViewRecordingStatus;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;
    private boolean isVoiceDescriptionSelected = false;

    // New variables for date and time
    private TextView textViewDateTime;
    private Calendar selectedDateTime = Calendar.getInstance();

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddReportBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        editTextDescription = binding.editTextDescription;
        textViewCurrentAddress = binding.textViewCurrentAddress;
        editTextStreet = binding.editTextStreet;
        editTextNumber = binding.editTextNumber;
        editTextPostalCode = binding.editTextPostalCode;
        editTextCity = binding.editTextCity;
        manualAddressLayout = binding.manualAddressLayout;
        locationRadioGroup = binding.locationRadioGroup;
        radioCurrentLocation = binding.radioCurrentLocation;
        radioManualAddress = binding.radioManualAddress;
        textViewDateTime = binding.textViewDateTime;

        locationHelper = new LocationHelper(this, this);
        AddReportButtonHelper buttonHelper = new AddReportButtonHelper(view, this);
        buttonHelper.setupAddReportButtonListeners();
        imageView = binding.imageView;
        ImageView imageAddImage = binding.imageView;
        imageAddImage.setOnClickListener(v -> checkPermissionAndOpenGallery());

        if (savedInstanceState != null) {
            editTextDescription.setText(savedInstanceState.getString(KEY_DESCRIPTION));
        }

        // RadioGroup listener
        locationRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioCurrentLocation) {
                onCurrentLocationSelected();
            } else if (checkedId == R.id.radioManualAddress) {
                onManualAddressSelected();
            }
        });

        // New code for voice memo
        descriptionRadioGroup = binding.descriptionRadioGroup;
        radioTextDescription = binding.radioTextDescription;
        radioVoiceDescription = binding.radioVoiceDescription;
        voiceMemoLayout = binding.voiceMemoLayout;
        buttonRecord = binding.buttonRecord;
        textViewRecordingStatus = binding.textViewRecordingStatus;

        descriptionRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioTextDescription) {
                onTextDescriptionSelected();
            } else if (checkedId == R.id.radioVoiceDescription) {
                onVoiceDescriptionSelected();
            }
        });

        buttonRecord.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });

        // Date and time
        updateDateTimeTextView();
        textViewDateTime.setOnClickListener(v -> showDateTimePicker());

        return view;
    }

    /**
     * Called immediately after onCreateView() has returned.
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
     * Called to ask the fragment to save its current dynamic state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_DESCRIPTION, editTextDescription.getText().toString());
    }

    /**
     * Called when a new location is available.
     */
    @Override
    public void onLocationResult(LatLng location) {
        if (location != null) {
            addReportViewModel.setLocation(location);
            Log.d(TAG, "Latitude: " + location.latitude + " Longitude: " + location.longitude);
            if (isCurrentLocationSelected) {
                updateCurrentAddressTextView(location);
            }
        } else {
            addReportViewModel.setLocation(new LatLng(0, 0));
            // Location is null, handle this case (e.g., display a message)
            Toast.makeText(getContext(), R.string.msg_enable_location_services, Toast.LENGTH_LONG).show();
            Log.w(TAG, "Location is null");
        }
    }

    /**
     * Called when the "Current Location" radio button is selected.
     */
    private void onCurrentLocationSelected() {
        manualAddressLayout.setVisibility(View.GONE);
        textViewCurrentAddress.setVisibility(View.VISIBLE);
        isCurrentLocationSelected = true;
        locationHelper.checkLocationPermissions();
    }

    /**
     * Called when the "Manual Address" radio button is selected.
     */
    private void onManualAddressSelected() {
        manualAddressLayout.setVisibility(View.VISIBLE);
        textViewCurrentAddress.setVisibility(View.GONE);
        isCurrentLocationSelected = false;
    }

    /**
     * Checks if the app has permission to accessthe gallery and opens it if it does.
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
        String description = "";
        if (isVoiceDescriptionSelected) {
            if (audioFilePath == null) {
                Toast.makeText(getContext(), "Please record a voice memo", Toast.LENGTH_SHORT).show();
                return;
            }
            addReportViewModel.setAudioFilePath(audioFilePath);
        } else {
            description = editTextDescription.getText().toString();
            if (description.isEmpty()) {
                Toast.makeText(getContext(), "Please fill the description field", Toast.LENGTH_SHORT).show();
                return;
            }
            addReportViewModel.setDescription(description);
        }

//        if (selectedImageUri == null) {
//            Toast.makeText(getContext(), "Please upload a picture", Toast.LENGTH_SHORT).show();
//            return;
//        }

        LatLng location;
        if (radioCurrentLocation.isChecked()) {
            if (addReportViewModel.getLocation().getValue() == null) {
                // Location is null, handle this case (e.g., display a message)
                Toast.makeText(getContext(), R.string.msg_enable_location_services, Toast.LENGTH_LONG).show();
                return;
            }
            location = addReportViewModel.getLocation().getValue();
        } else {
            String street = editTextStreet.getText().toString();
            String number = editTextNumber.getText().toString();
            String postalCode = editTextPostalCode.getText().toString();
            String city = editTextCity.getText().toString();

            if (street.isEmpty() || number.isEmpty() || postalCode.isEmpty() || city.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all address fields", Toast.LENGTH_SHORT).show();
                return;
            }
            location = getLocationFromAddress(street, number, postalCode, city);
            if (location == null) {
                Toast.makeText(getContext(), "Invalid address", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Date dateTime = selectedDateTime.getTime();
        addReportViewModel.setDateTime(dateTime);
        addReportViewModel.setImageUri(selectedImageUri);
        addReportViewModel.setLocation(location);
        addReportViewModel.addReport();

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_addReportFragement_to_homeFragment);
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
        binding = null;
    }

    /**
     * Gets the LatLng from the given address.
     */
    private LatLng getLocationFromAddress(String street, String number, String postalCode, String city) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(street + " " + number + ", " + postalCode + " " + city, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error getting location from address", e);
        }
        return null;
    }

    /**
     * Updates the current address TextView with the given location.
     */
    private void updateCurrentAddressTextView(LatLng location) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressLine = address.getAddressLine(0); // Full address
                textViewCurrentAddress.setText(addressLine);
            } else {
                textViewCurrentAddress.setText(R.string.location_unavailable);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error getting address from location", e);
            textViewCurrentAddress.setText(R.string.location_unavailable);
        }
    }

    // New methods for voice memo
    private void onTextDescriptionSelected() {
        editTextDescription.setVisibility(View.VISIBLE);
        voiceMemoLayout.setVisibility(View.GONE);
        isVoiceDescriptionSelected = false;
    }

    private void onVoiceDescriptionSelected() {
        editTextDescription.setVisibility(View.GONE);
        voiceMemoLayout.setVisibility(View.VISIBLE);
        isVoiceDescriptionSelected = true;
    }

    private void startRecording() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // La permission n'est pas accordée, on la demande
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            // La permission est déjà accordée, on peut commencer l'enregistrement
            try {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mediaRecorder.setOutputFile(getOutputFilePath());
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;
                buttonRecord.setText(R.string.stop);
                textViewRecordingStatus.setText(R.string.recording);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            buttonRecord.setText(R.string.record);
            textViewRecordingStatus.setText(R.string.not_recording);
        }
    }

    private String getOutputFilePath() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "audio_" + timeStamp + ".3gp";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File audioFile = new File(storageDir, fileName);
        audioFilePath = audioFile.getAbsolutePath();
        return audioFilePath;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // La permission a été accordée, on peut commencer l'enregistrement
                startRecording();
            } else {
                // La permission a été refusée, on affiche un message à l'utilisateur
                Toast.makeText(getContext(), R.string.permission_denied_record_audio, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_IMAGE_PICK) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // La permission a été accordée, on peut ouvrir la galerie
                openGallery();
            } else {
                // La permission a été refusée, on affiche un message à l'utilisateur
                Toast.makeText(getContext(), R.string.permission_denied_read_media_images, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateDateTimeTextView() {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        textViewDateTime.setText(dateTimeFormat.format(selectedDateTime.getTime()));
    }

    private void showDateTimePicker() {
        // Date Picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDateTime.set(year, monthOfYear, dayOfMonth);
                    showTimePicker();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        // Time Picker
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    updateDateTimeTextView();
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true // 24-hour format
        );
        timePickerDialog.show();
    }
}