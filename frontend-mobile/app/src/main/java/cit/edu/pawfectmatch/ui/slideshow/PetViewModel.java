package cit.edu.pawfectmatch.ui.slideshow;

import static cit.edu.pawfectmatch.LoginActivity.BASE_URL;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Map;

import cit.edu.pawfectmatch.backendstuff.Pet;
import cit.edu.pawfectmatch.network.ApiService;
import cit.edu.pawfectmatch.network.CreatePetRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PetViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Pet>> pets = new MutableLiveData<>();
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
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public LiveData<List<Pet>> getPets() {
        return pets;
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
                    errorMessage.setValue("Failed to fetch pets: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Pet>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
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
                    errorMessage.setValue("Failed to create pet: " + response.message());
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