package cit.edu.pawfect.match.dto;

import cit.edu.pawfect.match.entity.User;
import java.util.List;

public class UserProfile {
    private User user;
    private List<PetWithPhotos> pets;

    public UserProfile(User user, List<PetWithPhotos> pets) {
        this.user = user;
        this.pets = pets;
    }

    // Getters and setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<PetWithPhotos> getPets() {
        return pets;
    }

    public void setPets(List<PetWithPhotos> pets) {
        this.pets = pets;
    }
}