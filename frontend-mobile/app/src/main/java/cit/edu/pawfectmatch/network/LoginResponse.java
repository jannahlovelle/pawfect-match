package cit.edu.pawfectmatch.network;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("userId")
    private String userId;

    @SerializedName("token")
    private String token;

    public String getUserID() {
        return userId;
    }
    public String getToken() {
        return token;
    }
}