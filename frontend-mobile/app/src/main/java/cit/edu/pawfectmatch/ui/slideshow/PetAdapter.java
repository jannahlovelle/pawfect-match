package cit.edu.pawfectmatch.ui.slideshow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cit.edu.pawfectmatch.R;
import cit.edu.pawfectmatch.backendstuff.Pet;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
    private List<Pet> pets = new ArrayList<>();
    private final OnPetClickListener onPetClickListener;

    public interface OnPetClickListener {
        void onPetClick(Pet pet);
    }

    public PetAdapter(OnPetClickListener listener) {
        this.onPetClickListener = listener;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = pets.get(position);
        holder.nameTextView.setText(pet.getName() != null ? pet.getName() : "N/A");
        holder.speciesTextView.setText(pet.getSpecies() != null ? pet.getSpecies() : "N/A");
        holder.genderTextView.setText(pet.getGender() != null ? pet.getGender() : "N/A");
        holder.statusTextView.setText(pet.getAvailabilityStatus() != null ? pet.getAvailabilityStatus() : "N/A");

        holder.itemView.setOnClickListener(v -> onPetClickListener.onPetClick(pet));
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, speciesTextView, genderTextView, statusTextView;
        Button editButton, deleteButton;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_view_name);
            speciesTextView = itemView.findViewById(R.id.text_view_species);
            genderTextView = itemView.findViewById(R.id.text_view_gender);
            statusTextView = itemView.findViewById(R.id.text_view_status);
        }
    }
}