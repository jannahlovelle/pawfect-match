package cit.edu.pawfectmatch.ui.profile;

import static cit.edu.pawfectmatch.LoginActivity.BASE_URL;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.GsonBuilder;

import java.util.Map;

import cit.edu.pawfectmatch.backendstuff.UserData;
import cit.edu.pawfectmatch.backendstuff.UserProfile;
import cit.edu.pawfectmatch.network.ApiService;
import cit.edu.pawfectmatch.network.UpdateUserRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<UserData> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateResult = new MutableLiveData<>();

    // LiveData for observing the user data and update status
    public LiveData<UserData> getUser() {
        return userLiveData;
    }

    public LiveData<Boolean> getUpdateResult() {
        return updateResult;
    }

    // Fetch user data from the backend
    public void fetchUser(String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<UserProfile> call = apiService.getUserProfile("Bearer " + token);

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    userLiveData.postValue(response.body().getUser());  // Update the UI with the fetched user data
                } else {
                    Log.e("UserProfile", "Error: " + response.code());
                    userLiveData.postValue(null);  // Set user data to null in case of failure
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e("UserProfile", "Failure: " + t.getMessage());
                userLiveData.postValue(null);  // Set user data to null if there's a failure
            }
        });
    }

    // Update user profile on the backend
    public void updateUserProfile(String token, String userId, UpdateUserRequest request) {
        Log.d("UpdateRequest", "First Name: " + request.getFirstName());
        Log.d("UpdateRequest", "Last Name: " + request.getLastName());
        Log.d("UpdateRequest", "Email: " + request.getEmail());
        Log.d("UpdateRequest", "Phone: " + request.getPhone());
        Log.d("UpdateRequest", "Address: " + request.getAddress());
        Log.d("UpdateRequest", "Password: " + request.getPassword());
        Log.d("UpdateRequest", "Profile Picture: " + request.getProfilePicture());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<Map<String, String>> call = apiService.updateUser(userId, request, "Bearer " + token);

        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ProfileUpdate", "Success: " + response.body().get("message"));
                    updateResult.postValue(true);  // Notify the UI of success
                    fetchUser(token);  // Fetch updated user data
                } else {
                    Log.e("ProfileUpdate", "Failed: " + response.code());
                    updateResult.postValue(false);  // Notify the UI of failure
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e("ProfileUpdate", "Error: " + t.getMessage());
                updateResult.postValue(false);  // Notify the UI of failure
            }
        });
    }
}
