package cit.edu.pawfect.match.dto;

public class AddPhotoRequest {
    private String petId;
    private String url;

    // Getters and setters
    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}