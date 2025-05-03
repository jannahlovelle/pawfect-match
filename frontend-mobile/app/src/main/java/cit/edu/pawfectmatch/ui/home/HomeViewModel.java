package cit.edu.pawfectmatch.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import cit.edu.pawfectmatch.network.ApiService;
import cit.edu.pawfectmatch.ui.home.PetFeedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<PetFeedResponse>> pets = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private int currentPage = 0;
    private final int PAGE_SIZE = 20;

    public LiveData<List<PetFeedResponse>> getPets() {
        return pets;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchPets(Context context, ApiService apiService, boolean isRefresh) {
        if (isLoading.getValue() != null && isLoading.getValue()) return;
        if (isRefresh) {
            currentPage = 0;
            pets.setValue(new ArrayList<>());
        }

        isLoading.setValue(true);
        String authToken = "Bearer " + getAuthToken(context);
        if (authToken.equals("Bearer null")) {
            isLoading.setValue(false);
            errorMessage.setValue("Please log in to view pets");
            return;
        }

        Call<List<PetFeedResponse>> call = apiService.getPetsForFeed(authToken, null, currentPage, PAGE_SIZE);
        call.enqueue(new Callback<List<PetFeedResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<PetFeedResponse>> call, @NonNull Response<List<PetFeedResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<PetFeedResponse> newPets = response.body();
                    List<PetFeedResponse> currentPets = new ArrayList<>(pets.getValue() != null ? pets.getValue() : new ArrayList<>());
                    if (currentPage == 0) {
                        currentPets = newPets;
                    } else {
                        currentPets.addAll(newPets);
                    }
                    pets.setValue(currentPets);
                    currentPage++;
                    errorMessage.setValue(null);
                } else {
                    String msg = response.code() == 401 ? "Unauthorized: Please log in again" : "Failed to load pets";
                    errorMessage.setValue(msg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PetFeedResponse>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    private String getAuthToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        return prefs.getString("jwt_token", null);
    }
}