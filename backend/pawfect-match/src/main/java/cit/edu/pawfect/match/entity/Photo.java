package cit.edu.pawfect.match.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "photos")
public class Photo {

    @Id
    private String photoId; 

    private String petId; 
    private String url; 

    public Photo() {}

    public Photo(String petId, String url) {
        this.petId = petId;
        this.url = url;
    }

    // Getters and setters
    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

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