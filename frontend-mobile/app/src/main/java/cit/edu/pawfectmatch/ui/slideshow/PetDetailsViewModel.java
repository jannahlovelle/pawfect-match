package cit.edu.pawfectmatch.ui.slideshow;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Map;

import cit.edu.pawfectmatch.backendstuff.Photo;
import cit.edu.pawfectmatch.network.ApiService;
import cit.edu.pawfectmatch.network.UpdatePetRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PetDetailsViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Photo>> photos = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();
    private final ApiService apiService;
    private final SharedPreferences sharedPreferences;

    public PetDetailsViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences("auth", Context.MODE_PRIVATE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(cit.edu.pawfectmatch.LoginActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
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
                    photos.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to fetch photos: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }

    public void updatePet(String petId, UpdatePetRequest request) {
        isLoading.setValue(true);
        String token = sharedPreferences.getString("jwt_token", null);
        if (token == null) {
            isLoading.setValue(false);
            errorMessage.setValue("Not authenticated. Please log in.");
            return;
        }

        String authHeader = "Bearer " + token;
        apiService.updatePet(authHeader, petId, request).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    updateSuccess.setValue(true);
                } else {
                    errorMessage.setValue("Failed to update pet: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }

    public void deletePet(String petId) {
        isLoading.setValue(true);
        String token = sharedPreferences.getString("jwt_token", null);
        if (token == null) {
            isLoading.setValue(false);
            errorMessage.setValue("Not authenticated. Please log in.");
            return;
        }

        String authHeader = "Bearer " + token;
        apiService.deletePet(authHeader, petId).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    deleteSuccess.setValue(true);
                } else {
                    errorMessage.setValue("Failed to delete pet: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }
}