package cit.edu.pawfectmatch.ui.slideshow;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import cit.edu.pawfectmatch.R;
import cit.edu.pawfectmatch.backendstuff.Pet;


public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
    private List<Pet> pets = new ArrayList<>();

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
        holder.nameTextView.setText(pet.getName());
        holder.speciesTextView.setText(pet.getSpecies());
        holder.genderTextView.setText(pet.getGender());
        holder.statusTextView.setText(pet.getAvailabilityStatus());
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, speciesTextView, genderTextView, statusTextView;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(cit.edu.pawfectmatch.R.id.text_view_name);
            speciesTextView = itemView.findViewById(R.id.text_view_species);
            genderTextView = itemView.findViewById(R.id.text_view_gender);
            statusTextView = itemView.findViewById(R.id.text_view_status);
        }
    }
}