package cit.edu.pawfectmatch.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import cit.edu.pawfectmatch.R;

public class PetFragment extends Fragment {
    private PetViewModel petViewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private PetAdapter petAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pet, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_pets);
        progressBar = view.findViewById(R.id.progress_bar);
        errorTextView = view.findViewById(R.id.error_text_view);
        FloatingActionButton fab = view.findViewById(R.id.fab_add_pet);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        petAdapter = new PetAdapter(pet -> {
            PetDetailsBottomSheet bottomSheet = PetDetailsBottomSheet.newInstance(pet);
            bottomSheet.show(getParentFragmentManager(), "PetDetailsBottomSheet");
        });
        recyclerView.setAdapter(petAdapter);

        fab.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_addPetFragment);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        petViewModel = new ViewModelProvider(this).get(PetViewModel.class);

        petViewModel.getPets().observe(getViewLifecycleOwner(), pets -> {
            if (pets != null && !pets.isEmpty()) {
                petAdapter.setPets(pets);
                recyclerView.setVisibility(View.VISIBLE);
                errorTextView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText("No pets found.");
            }
        });

        petViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        petViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                recyclerView.setVisibility(View.GONE);
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText(error);
            }
        });

        petViewModel.fetchMyPets();
    }

    public void refreshPets() {
        petViewModel.fetchMyPets();
    }
}