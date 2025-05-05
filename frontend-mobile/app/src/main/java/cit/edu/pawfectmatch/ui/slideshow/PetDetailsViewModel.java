package cit.edu.pawfectmatch.ui.slideshow;

import static cit.edu.pawfectmatch.LoginActivity.BASE_URL;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.List;
import java.util.Map;

import cit.edu.pawfectmatch.backendstuff.Pet;
import cit.edu.pawfectmatch.backendstuff.Photo;
import cit.edu.pawfectmatch.network.ApiService;
import cit.edu.pawfectmatch.network.UpdatePetRequest;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PetDetailsViewModel extends AndroidViewModel {
    private static final String TAG = "PetDetailsViewModel";
    private final MutableLiveData<Pet> pet = new MutableLiveData<>();
    private final MutableLiveData<List<Photo>> photos = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> photoUploadSuccess = new MutableLiveData<>();
    private final ApiService apiService;
    private final SharedPreferences sharedPreferences;

    public PetDetailsViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences("auth", Context.MODE_PRIVATE);
        Log.d(TAG, "Using BASE_URL: " + BASE_URL);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public LiveData<Pet> getPet() {
        return pet;
    }

    public LiveData<List<Photo>> getPhotos() {
        return photos;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    public LiveData<Boolean> getDeleteSuccess() {
        return deleteSuccess;
    }

    public LiveData<String> getPhotoUploadSuccess() {
        return photoUploadSuccess;
    }

    public void fetchPetPhotos(String authToken, String petId) {
        isLoading.setValue(true);
        String authHeader = "Bearer " + authToken;
        apiService.getPetPhotos(authHeader, petId).enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    photos.setValue(response.body());
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "Failed to fetch photos: HTTP " + response.code() + ": " + errorBody);
                    errorMessage.setValue("Failed to load photos: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Network error fetching photos: " + t.getMessage());
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void updatePet(String authToken, String petId, UpdatePetRequest request) {
        isLoading.setValue(true);
        String authHeader = "Bearer " + authToken;
        apiService.updatePet(authHeader, petId, request).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    updateSuccess.setValue(true);
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "Failed to update pet: HTTP " + response.code() + ": " + errorBody);
                    errorMessage.setValue("Failed to update pet: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Network error updating pet: " + t.getMessage());
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void deletePet(String authToken, String petId) {
        isLoading.setValue(true);
        String authHeader = "Bearer " + authToken;
        apiService.deletePet(authHeader, petId).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    deleteSuccess.setValue(true);
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "Failed to delete pet: HTTP " + response.code() + ": " + errorBody);
                    errorMessage.setValue("Failed to delete pet: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Network error deleting pet: " + t.getMessage());
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void uploadPetPhoto(String authToken, String petId, File photoFile) {
        isLoading.setValue(true);
        String authHeader = "Bearer " + authToken;

        // Determine MediaType based on file extension
        String mediaType = photoFile.getName().endsWith(".png") ? "image/png" : "image/jpeg";
        RequestBody fileBody = RequestBody.create(MediaType.parse(mediaType), photoFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", photoFile.getName(), fileBody);

        // Log request details
        Log.d(TAG, "Upload request: URL=" + BASE_URL + "pets/" + petId + "/photos, authHeader=" + authHeader + ", fileName=" + photoFile.getName() + ", mediaType=" + mediaType + ", fileSize=" + photoFile.length() + " bytes");

        apiService.addPetPhoto(authHeader, petId, filePart).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    String photoUrl = response.body().get("url");
                    photoUploadSuccess.setValue(photoUrl != null ? photoUrl : "success");
                    Log.d(TAG, "Photo uploaded successfully: " + response.body());
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "Failed to upload photo: HTTP " + response.code() + ": " + errorBody);
                    errorMessage.setValue("Failed to upload photo: HTTP " + response.code() + ": " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Network error uploading photo: " + t.getMessage());
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}