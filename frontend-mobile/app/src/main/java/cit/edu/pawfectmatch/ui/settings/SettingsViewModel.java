package cit.edu.pawfectmatch.ui.settings;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.GsonBuilder;

import java.util.Map;

import cit.edu.pawfectmatch.LoginActivity; // for BASE_URL
import cit.edu.pawfectmatch.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<String> deleteStatus = new MutableLiveData<>();

    public LiveData<String> getDeleteStatus() {
        return deleteStatus;
    }

    public void deleteAccount(String token) {


        // Create Retrofit instance inline
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LoginActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<Map<String, String>> call = apiService.deleteUser("Bearer " + token);

        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message");
                    Log.d("DeleteUser", "Success: " + message);
                    deleteStatus.postValue("success:" + message);
                } else {
                    Log.e("DeleteUser", "Failed to delete user. Code: " + response.code());
                    deleteStatus.postValue("error:Failed to delete account.");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e("DeleteUser", "Error: " + t.getMessage());
                deleteStatus.postValue("error:" + t.getMessage());
            }
        });
    }
}
