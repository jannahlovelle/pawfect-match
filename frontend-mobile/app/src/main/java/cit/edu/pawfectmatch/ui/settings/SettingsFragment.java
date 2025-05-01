package cit.edu.pawfectmatch.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cit.edu.pawfectmatch.LoginActivity;
import cit.edu.pawfectmatch.R;

public class SettingsFragment extends Fragment implements ConfirmDeleteBottomSheet.OnDeleteConfirmedListener {

    private SettingsViewModel settingsViewModel;
    private String token;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        Button deleteButton = root.findViewById(R.id.settings_deleteaccount);

        SharedPreferences prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
        token = prefs.getString("jwt_token", null);

        deleteButton.setOnClickListener(v -> {
            if (token != null) {
                // Show confirmation bottom sheet
                ConfirmDeleteBottomSheet bottomSheet = new ConfirmDeleteBottomSheet();
                bottomSheet.show(getChildFragmentManager(), "ConfirmDeleteBottomSheet");
            } else {
                Toast.makeText(getContext(), "Token not found. Please log in again.", Toast.LENGTH_SHORT).show();
            }
        });

        settingsViewModel.getDeleteStatus().observe(getViewLifecycleOwner(), status -> {
            if (status.startsWith("success:")) {
                Toast.makeText(getContext(), status.substring(8), Toast.LENGTH_SHORT).show();
                // Clear token and redirect to login
                SharedPreferences.Editor editor = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE).edit();
                editor.remove("jwt_token");
                editor.apply();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (status.startsWith("error:")) {
                Toast.makeText(getContext(), status.substring(6), Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    @Override
    public void onDeleteConfirmed() {
        // Called when user confirms deletion in the bottom sheet
        Log.d("SettingsFragment", "Delete account confirmed, token: " + token);
        settingsViewModel.deleteAccount(token);
    }
}