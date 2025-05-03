package cit.edu.pawfectmatch.ui.home;

public class PetFeedResponse {
    private String petId;
    private String name;
    private String species;
    private String breed;
    private String photoUrl;
    private String description;

    public PetFeedResponse(String petId, String name, String species, String breed, String photoUrl, String description) {
        this.petId = petId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.photoUrl = photoUrl;
        this.description = description;
    }

    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}