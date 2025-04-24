package cit.edu.pawfectmatch.network;


import java.util.Map;

import cit.edu.pawfectmatch.backendstuff.UserProfile;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // Login endpoint (returns a plain string token)
    @POST("auth/login")
    Call<LoginResponse> login(@Body AuthRequest request);
    // Register endpoint

    @POST("auth/register")
    Call<Map<String, String>> register(@Body RegisterRequest request);
    // Get user by email from MongoDB
    @GET("users/email/{email}")
    Call<UserResponse> getUserByEmail(@Path("email") String email);

    @GET("users/me")
    Call<UserProfile> getUserProfile(@Header("Authorization") String token);

    @DELETE("users/{userId}")
    Call<Void> deleteUser(@Path("userId") String userId, @Header("Authorization") String token);

    @PUT("users/update/{userId}")
    Call<Map<String, String>> updateUser(
            @Path("userId") String userId,
            @Body UpdateUserRequest request,
            @Header("Authorization") String token
    );
    @DELETE("/users/delete/me")
    Call<Map<String, String>> deleteUser(@Header("Authorization") String token);




}

// Response model for user data (used by getUserByEmail)
