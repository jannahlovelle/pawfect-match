package cit.edu.pawfect.match.dto;

import cit.edu.pawfect.match.entity.Pet;
import cit.edu.pawfect.match.entity.Photo;

import java.util.List;

public class PetWithPhotos {

    private Pet pet;
    private List<Photo> photos;

    public PetWithPhotos(Pet pet, List<Photo> photos) {
        this.pet = pet;
        this.photos = photos;
    }

    // Getters and setters
    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}