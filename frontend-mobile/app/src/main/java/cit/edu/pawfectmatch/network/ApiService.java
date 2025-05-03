package cit.edu.pawfectmatch.network;

import java.util.List;
import java.util.Map;

import cit.edu.pawfectmatch.backendstuff.Pet;
import cit.edu.pawfectmatch.backendstuff.Photo;
import cit.edu.pawfectmatch.backendstuff.UserData;
import cit.edu.pawfectmatch.backendstuff.UserProfile;
import cit.edu.pawfectmatch.ui.home.PetFeedResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Existing endpoints
    @POST("auth/login")
    Call<LoginResponse> login(@Body AuthRequest request);

    @Multipart
    @POST("auth/register")
    Call<Map<String, String>> register(
            @Part("user") RequestBody userRequest,
            @Part MultipartBody.Part file
    );

    @GET("users/email/{email}")
    Call<UserResponse> getUserByEmail(@Path("email") String email);

    @GET("users/me")
    Call<UserProfile> getUserProfile(@Header("Authorization") String token);

    @DELETE("users/{userId}")
    Call<Void> deleteUser(@Path("userId") String userId, @Header("Authorization") String token);

    @PUT("users/update/{userId}")
    Call<Map<String, String>> updateUser(
            @Header("Authorization") String token,
            @Path("userId") String userId,
            @Body UpdateUserRequest request
    );

    @Multipart
    @PUT("users/{userId}/profile-picture")
    Call<UserData> updateProfilePicture(
            @Header("Authorization") String token,
            @Path("userId") String userId,
            @Part MultipartBody.Part file
    );

    @DELETE("/users/delete/me")
    Call<Map<String, String>> deleteUser(@Header("Authorization") String token);

    @POST("pets/create")
    Call<Map<String, String>> createPet(@Header("Authorization") String authToken, @Body CreatePetRequest petRequest);

    @GET("pets/my-pets")
    Call<List<Pet>> getMyPets(@Header("Authorization") String authToken);

    @GET("pets/{petId}/photos")
    Call<List<Photo>> getPetPhotos(@Header("Authorization") String authToken, @Path("petId") String petId);

    @PUT("pets/update/{petId}")
    Call<Map<String, String>> updatePet(@Header("Authorization") String authToken, @Path("petId") String petId, @Body UpdatePetRequest request);

    @DELETE("pets/delete/{petId}")
    Call<Map<String, String>> deletePet(@Header("Authorization") String authToken, @Path("petId") String petId);

    // New endpoint for Firebase login
    @Multipart
    @POST("auth/firebase-login")
    Call<LoginResponse> firebaseLogin(
            @Part("idToken") RequestBody idToken,
            @Part MultipartBody.Part file
    );

    @GET("pets/feed")
    Call<List<PetFeedResponse>> getPetsForFeed(
            @Header("Authorization") String authToken,
            @Query("species") String species,
            @Query("page") int page,
            @Query("size") int size
    );
}