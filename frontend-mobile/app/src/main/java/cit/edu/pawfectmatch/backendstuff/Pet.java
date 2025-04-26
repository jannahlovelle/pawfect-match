package cit.edu.pawfectmatch.backendstuff;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Pet {
    @SerializedName("petId")
    private String petId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("name")
    private String name;

    @SerializedName("species")
    private String species;

    @SerializedName("breed")
    private String breed;

    @SerializedName("gender")
    private String gender;

    @SerializedName("dateOfBirth")
    private Date dateOfBirth;

    @SerializedName("weight")
    private double weight;

    @SerializedName("color")
    private String color;

    @SerializedName("description")
    private String description;

    @SerializedName("availabilityStatus")
    private String availabilityStatus;

    @SerializedName("price")
    private double price;

    @SerializedName("pedigreeInfo")
    private String pedigreeInfo;

    @SerializedName("healthStatus")
    private String healthStatus;

    // Getters and setters
    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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