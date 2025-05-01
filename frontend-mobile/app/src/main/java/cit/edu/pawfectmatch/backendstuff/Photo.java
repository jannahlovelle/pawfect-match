package cit.edu.pawfectmatch.backendstuff;


import com.google.gson.annotations.SerializedName;

public class Photo {
    @SerializedName("photoId")
    private String photoId;

    @SerializedName("petId")
    private String petId;

    @SerializedName("url")
    private String url;

    @SerializedName("cloudinaryPublicId")
    private String cloudinaryPublicId;

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

    public String getCloudinaryPublicId() {
        return cloudinaryPublicId;
    }

    public void setCloudinaryPublicId(String cloudinaryPublicId) {
        this.cloudinaryPublicId = cloudinaryPublicId;
    }
}