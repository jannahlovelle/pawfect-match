package cit.edu.pawfect.match.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "pets")
public class Pet {

    @Id
    private String petId; // Primary key (MongoDB ObjectId)

    private String userId; // Foreign key referencing the User (MongoDB ObjectId)
    private String name;
    private String species;
    private String breed;
    private String gender;
    private Date dateOfBirth;
    private double weight;
    private String color;
    private String description;
    private String availabilityStatus; // "available", "reserved", "sold"
    private double price; //Price for the breeding service
    private String pedigreeInfo;
    private String healthStatus;

    // Constructors
    public Pet() {}

    public Pet(String userId, String name, String species, String breed, String gender, Date dateOfBirth,
               double weight, String color, String description, String availabilityStatus, double price,
               String pedigreeInfo, String healthStatus) {
        this.userId = userId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.weight = weight;
        this.color = color;
        this.description = description;
        this.availabilityStatus = availabilityStatus;
        this.price = price;
        this.pedigreeInfo = pedigreeInfo;
        this.healthStatus = healthStatus;
    }

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