package cit.edu.pawfectmatch;

import static cit.edu.pawfectmatch.LoginActivity.BASE_URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.bumptech.glide.Glide; // Import Glide
import com.google.gson.GsonBuilder;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import cit.edu.pawfectmatch.backendstuff.UserData;
import cit.edu.pawfectmatch.backendstuff.UserProfile;
import cit.edu.pawfectmatch.databinding.ActivityMainBinding;
import cit.edu.pawfectmatch.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private ImageView profileImageView;
    private TextView navNameHolder, navEmailHolder;
    private View headerView;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setVisibility(View.GONE);

        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        headerView = navigationView.getHeaderView(0);

        navNameHolder = headerView.findViewById(R.id.nav_header_name);
        navEmailHolder = headerView.findViewById(R.id.nav_header_email);
        profileImageView = headerView.findViewById(R.id.nav_profile_pic);

        // User Data Passing START
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

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
                    UserData user = response.body().getUser();
                    String strfirstName = user.getFirstName();
                    String strlastName = user.getLastName();
                    String stremail = user.getEmail();
                    String strpic = user.getProfilePicture();

                    navNameHolder.setText(new StringBuilder().append(strfirstName).append(" ").append(strlastName).toString());
                    navEmailHolder.setText(stremail);

                    // Check if the profile picture URL is null or empty
                    if (strpic != null && !strpic.trim().isEmpty() && !strpic.trim().equalsIgnoreCase("null")) {
                        try {
                            if (strpic.startsWith("http")) {
                                // Load using Glide if it's a proper URL
                                Glide.with(MainActivity.this)
                                        .load(strpic)
                                        .placeholder(R.drawable.defaultprofile) // optional
                                        .error(R.drawable.defaultprofile)
                                        .into(profileImageView);
                            } else {
                                // Treat as Base64
                                if (strpic.contains(",")) {
                                    strpic = strpic.split(",")[1]; // Remove data:image/... prefix if present
                                }

                                byte[] imageBytes = android.util.Base64.decode(strpic, android.util.Base64.DEFAULT);
                                android.graphics.Bitmap decodedImage = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                profileImageView.setImageBitmap(decodedImage);
                            }
                        } catch (Exception e) {
                            Log.e("ProfilePic", "Image load failed: " + e.getMessage());
                            profileImageView.setImageResource(R.drawable.defaultprofile); // fallback
                        }
                    } else {
                        Log.d("ProfilePic", "Picture is null or empty, setting default.");
                        profileImageView.setImageResource(R.drawable.defaultprofile);
                    }


                } else {
                    Log.e("UserProfile", "Response error or null user object: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e("UserProfile", "Failure: " + t.getMessage());
            }
        });
        // STOP


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_slideshow, R.id.nav_settings, R.id.nav_signout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        binding.appBarMain.fab.setOnClickListener(view -> {
//            NavController navController1 = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
//            if (navController1.getCurrentDestination().getId() == R.id.nav_pets) {
//                navController1.navigate(R.id.nav_addPetFragment);
//            }
//        });

        binding.appBarMain.fab.setOnClickListener(view -> {
            NavController navController1 = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
            navController1.navigate(R.id.nav_addPetFragment);
        });
//         Add destination change listener to toggle Toolbar visibility
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_pets) {
                // Show Toolbar in ProfileFragment
                binding.appBarMain.fab.setVisibility(View.VISIBLE);
            } else {
                // Hide Toolbar in all other fragments
                binding.appBarMain.fab.setVisibility(View.GONE);
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_signout) {
                signOut();
                return true;
            }

            // Default navigation behavior
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                drawer.closeDrawers(); // close drawer after item selection
            }
            return handled;
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void signOut() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // important!
        startActivity(intent);
        finish();
    }

}
