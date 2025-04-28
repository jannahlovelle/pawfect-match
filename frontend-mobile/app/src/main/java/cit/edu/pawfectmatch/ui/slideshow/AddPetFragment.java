package cit.edu.pawfectmatch.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import cit.edu.pawfectmatch.R;
import cit.edu.pawfectmatch.network.CreatePetRequest;
import cit.edu.pawfectmatch.ui.slideshow.PetViewModel;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddPetFragment extends Fragment {
    private PetViewModel petViewModel;
    private TextInputLayout inputLayoutName, inputLayoutSpecies, inputLayoutGender, inputLayoutBreed, inputLayoutWeight, inputLayoutDescription;
    private TextInputEditText editTextName, editTextBreed, editTextWeight, editTextDescription;
    private AutoCompleteTextView spinnerSpecies, spinnerGender;
    private MaterialButton buttonSubmit;
    private ProgressBar progressBar;
    private TextView errorTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_pet, container, false);

        // Initialize TextInputLayouts
        inputLayoutName = view.findViewById(R.id.input_layout_name);
        inputLayoutSpecies = view.findViewById(R.id.input_layout_species);
        inputLayoutGender = view.findViewById(R.id.input_layout_gender);
        inputLayoutBreed = view.findViewById(R.id.input_layout_breed);
        inputLayoutWeight = view.findViewById(R.id.input_layout_weight);
        inputLayoutDescription = view.findViewById(R.id.input_layout_description);

        // Initialize TextInputEditTexts
        editTextName = view.findViewById(R.id.edit_text_name);
        editTextBreed = view.findViewById(R.id.edit_text_breed);
        editTextWeight = view.findViewById(R.id.edit_text_weight);
        editTextDescription = view.findViewById(R.id.edit_text_description);

        // Initialize AutoCompleteTextViews
        spinnerSpecies = view.findViewById(R.id.spinner_species);
        spinnerGender = view.findViewById(R.id.spinner_gender);

        // Initialize other views
        buttonSubmit = view.findViewById(R.id.button_submit);
        progressBar = view.findViewById(R.id.progress_bar);
        errorTextView = view.findViewById(R.id.error_text_view);

        setupSpinners();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        petViewModel = new ViewModelProvider(this).get(PetViewModel.class);

        buttonSubmit.setOnClickListener(v -> submitPet());

        petViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            buttonSubmit.setEnabled(!isLoading);
        });

        petViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                errorTextView.setText(error);
                errorTextView.setVisibility(View.VISIBLE);
            } else {
                errorTextView.setVisibility(View.GONE);
            }
        });

        petViewModel.getCreateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                // Navigate back or to PetFragment
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void setupSpinners() {
        String[] speciesOptions = {"Dog", "Cat", "Bird", "Other"};
        ArrayAdapter<String> speciesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, speciesOptions);
        spinnerSpecies.setAdapter(speciesAdapter);

        String[] genderOptions = {"Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, genderOptions);
        spinnerGender.setAdapter(genderAdapter);
    }

    private void submitPet() {
        // Reset errors
        inputLayoutName.setError(null);
        inputLayoutSpecies.setError(null);
        inputLayoutGender.setError(null);

        String name = editTextName.getText() != null ? editTextName.getText().toString().trim() : "";
        String species = spinnerSpecies.getText() != null ? spinnerSpecies.getText().toString().trim() : "";
        String gender = spinnerGender.getText() != null ? spinnerGender.getText().toString().trim() : "";
        String breed = editTextBreed.getText() != null ? editTextBreed.getText().toString().trim() : "";
        String weightStr = editTextWeight.getText() != null ? editTextWeight.getText().toString().trim() : "";
        String description = editTextDescription.getText() != null ? editTextDescription.getText().toString().trim() : "";

        // Validate required fields
        boolean isValid = true;
        if (name.isEmpty()) {
            inputLayoutName.setError("Name is required");
            isValid = false;
        }
        if (species.isEmpty()) {
            inputLayoutSpecies.setError("Species is required");
            isValid = false;
        }
        if (gender.isEmpty()) {
            inputLayoutGender.setError("Gender is required");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        CreatePetRequest petRequest = new CreatePetRequest();
        petRequest.setName(name);
        petRequest.setSpecies(species);
        petRequest.setGender(gender);
        petRequest.setBreed(breed.isEmpty() ? null : breed);
        petRequest.setWeight(weightStr.isEmpty() ? 0.0 : Double.parseDouble(weightStr));
        petRequest.setDescription(description.isEmpty() ? null : description);

        petViewModel.createPet(petRequest);
    }
}