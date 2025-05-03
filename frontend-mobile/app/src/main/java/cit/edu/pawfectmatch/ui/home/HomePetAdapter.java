package cit.edu.pawfectmatch.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cit.edu.pawfectmatch.R;

public class HomePetAdapter extends RecyclerView.Adapter<HomePetAdapter.PetViewHolder> {
    private List<PetFeedResponse> pets = new ArrayList<>();
    private final OnPetClickListener onPetClickListener;

    public interface OnPetClickListener {
        void onPetClick(PetFeedResponse pet);
    }

    public HomePetAdapter(OnPetClickListener listener) {
        this.onPetClickListener = listener;
    }

    public List<PetFeedResponse> getPets() {
        return pets;
    }

    public void setPets(List<PetFeedResponse> pets) {
        this.pets = pets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        PetFeedResponse pet = pets.get(position);
        holder.nameTextView.setText(pet.getName() != null ? pet.getName() : "N/A");
        holder.speciesTextView.setText(pet.getSpecies() != null ? pet.getSpecies() : "N/A");
        holder.breedTextView.setText(pet.getBreed() != null ? pet.getBreed() : "N/A");
        holder.descriptionTextView.setText(pet.getDescription() != null ? pet.getDescription() : "N/A");

        Glide.with(holder.itemView.getContext())
                .load(pet.getPhotoUrl())
//                .placeholder(R.drawable.placeholder_image)
//                .error(R.drawable.error_image)
                .into(holder.petImageView);

        holder.itemView.setOnClickListener(v -> onPetClickListener.onPetClick(pet));
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, speciesTextView, breedTextView, descriptionTextView;
        ImageView petImageView;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_view_name);
            speciesTextView = itemView.findViewById(R.id.text_view_species);
            breedTextView = itemView.findViewById(R.id.text_view_breed);
            descriptionTextView = itemView.findViewById(R.id.text_view_description);
            petImageView = itemView.findViewById(R.id.pet_image);
        }
    }
}