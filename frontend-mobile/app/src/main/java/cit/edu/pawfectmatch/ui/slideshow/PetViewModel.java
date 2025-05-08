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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cit.edu.pawfectmatch.backendstuff.Pet;
import cit.edu.pawfectmatch.backendstuff.Photo;
import cit.edu.pawfectmatch.network.ApiService;
import cit.edu.pawfectmatch.network.CreatePetRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PetViewModel extends AndroidViewModel {
    private static final String TAG = "PetViewModel";
    private final MutableLiveData<List<Pet>> pets = new MutableLiveData<>();
    private final MutableLiveData<Map<String, List<Photo>>> photos = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> createSuccess = new MutableLiveData<>();
    private final ApiService apiService;
    private final SharedPreferences sharedPreferences;

    public PetViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences("auth", Context.MODE_PRIVATE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public LiveData<List<Pet>> getPets() {
        return pets;
    }

    public LiveData<Map<String, List<Photo>>> getPhotos() {
        return photos;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getCreateSuccess() {
        return createSuccess;
    }

    public void fetchMyPets() {
        isLoading.setValue(true);

        String token = sharedPreferences.getString("jwt_token", null);
        if (token == null) {
            isLoading.setValue(false);
            errorMessage.setValue("Not authenticated. Please log in.");
            return;
        }

        String authHeader = "Bearer " + token;
        apiService.getMyPets(authHeader).enqueue(new Callback<List<Pet>>() {
            @Override
            public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    pets.setValue(response.body());
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "Failed to fetch pets: HTTP " + response.code() + ": " + errorBody);
                    errorMessage.setValue("Failed to fetch pets: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Pet>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Network error fetching pets: " + t.getMessage());
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void fetchPetPhotos(String petId) {
        isLoading.setValue(true);

        String token = sharedPreferences.getString("jwt_token", null);
        if (token == null) {
            isLoading.setValue(false);
            errorMessage.setValue("Not authenticated. Please log in.");
            return;
        }

        String authHeader = "Bearer " + token;
        apiService.getPetPhotos(authHeader, petId).enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, List<Photo>> currentPhotos = photos.getValue();
                    if (currentPhotos == null) {
                        currentPhotos = new HashMap<>();
                    }
                    currentPhotos.put(petId, response.body());
                    photos.setValue(currentPhotos);
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "Failed to fetch photos for petId " + petId + ": HTTP " + response.code() + ": " + errorBody);
                    errorMessage.setValue("Failed to load photos: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Network error fetching photos for petId " + petId + ": " + t.getMessage());
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void createPet(CreatePetRequest petRequest) {
        isLoading.setValue(true);
        String token = sharedPreferences.getString("jwt_token", null);
        if (token == null) {
            isLoading.setValue(false);
            errorMessage.setValue("Not authenticated. Please log in.");
            return;
        }

        String authHeader = "Bearer " + token;
        apiService.createPet(authHeader, petRequest).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    createSuccess.setValue(true);
                    fetchMyPets(); // Refresh pet list
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "Failed to create pet: HTTP " + response.code() + ": " + errorBody);
                    errorMessage.setValue("Failed to create pet: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Network error creating pet: " + t.getMessage());
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}