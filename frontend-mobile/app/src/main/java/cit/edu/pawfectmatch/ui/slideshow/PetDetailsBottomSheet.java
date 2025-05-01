package cit.edu.pawfectmatch.ui.slideshow;

import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cit.edu.pawfectmatch.R;
import cit.edu.pawfectmatch.backendstuff.Pet;
import cit.edu.pawfectmatch.backendstuff.Photo;
import cit.edu.pawfectmatch.network.UpdatePetRequest;

public class PetDetailsBottomSheet extends BottomSheetDialogFragment implements DeleteConfirmationBottomSheet.OnDeleteConfirmedListener {

    private static final String ARG_PET = "pet";
    private static final String TAG = "PetDetailsBottomSheet";
    private Pet pet;
    private PetDetailsViewModel viewModel;
    private boolean isEditMode = false;

    // View mode
    private ImageView petImage;
    private ViewPager2 photoPager;
    private TextView nameText, speciesText, breedText, birthdayText, genderText, weightText, colorText, descText, availabilityText, priceText, errorText;
    private Button editButton, deleteButton;

    // Edit mode
    private EditText nameEdit, breedEdit, weightEdit, colorEdit, descEdit, priceEdit, pedigreeEdit, healthEdit;
    private AutoCompleteTextView speciesEdit, genderEdit, availabilityEdit;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_pet_details, container, false);

        // Initialize view mode views
        petImage = view.findViewById(R.id.bspet_image);
        photoPager = view.findViewById(R.id.photo_pager);
        nameText = view.findViewById(R.id.bspet_name);
        speciesText = view.findViewById(R.id.bspet_species);
        breedText = view.findViewById(R.id.bspet_breed);
        birthdayText = view.findViewById(R.id.bspet_birthday);
        genderText = view.findViewById(R.id.bspet_gender);
        weightText = view.findViewById(R.id.bspet_weight);
        colorText = view.findViewById(R.id.bspet_color);
        descText = view.findViewById(R.id.bspet_desc);
        availabilityText = view.findViewById(R.id.bspet_availability);
        priceText = view.findViewById(R.id.bspet_price);
        errorText = view.findViewById(R.id.bspet_error);
        editButton = view.findViewById(R.id.bspet_edit);
        deleteButton = view.findViewById(R.id.bspet_delete);

        // Initialize edit mode views
        nameEdit = view.findViewById(R.id.bspet_name_edit);
        speciesEdit = view.findViewById(R.id.bspet_species_edit);
        breedEdit = view.findViewById(R.id.bspet_breed_edit);
        genderEdit = view.findViewById(R.id.bspet_gender_edit);
        weightEdit = view.findViewById(R.id.bspet_weight_edit);
        colorEdit = view.findViewById(R.id.bspet_color_edit);
        descEdit = view.findViewById(R.id.bspet_desc_edit);
        availabilityEdit = view.findViewById(R.id.bspet_availability_edit);
        priceEdit = view.findViewById(R.id.bspet_price_edit);
        pedigreeEdit = view.findViewById(R.id.bspet_pedigree_edit);
        healthEdit = view.findViewById(R.id.bspet_health_edit);
        saveButton = view.findViewById(R.id.bspet_save);
        cancelButton = view.findViewById(R.id.bspet_cancel);
        progressBar = view.findViewById(R.id.bspet_progress);

        setupSpinners();
        bindPetData();
        toggleEditMode(false);

        viewModel = new ViewModelProvider(this).get(PetDetailsViewModel.class);

        // Fetch photos with error handling
        try {
            viewModel.fetchPetPhotos(pet.getPetId());
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch photos: " + e.getMessage());
            errorText.setText("Failed to load photos");
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
        });

        // Handle update success
        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "Pet updated successfully", Toast.LENGTH_SHORT).show();
                toggleEditMode(false);
                bindPetData(); // Refresh displayed data
                // Notify PetFragment to refresh
                try {
                    ((PetFragment) getParentFragment()).refreshPets();
                } catch (Exception e) {
                    Log.e(TAG, "Failed to refresh PetFragment: " + e.getMessage());
                }
            }
        });

        // Handle delete success
        viewModel.getDeleteSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "Pet deleted successfully", Toast.LENGTH_SHORT).show();
                // Notify PetFragment to refresh
                try {
                    ((PetFragment) getParentFragment()).refreshPets();
                } catch (Exception e) {
                    Log.e(TAG, "Failed to refresh PetFragment: " + e.getMessage());
                }
                dismiss();
            }
        });

        // Button listeners
        editButton.setOnClickListener(v -> toggleEditMode(true));
        deleteButton.setOnClickListener(v -> {
            DeleteConfirmationBottomSheet confirmation = DeleteConfirmationBottomSheet.newInstance(pet.getPetId(), pet.getName());
            confirmation.show(getChildFragmentManager(), "DeleteConfirmationBottomSheet");
        });
        saveButton.setOnClickListener(v -> savePet());
        cancelButton.setOnClickListener(v -> toggleEditMode(false));

        return view;
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
        nameText.setText(pet.getName() != null ? pet.getName() : "N/A");
        speciesText.setText(pet.getSpecies() != null ? pet.getSpecies() : "N/A");
        breedText.setText(pet.getBreed() != null ? pet.getBreed() : "N/A");
        birthdayText.setText(pet.getDateOfBirth() != null ? new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(pet.getDateOfBirth()) : "N/A");
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
        genderEdit.setText(pet.getGender());
        weightEdit.setText(pet.getWeight() > 0 ? String.valueOf(pet.getWeight()) : "");
        colorEdit.setText(pet.getColor());
        descEdit.setText(pet.getDescription());
        availabilityEdit.setText(pet.getAvailabilityStatus());
        priceEdit.setText(pet.getPrice() > 0 ? String.valueOf(pet.getPrice()) : "");
        pedigreeEdit.setText(pet.getPedigreeInfo());
        healthEdit.setText(pet.getHealthStatus());
    }

    private void toggleEditMode(boolean enable) {
        isEditMode = enable;

        // View mode visibility
        petImage.setVisibility(!enable ? View.VISIBLE : View.GONE);
        photoPager.setVisibility(!enable && photoPager.getAdapter() != null ? View.VISIBLE : View.GONE);
        nameText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        speciesText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        breedText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        birthdayText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        genderText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        weightText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        colorText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        descText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        availabilityText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        priceText.setVisibility(!enable ? View.VISIBLE : View.GONE);
        editButton.setVisibility(!enable ? View.VISIBLE : View.GONE);
        deleteButton.setVisibility(!enable ? View.VISIBLE : View.GONE);

        // Edit mode visibility
        nameEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        speciesEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        breedEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        genderEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        weightEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        colorEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        descEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        availabilityEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        priceEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        pedigreeEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        healthEdit.setVisibility(enable ? View.VISIBLE : View.GONE);
        saveButton.setVisibility(enable ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    private void savePet() {
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

        viewModel.updatePet(pet.getPetId(), request);
    }

    @Override
    public void onDeleteConfirmed(String petId) {
        viewModel.deletePet(petId);
    }
}