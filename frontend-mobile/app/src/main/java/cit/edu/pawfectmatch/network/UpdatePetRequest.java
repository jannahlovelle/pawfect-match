package cit.edu.pawfectmatch.network;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;

public class UpdatePetRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("species")
    private String species;

    @SerializedName("breed")
    private String breed;

    @SerializedName("gender")
    private String gender;

    @SerializedName("dateOfBirth")
    private String dateOfBirth;

    @SerializedName("weight")
    private Double weight;

    @SerializedName("color")
    private String color;

    @SerializedName("description")
    private String description;

    @SerializedName("availabilityStatus")
    private String availabilityStatus;

    @SerializedName("price")
    private Double price;

    @SerializedName("pedigreeInfo")
    private String pedigreeInfo;

    @SerializedName("healthStatus")
    private String healthStatus;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPedigreeInfo() {
        return pedigreeInfo;
    }

    public void setPedigreeInfo(String pedigreeInfo) {
        this.pedigreeInfo = pedigreeInfo;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }
}