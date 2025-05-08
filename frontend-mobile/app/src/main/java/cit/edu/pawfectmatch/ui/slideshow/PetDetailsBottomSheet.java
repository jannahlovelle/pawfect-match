package cit.edu.pawfectmatch.ui.slideshow;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import cit.edu.pawfectmatch.R;
import cit.edu.pawfectmatch.backendstuff.Pet;
import cit.edu.pawfectmatch.backendstuff.Photo;
import cit.edu.pawfectmatch.network.UpdatePetRequest;

public class PetDetailsBottomSheet extends BottomSheetDialogFragment implements DeleteConfirmationBottomSheet.OnDeleteConfirmedListener {

    private static final String TAG = "PetDetailsBottomSheet";
    private static final String ARG_PET = "pet";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private Pet pet;
    private PetDetailsViewModel viewModel;
    private boolean isEditMode = false;
    private Uri selectedPhotoUri;
    private String authToken;

    // View mode
    private ImageView petImage;
    private ViewPager2 photoPager;
    private TextView nameText, speciesText, pedigreeText, healthText ,breedText, birthdayText, genderText, weightText, colorText, descText, availabilityText, priceText, errorText;
    private TextView nameLabel, speciesLabel, breedLabel, birthdayLabel, genderLabel, weightLabel, colorLabel, descLabel, availabilityLabel, priceLabel;
    private Button editButton, deleteButton, uploadPhotoButton;

    // Edit mode
    private EditText nameEdit, breedEdit, birthdayEdit, weightEdit, colorEdit, descEdit, priceEdit, pedigreeEdit, healthEdit;
    private AutoCompleteTextView speciesEdit, genderEdit, availabilityEdit;
    private TextInputLayout speciesEditLayout, genderEditLayout, availabilityEditLayout, birthdayEditLayout;
    private TextView nameEditLabel, speciesEditLabel,pedigreeLabel, healthLabel,breedEditLabel, birthdayEditLabel, genderEditLabel, weightEditLabel, colorEditLabel, descEditLabel, availabilityEditLabel, priceEditLabel, pedigreeEditLabel, healthEditLabel;
    private Button saveButton, cancelButton;
    private ProgressBar progressBar;

    public static PetDetailsBottomSheet newInstance(Pet pet) {
        PetDetailsBottomSheet fragment = new PetDetailsBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PET, pet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pet = (Pet) getArguments().getSerializable(ARG_PET);
        }

        // Retrieve JWT token from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
        authToken = prefs.getString("jwt_token", null);
        if (authToken == null) {
            Log.e(TAG, "JWT token is null");
            Toast.makeText(requireContext(), "Authentication error. Please log in again.", Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_pet_details, container, false);

        // Initialize view mode views
        petImage = view.findViewById(R.id.bspet_image);
        photoPager = view.findViewById(R.id.photo_pager);
        nameLabel = view.findViewById(R.id.bspet_name_label);
        nameText = view.findViewById(R.id.bspet_name);
        speciesLabel = view.findViewById(R.id.bspet_species_label);
        speciesText = view.findViewById(R.id.bspet_species);
        breedLabel = view.findViewById(R.id.bspet_breed_label);
        breedText = view.findViewById(R.id.bspet_breed);
        birthdayLabel = view.findViewById(R.id.bspet_birthday_label);
        birthdayText = view.findViewById(R.id.bspet_birthday);
        genderLabel = view.findViewById(R.id.bspet_gender_label);
        genderText = view.findViewById(R.id.bspet_gender);
        weightLabel = view.findViewById(R.id.bspet_weight_label);
        weightText = view.findViewById(R.id.bspet_weight);
        colorLabel = view.findViewById(R.id.bspet_color_label);
        colorText = view.findViewById(R.id.bspet_color);
        descLabel = view.findViewById(R.id.bspet_desc_label);
        descText = view.findViewById(R.id.bspet_desc);
        availabilityLabel = view.findViewById(R.id.bspet_availability_label);
        availabilityText = view.findViewById(R.id.bspet_availability);
        priceLabel = view.findViewById(R.id.bspet_price_label);
        priceText = view.findViewById(R.id.bspet_price);
        pedigreeLabel = view.findViewById(R.id.bspet_pedigree_label);
        pedigreeText = view.findViewById(R.id.bspet_pedigree);
        healthLabel = view.findViewById(R.id.bspet_health_label);
        healthText = view.findViewById(R.id.bspet_health);
        errorText = view.findViewById(R.id.bspet_error);
        editButton = view.findViewById(R.id.bspet_edit);
        deleteButton = view.findViewById(R.id.bspet_delete);
        uploadPhotoButton = view.findViewById(R.id.bspet_upload_photo);

        // Initialize edit mode views
        nameEditLabel = view.findViewById(R.id.bspet_name_edit_label);
        nameEdit = view.findViewById(R.id.bspet_name_edit);
        speciesEditLayout = view.findViewById(R.id.bspet_species_layout);
        speciesEditLabel = view.findViewById(R.id.bspet_species_edit_label);
        speciesEdit = view.findViewById(R.id.bspet_species_edit);
        breedEditLabel = view.findViewById(R.id.bspet_breed_edit_label);
        breedEdit = view.findViewById(R.id.bspet_breed_edit);
        birthdayEditLayout = view.findViewById(R.id.bspet_birthday_edit_layout);
        birthdayEditLabel = view.findViewById(R.id.bspet_birthday_edit_label);
        birthdayEdit = view.findViewById(R.id.bspet_birthday_edit);
        genderEditLayout = view.findViewById(R.id.bspet_gender_layout);
        genderEditLabel = view.findViewById(R.id.bspet_gender_edit_label);
        genderEdit = view.findViewById(R.id.bspet_gender_edit);
        weightEditLabel = view.findViewById(R.id.bspet_weight_edit_label);
        weightEdit = view.findViewById(R.id.bspet_weight_edit);
        colorEditLabel = view.findViewById(R.id.bspet_color_edit_label);
        colorEdit = view.findViewById(R.id.bspet_color_edit);
        descEditLabel = view.findViewById(R.id.bspet_desc_edit_label);
        descEdit = view.findViewById(R.id.bspet_desc_edit);
        availabilityEditLayout = view.findViewById(R.id.bspet_availability_layout);
        availabilityEditLabel = view.findViewById(R.id.bspet_availability_edit_label);
        availabilityEdit = view.findViewById(R.id.bspet_availability_edit);
        priceEditLabel = view.findViewById(R.id.bspet_price_edit_label);
        priceEdit = view.findViewById(R.id.bspet_price_edit);
        pedigreeEditLabel = view.findViewById(R.id.bspet_pedigree_edit_label);
        pedigreeEdit = view.findViewById(R.id.bspet_pedigree_edit);
        healthEditLabel = view.findViewById(R.id.bspet_health_edit_label);
        healthEdit = view.findViewById(R.id.bspet_health_edit);
        saveButton = view.findViewById(R.id.bspet_save);
        cancelButton = view.findViewById(R.id.bspet_cancel);
        progressBar = view.findViewById(R.id.bspet_progress);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(PetDetailsViewModel.class);

        setupSpinners();
        bindPetData();
        toggleEditMode(false);

        // DatePickerDialog for birthday
        birthdayEdit.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (pet != null && pet.getDateOfBirth() != null && !pet.getDateOfBirth().isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(pet.getDateOfBirth(), DateTimeFormatter.ISO_DATE_TIME);
                    calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
                } catch (DateTimeParseException e) {
                    Log.e(TAG, "Failed to parse dateOfBirth for DatePicker: " + pet.getDateOfBirth(), e);
                }
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = String.format(Locale.US, "%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        birthdayEdit.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Upload photo button listener
        uploadPhotoButton.setOnClickListener(v -> {
            if (isEditMode) {
                String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                        Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;
                if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                } else {
                    Toast.makeText(requireContext(), "Storage permission required. Please enable it in Settings.", Toast.LENGTH_LONG).show();
                    Intent settingsIntent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    settingsIntent.setData(Uri.parse("package:" + requireContext().getPackageName()));
                    startActivity(settingsIntent);
                }
            } else {
                Toast.makeText(requireContext(), "Enter edit mode to upload a photo", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch photos with error handling
        if (pet != null && viewModel != null && authToken != null) {
            try {
                viewModel.fetchPetPhotos(authToken, pet.getPetId());
            } catch (Exception e) {
                Log.e(TAG, "Failed to fetch photos: " + e.getMessage());
                errorText.setText("Failed to load photos");
                errorText.setVisibility(View.VISIBLE);
            }
        } else {
            Log.e(TAG, "Cannot fetch photos: pet, viewModel, or authToken is null");
            errorText.setText("Failed to load pet data");
            errorText.setVisibility(View.VISIBLE);
        }

        viewModel.getPhotos().observe(getViewLifecycleOwner(), photos -> {
            if (photos != null && !photos.isEmpty()) {
                try {
                    PhotoPagerAdapter adapter = new PhotoPagerAdapter(photos);
                    photoPager.setAdapter(adapter);
                    photoPager.setVisibility(View.VISIBLE);
                    petImage.setVisibility(View.GONE);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to set photo adapter: " + e.getMessage());
                    errorText.setText("Failed to display photos");
                    errorText.setVisibility(View.VISIBLE);
                }
            } else {
                photoPager.setVisibility(View.GONE);
                petImage.setVisibility(View.VISIBLE);
                Glide.with(this).load(R.drawable.defaultprofile).into(petImage);
            }
        });

        // Handle errors
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                errorText.setText(error);
                errorText.setVisibility(View.VISIBLE);
            } else {
                errorText.setVisibility(View.GONE);
            }
        });

        // Handle loading
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            saveButton.setEnabled(!isLoading);
            uploadPhotoButton.setEnabled(!isLoading && isEditMode);
        });

        // Handle update success
        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "Pet updated successfully", Toast.LENGTH_SHORT).show();
                toggleEditMode(false);
                bindPetData();
                // Notify PetFragment to refresh via navigation
                try {
                    NavController navController = NavHostFragment.findNavController(PetDetailsBottomSheet.this);
                    navController.getCurrentBackStackEntry()
                            .getSavedStateHandle()
                            .set("refresh_pets", true);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to notify PetFragment: " + e.getMessage());
                }
                // Clear selected photo after successful update
                selectedPhotoUri = null;
            }
        });

        // Handle delete success
        viewModel.getDeleteSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "Pet deleted successfully", Toast.LENGTH_SHORT).show();
                // Notify PetFragment to refresh via navigation
                try {
                    NavController navController = NavHostFragment.findNavController(PetDetailsBottomSheet.this);
                    navController.getCurrentBackStackEntry()
                            .getSavedStateHandle()
                            .set("refresh_pets", true);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to notify PetFragment: " + e.getMessage());
                }
                dismiss();
            }
        });

        // Handle photo upload success
        viewModel.getPhotoUploadSuccess().observe(getViewLifecycleOwner(), photoUrl -> {
            if (photoUrl != null) {
                Toast.makeText(requireContext(), "Photo uploaded successfully", Toast.LENGTH_SHORT).show();
                if (pet != null && viewModel != null && authToken != null) {
                    viewModel.fetchPetPhotos(authToken, pet.getPetId());
                }
            }
        });

        // Button listeners
        editButton.setOnClickListener(v -> toggleEditMode(true));
        deleteButton.setOnClickListener(v -> {
            if (pet != null) {
                DeleteConfirmationBottomSheet confirmation = DeleteConfirmationBottomSheet.newInstance(pet.getPetId(), pet.getName());
                confirmation.setOnDeleteConfirmedListener(this); // Set listener explicitly
                confirmation.show(getChildFragmentManager(), "DeleteConfirmationBottomSheet");
            }
        });
        saveButton.setOnClickListener(v -> savePet());
        cancelButton.setOnClickListener(v -> {
            selectedPhotoUri = null;
            toggleEditMode(false);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.getData();
            try {
                // Validate MIME type and file size
                ContentResolver contentResolver = requireContext().getContentResolver();
                String mimeType = contentResolver.getType(selectedPhotoUri);
                if (mimeType == null || (!mimeType.equals("image/jpeg") && !mimeType.equals("image/png"))) {
                    selectedPhotoUri = null;
                    Toast.makeText(requireContext(), "Please select a JPEG or PNG image", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check file size and integrity
                long size = 0;
                try (InputStream inputStream = contentResolver.openInputStream(selectedPhotoUri)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        size += bytesRead;
                        if (size > MAX_FILE_SIZE) {
                            selectedPhotoUri = null;
                            Toast.makeText(requireContext(), "File too large. Maximum size is 5MB.", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
                if (size == 0) {
                    selectedPhotoUri = null;
                    Toast.makeText(requireContext(), "Selected file is empty", Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d(TAG, "Selected photo: URI=" + selectedPhotoUri + ", MIME=" + mimeType + ", size=" + size + " bytes");
                petImage.setImageURI(selectedPhotoUri);
                Toast.makeText(requireContext(), "Photo selected. Save to upload.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error loading image: " + e.getMessage());
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                selectedPhotoUri = null;
            }
        }
    }

    private void setupSpinners() {
        String[] speciesOptions = {"Dog", "Cat", "Bird", "Other"};
        ArrayAdapter<String> speciesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, speciesOptions);
        speciesEdit.setAdapter(speciesAdapter);

        String[] genderOptions = {"Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, genderOptions);
        genderEdit.setAdapter(genderAdapter);

        String[] availabilityOptions = {"available", "reserved", "sold"};
        ArrayAdapter<String> availabilityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, availabilityOptions);
        availabilityEdit.setAdapter(availabilityAdapter);
    }

    private void bindPetData() {
        if (pet == null) {
            Log.e(TAG, "Pet is null in bindPetData");
            return;
        }
        nameText.setText(pet.getName() != null ? pet.getName() : "N/A");
        speciesText.setText(pet.getSpecies() != null ? pet.getSpecies() : "N/A");
        breedText.setText(pet.getBreed() != null ? pet.getBreed() : "N/A");
        birthdayText.setText(pet.getDateOfBirth() != null && !pet.getDateOfBirth().isEmpty() ?
                formatDateForDisplay(pet.getDateOfBirth()) : "N/A");
        genderText.setText(pet.getGender() != null ? pet.getGender() : "N/A");
        weightText.setText(pet.getWeight() > 0 ? String.format(Locale.US, "%.2f kg", pet.getWeight()) : "N/A");
        colorText.setText(pet.getColor() != null ? pet.getColor() : "N/A");
        descText.setText(pet.getDescription() != null ? pet.getDescription() : "N/A");
        availabilityText.setText(pet.getAvailabilityStatus() != null ? pet.getAvailabilityStatus() : "N/A");
        priceText.setText(pet.getPrice() > 0 ? String.format(Locale.US, "$%.2f", pet.getPrice()) : "N/A");

        // Pre-fill edit fields
        nameEdit.setText(pet.getName());
        speciesEdit.setText(pet.getSpecies());
        breedEdit.setText(pet.getBreed());
        birthdayEdit.setText(pet.getDateOfBirth() != null && !pet.getDateOfBirth().isEmpty() ?
                formatDateForDisplay(pet.getDateOfBirth()) : "");
        genderEdit.setText(pet.getGender());
        weightEdit.setText(pet.getWeight() > 0 ? String.valueOf(pet.getWeight()) : "");
        colorEdit.setText(pet.getColor());
        descEdit.setText(pet.getDescription());
        availabilityEdit.setText(pet.getAvailabilityStatus());
        priceEdit.setText(pet.getPrice() > 0 ? String.valueOf(pet.getPrice()) : "");
        pedigreeEdit.setText(pet.getPedigreeInfo());
        healthEdit.setText(pet.getHealthStatus());
    }

    private String formatDateForDisplay(String dateStr) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
            return dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            Log.e(TAG, "Failed to parse date for display: " + dateStr, e);
            return dateStr; // Fallback to original string
        }
    }

    private void toggleEditMode(boolean enable) {
        isEditMode = enable;

        // View mode visibility
        petImage.setVisibility(!enable ? View.VISIBLE : View.VISIBLE);
        photoPager.setVisibility(!enable && photoPager.getAdapter() != null ? View.VISIBLE : View.GONE);
        uploadPhotoButton.setVisibility(View.VISIBLE);
        uploadPhotoButton.setEnabled(enable);
        nameLabel.setVisibility(!enable ? View.VISIBLE : View.GONE);
        nameText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        speciesLabel.setVisibility(!enable ? View.VISIBLE : View.GONE);
        speciesText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        breedLabel.setVisibility(!enable ? View.VISIBLE : View.GONE);
        breedText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        birthdayLabel.setVisibility(!enable ? View.VISIBLE : View.GONE);
        birthdayText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        genderLabel.setVisibility(!enable ? View.VISIBLE : View.GONE);
        genderText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        weightLabel.setVisibility(!enable ? View.VISIBLE : View.GONE);
        weightText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        colorLabel.setVisibility(!enable ? View.VISIBLE : View.GONE);
        colorText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        descLabel.setVisibility(!enable ? View.VISIBLE : View.GONE);
        descText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        availabilityLabel.setVisibility(!enable ? View.VISIBLE : View.GONE);
        availabilityText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        priceLabel.setVisibility(!enable ? View.VISIBLE : View.GONE);
        priceText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        pedigreeLabel.setVisibility(!enable ? View.VISIBLE : View.GONE);
        pedigreeText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        healthLabel.setVisibility(!enable ? View.VISIBLE : View.GONE);
        healthText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        editButton.setVisibility(!enable ? View.VISIBLE : View.GONE);
        deleteButton.setVisibility(!enable ? View.VISIBLE : View.GONE);

        // Edit mode visibility
        nameEditLabel.setVisibility(enable ? View.VISIBLE : View.GONE);
        nameEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        speciesEditLayout.setVisibility(enable ? View.VISIBLE : View.GONE);
        speciesEditLabel.setVisibility(enable ? View.VISIBLE : View.GONE);
        speciesEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        breedEditLabel.setVisibility(enable ? View.VISIBLE : View.GONE);
        breedEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        birthdayEditLabel.setVisibility(enable ? View.VISIBLE : View.GONE);
        birthdayEditLayout.setVisibility(enable ? View.VISIBLE : View.GONE);
        birthdayEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        genderEditLayout.setVisibility(enable ? View.VISIBLE : View.GONE);
        genderEditLabel.setVisibility(enable ? View.VISIBLE : View.GONE);
        genderEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        weightEditLabel.setVisibility(enable ? View.VISIBLE : View.GONE);
        weightEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        colorEditLabel.setVisibility(enable ? View.VISIBLE : View.GONE);
        colorEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        descEditLabel.setVisibility(enable ? View.VISIBLE : View.GONE);
        descEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        availabilityEditLayout.setVisibility(enable ? View.VISIBLE : View.GONE);
        availabilityEditLabel.setVisibility(enable ? View.VISIBLE : View.GONE);
        availabilityEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        priceEditLabel.setVisibility(enable ? View.VISIBLE : View.GONE);
        priceEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        pedigreeEditLabel.setVisibility(enable ? View.VISIBLE : View.GONE);
        pedigreeEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        healthEditLabel.setVisibility(enable ? View.VISIBLE : View.GONE);
        healthEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        saveButton.setVisibility(enable ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(enable ? View.VISIBLE : View.GONE);

        // Reset selected photo and reload photos when exiting edit mode
        if (!enable) {
            selectedPhotoUri = null;
            if (pet != null && viewModel != null && authToken != null) {
                viewModel.fetchPetPhotos(authToken, pet.getPetId());
            }
        }
    }

    private void savePet() {
        if (pet == null) {
            errorText.setText("Pet data is missing");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        if (authToken == null) {
            errorText.setText("Authentication error. Please log in again.");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        String name = nameEdit.getText() != null ? nameEdit.getText().toString().trim() : "";
        String species = speciesEdit.getText() != null ? speciesEdit.getText().toString().trim() : "";
        String gender = genderEdit.getText() != null ? genderEdit.getText().toString().trim() : "";

        if (name.isEmpty()) {
            errorText.setText("Name is required");
            errorText.setVisibility(View.VISIBLE);
            return;
        }
        if (species.isEmpty()) {
            errorText.setText("Species is required");
            errorText.setVisibility(View.VISIBLE);
            return;
        }
        if (gender.isEmpty()) {
            errorText.setText("Gender is required");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        UpdatePetRequest request = new UpdatePetRequest();
        String birthday = birthdayEdit.getText() != null ? birthdayEdit.getText().toString().trim() : "";
        String dateOfBirth = null;
        if (!birthday.isEmpty()) {
            try {
                LocalDate localDate = LocalDate.parse(birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                dateOfBirth = localDate.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                errorText.setText("Invalid birthday format (use YYYY-MM-DD)");
                errorText.setVisibility(View.VISIBLE);
                return;
            }
        }
        request.setDateOfBirth(dateOfBirth);
        request.setName(name);
        request.setSpecies(species);
        request.setBreed(breedEdit.getText() != null ? breedEdit.getText().toString().trim() : null);
        request.setGender(gender);
        request.setWeight(weightEdit.getText() != null && !weightEdit.getText().toString().isEmpty() ? Double.parseDouble(weightEdit.getText().toString()) : null);
        request.setColor(colorEdit.getText() != null ? colorEdit.getText().toString().trim() : null);
        request.setDescription(descEdit.getText() != null ? descEdit.getText().toString().trim() : null);
        request.setAvailabilityStatus(availabilityEdit.getText() != null ? availabilityEdit.getText().toString().trim() : null);
        request.setPrice(priceEdit.getText() != null && !priceEdit.getText().toString().isEmpty() ? Double.parseDouble(priceEdit.getText().toString()) : null);
        request.setPedigreeInfo(pedigreeEdit.getText() != null ? pedigreeEdit.getText().toString().trim() : null);
        request.setHealthStatus(healthEdit.getText() != null ? healthEdit.getText().toString().trim() : null);

        // Update pet and upload photo if selected
        if (viewModel != null) {
            viewModel.updatePet(authToken, pet.getPetId(), request);

            if (selectedPhotoUri != null) {
                File photoFile = uriToFile(selectedPhotoUri);
                if (photoFile != null && photoFile.exists() && photoFile.length() > 0) {
                    // Log file details
                    Log.d(TAG, "Uploading file: " + photoFile.getAbsolutePath() + ", size: " + photoFile.length() + " bytes");
                    try {
                        byte[] fileHeader = new byte[10];
                        try (java.io.FileInputStream fis = new java.io.FileInputStream(photoFile)) {
                            fis.read(fileHeader);
                        }
                        Log.d(TAG, "File header: " + Arrays.toString(fileHeader));
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to read file header: " + e.getMessage());
                    }
                    viewModel.uploadPetPhoto(authToken, pet.getPetId(), photoFile);
                } else {
                    errorText.setText("Failed to process selected photo: " + (photoFile == null ? "null file" : "empty or invalid file"));
                    errorText.setVisibility(View.VISIBLE);
                }
            }
        } else {
            errorText.setText("Failed to save: ViewModel not initialized");
            errorText.setVisibility(View.VISIBLE);
        }
    }

    private File uriToFile(Uri uri) {
        try {
            ContentResolver contentResolver = requireContext().getContentResolver();
            String mimeType = contentResolver.getType(uri);
            if (mimeType == null || (!mimeType.equals("image/jpeg") && !mimeType.equals("image/png"))) {
                Log.e(TAG, "Invalid file type: " + mimeType);
                return null;
            }

            String extension = mimeType.equals("image/jpeg") ? ".jpg" : ".png";
            File file = new File(requireContext().getCacheDir(), "pet_photo_" + System.currentTimeMillis() + extension);
            try (InputStream inputStream = contentResolver.openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            Log.d(TAG, "Created file: " + file.getAbsolutePath() + ", MIME: " + mimeType + ", size: " + file.length() + " bytes");
            return file;
        } catch (Exception e) {
            Log.e(TAG, "Failed to convert Uri to File: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void onDeleteConfirmed(String petId) {
        if (viewModel != null && authToken != null) {
            viewModel.deletePet(authToken, petId);
        } else {
            Log.e(TAG, "Cannot delete pet: ViewModel or authToken is null");
            Toast.makeText(requireContext(), "Failed to delete pet", Toast.LENGTH_SHORT).show();
        }
    }
}