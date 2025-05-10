package cit.edu.pawfectmatch.ui.slideshow;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cit.edu.pawfectmatch.R;
import cit.edu.pawfectmatch.backendstuff.Pet;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
    private static final String TAG = "PetAdapter";
    private List<Pet> pets = new ArrayList<>();
    private final Map<String, String> petPhotoUrls = new HashMap<>();
    private final OnPetClickListener onPetClickListener;

    public interface OnPetClickListener {
        void onPetClick(Pet pet);
    }

    public PetAdapter(OnPetClickListener listener) {
        this.onPetClickListener = listener;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
        notifyDataSetChanged();
    }

    public void setPetPhotos(Map<String, String> photoUrls) {
        this.petPhotoUrls.clear();
        this.petPhotoUrls.putAll(photoUrls);
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
        holder.breedTextView.setText(pet.getBreed() != null ? pet.getBreed() : "N/A");
        holder.descriptionTextView.setText(pet.getDescription() != null ? pet.getDescription() : "N/A");

        // Load pet photo using Glide
        String photoUrl = petPhotoUrls.get(pet.getPetId());
        Log.d(TAG, "Loading photo for petId " + pet.getPetId() + ": " + photoUrl);
        Glide.with(holder.itemView.getContext())
                .load(photoUrl)
                .placeholder(R.drawable.defaultprofile)
                .error(R.drawable.defaultprofile)
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