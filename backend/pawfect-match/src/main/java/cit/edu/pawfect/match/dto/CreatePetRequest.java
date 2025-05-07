package cit.edu.pawfect.match.dto;

import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreatePetRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Species is required")
    @Pattern(regexp = "(?i)^(Dog|Cat|Bird|Other)$", message = "Species must be one of: Dog, Cat, Bird, Other")
    private String species;
    @NotBlank(message = "Breed is required")
    private String breed;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "(?i)^(Male|Female)$", message = "Gender must be one of: Male, Female")
    private String gender;
    private Date dateOfBirth;
    private double weight;
    private String color;
    private String description;
    private String availabilityStatus;
    private double price;
    private String pedigreeInfo;
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