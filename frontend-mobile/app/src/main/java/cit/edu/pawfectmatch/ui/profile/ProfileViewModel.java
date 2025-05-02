package cit.edu.pawfectmatch.ui.profile;

import static cit.edu.pawfectmatch.LoginActivity.BASE_URL;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import cit.edu.pawfectmatch.backendstuff.UserData;
import cit.edu.pawfectmatch.backendstuff.UserProfile;
import cit.edu.pawfectmatch.network.ApiService;
import cit.edu.pawfectmatch.network.UpdateUserRequest;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<UserData> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateResult = new MutableLiveData<>();
    private ApiService apiService;

    public ProfileViewModel() {
        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    // LiveData for observing the user data and update status
    public LiveData<UserData> getUser() {
        return userLiveData;
    }

    public LiveData<Boolean> getUpdateResult() {
        return updateResult;
    }

    // Fetch user data from the backend
    public void fetchUser(String token) {
        Call<UserProfile> call = apiService.getUserProfile("Bearer " + token);

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    userLiveData.postValue(response.body().getUser()); // Update the UI with the fetched user data
                } else {
                    Log.e("UserProfile", "Error: " + response.code());
                    userLiveData.postValue(null); // Set user data to null in case of failure
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e("UserProfile", "Failure: " + t.getMessage());
                userLiveData.postValue(null); // Set user data to null if there's a failure
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

        Call<Map<String, String>> call = apiService.updateUser("Bearer " + token, userId, request);

        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ProfileUpdate", "Success: " + response.body().get("message"));
                    updateResult.postValue(true); // Notify the UI of success
                    fetchUser(token); // Fetch updated user data
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (Exception e) {
                        Log.e("ProfileUpdate", "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e("ProfileUpdate", "Failed: HTTP " + response.code() + ": " + errorBody);
                    updateResult.postValue(false); // Notify the UI of failure
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e("ProfileUpdate", "Error: " + t.getMessage());
                updateResult.postValue(false); // Notify the UI of failure
            }
        });
    }

    // Update profile picture on the backend
    public void updateProfilePicture(String token, String userId, Uri imageUri, Runnable onSuccess, Context context) {
        File file = createFileFromUri(imageUri, context);
        if (file == null || !file.exists()) {
            Log.e("ProfileUpdate", "Failed to process image file");
            updateResult.postValue(false);
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Log.d("ProfileUpdate", "Uploading file: " + file.getName());

        Call<UserData> call = apiService.updateProfilePicture("Bearer " + token, userId, filePart);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ProfileUpdate", "Profile picture updated successfully");
                    userLiveData.postValue(response.body()); // Update user data with new profile picture
                    onSuccess.run(); // Proceed to update user data
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (Exception e) {
                        Log.e("ProfileUpdate", "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e("ProfileUpdate", "Failed to update profile picture: HTTP " + response.code() + ": " + errorBody);
                    updateResult.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Log.e("ProfileUpdate", "Network error: " + t.getMessage());
                updateResult.postValue(false);
            }
        });
    }

    private File createFileFromUri(Uri uri, Context context) {
        try {
            File file = new File(context.getCacheDir(), "profile_image_" + System.currentTimeMillis() + ".jpg");
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            return file;
        } catch (Exception e) {
            Log.e("ProfileUpdate", "Error creating file from Uri: " + e.getMessage());
            return null;
        }
    }
}