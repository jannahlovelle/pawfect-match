package cit.edu.pawfectmatch.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.shimmer.ShimmerFrameLayout;

import cit.edu.pawfectmatch.R;
import cit.edu.pawfectmatch.databinding.FragmentProfileBinding;
import cit.edu.pawfectmatch.network.UpdateUserRequest;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private boolean editmode = false;
    private String token, userId;
    private ImageView profileImageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri profileImageUri = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        profileImageView = binding.profileProfilepic;

        SharedPreferences prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
        token = prefs.getString("jwt_token", null);
        userId = prefs.getString("user_id", null);

        if (token != null) {
            profileViewModel.fetchUser(token);
        }

        ShimmerFrameLayout shimmerLayout = binding.shimmerLayout;
        ScrollView profileContent = binding.scrollView3;

        shimmerLayout.setVisibility(View.VISIBLE);
        profileContent.setVisibility(View.GONE);

        profileViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                String fullName = user.getFirstName() + " " + user.getLastName();
                binding.profileName.setText(fullName);
                binding.profileEmail.setText(user.getEmail());
                binding.profilePhone.setText(user.getPhone());
                binding.profileAddress.setText(user.getAddress());

                String profilePic = user.getProfilePicture();
                if (profilePic != null && !profilePic.isEmpty()) {
                    // Load profile picture as a URL (backend returns Cloudinary URL)
                    com.bumptech.glide.Glide.with(requireContext())
                            .load(profilePic)
                            .into(profileImageView);
                } else {
                    com.bumptech.glide.Glide.with(requireContext())
                            .load(R.drawable.defaultprofile)
                            .into(profileImageView);
                }

                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                profileContent.setVisibility(View.VISIBLE);
            }
        });

        profileViewModel.getUpdateResult().observe(getViewLifecycleOwner(), isSuccess -> {
            binding.profileSubmtbtnText.setVisibility(View.VISIBLE);
            binding.profileSubmitbtnSpinner.setVisibility(View.GONE);

            if (Boolean.TRUE.equals(isSuccess)) {
                Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                profileViewModel.fetchUser(token);
                exitEditMode();
            } else {
                Toast.makeText(getContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
        });

        profileImageView.setOnClickListener(v -> {
            if (editmode) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        binding.profileSubmitbtnWrapper.setOnClickListener(v -> {
            if (token != null && userId != null) {
                binding.profileSubmtbtnText.setVisibility(View.GONE);
                binding.profileSubmitbtnSpinner.setVisibility(View.VISIBLE);

                // Update profile picture first (if changed)
                if (profileImageUri != null) {
                    profileViewModel.updateProfilePicture(token, userId, profileImageUri, () -> {
                        // Then update user data
                        updateUserData();
                    }, requireContext());
                } else {
                    // No profile picture change, update user data directly
                    updateUserData();
                }
            } else {
                Log.e("ProfileFragment", "User ID or token is null.");
                binding.profileSubmtbtnText.setVisibility(View.VISIBLE);
                binding.profileSubmitbtnSpinner.setVisibility(View.GONE);
            }
        });

        binding.profileEditbtn.setOnClickListener(v -> {
            if (profileViewModel.getUser().getValue() != null) {
                binding.profileEditFirstname.setText(profileViewModel.getUser().getValue().getFirstName());
                binding.profileEditLastname.setText(profileViewModel.getUser().getValue().getLastName());
                binding.profileEditEmail.setText(profileViewModel.getUser().getValue().getEmail());
                binding.profileEditPhone.setText(profileViewModel.getUser().getValue().getPhone());
                binding.profileEditAddress.setText(profileViewModel.getUser().getValue().getAddress());
            }

            enterEditMode();
        });

        binding.profileCancelbtn.setOnClickListener(v -> {
            profileImageUri = null; // Reset image selection
            exitEditMode();
        });

        return root;
    }

    private void updateUserData() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName(binding.profileEditFirstname.getText().toString());
        request.setLastName(binding.profileEditLastname.getText().toString());
        request.setPhone(binding.profileEditPhone.getText().toString());
        request.setAddress(binding.profileEditAddress.getText().toString());
        request.setEmail(binding.profileEditEmail.getText().toString());

        String password = binding.profileEditPassword.getText().toString();
        if (!password.isEmpty()) {
            request.setPassword(password);
        }

        profileViewModel.updateUserProfile(token, userId, request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            profileImageUri = data.getData();
            try {
                profileImageView.setImageURI(profileImageUri);
            } catch (Exception e) {
                Log.e("ProfileFragment", "Error loading image: " + e.getMessage());
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enterEditMode() {
        binding.profileName.setVisibility(View.GONE);
        binding.profilePetcount.setVisibility(View.GONE);
        binding.profileEmail.setVisibility(View.GONE);
        binding.profilePhone.setVisibility(View.GONE);
        binding.profileAddress.setVisibility(View.GONE);

        binding.profileEditFirstname.setVisibility(View.VISIBLE);
        binding.profileEditLastname.setVisibility(View.VISIBLE);
        binding.profileEditEmail.setVisibility(View.VISIBLE);
        binding.profileEditPhone.setVisibility(View.VISIBLE);
        binding.profileEditAddress.setVisibility(View.VISIBLE);
        binding.profileEditPassword.setVisibility(View.VISIBLE);

        binding.profileSubmitbtnWrapper.setVisibility(View.VISIBLE);
        binding.profileSubmtbtnText.setVisibility(View.VISIBLE);
        binding.profileCancelbtn.setVisibility(View.VISIBLE);
        binding.profileEditbtn.setVisibility(View.GONE);
        binding.profileProfilepic.setClickable(true);
        editmode = true;
    }

    private void exitEditMode() {
        binding.profileName.setVisibility(View.VISIBLE);
        binding.profilePetcount.setVisibility(View.VISIBLE);
        binding.profileEmail.setVisibility(View.VISIBLE);
        binding.profilePhone.setVisibility(View.VISIBLE);
        binding.profileAddress.setVisibility(View.VISIBLE);

        binding.profileEditFirstname.setVisibility(View.GONE);
        binding.profileEditLastname.setVisibility(View.GONE);
        binding.profileEditEmail.setVisibility(View.GONE);
        binding.profileEditPhone.setVisibility(View.GONE);
        binding.profileEditAddress.setVisibility(View.GONE);
        binding.profileEditPassword.setVisibility(View.GONE);

        binding.profileSubmitbtnWrapper.setVisibility(View.GONE);
        binding.profileSubmtbtnText.setVisibility(View.GONE);
        binding.profileSubmitbtnSpinner.setVisibility(View.GONE);
        binding.profileCancelbtn.setVisibility(View.GONE);
        binding.profileEditbtn.setVisibility(View.VISIBLE);
        binding.profileProfilepic.setClickable(false);
        profileImageUri = null; // Reset image selection
        editmode = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}