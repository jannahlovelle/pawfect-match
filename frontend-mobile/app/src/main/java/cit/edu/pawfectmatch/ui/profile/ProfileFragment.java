package cit.edu.pawfectmatch.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    private String encodedImage = null;

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
                    if (isBase64(profilePic)) {
                        byte[] imageBytes = Base64.decode(profilePic, Base64.DEFAULT);
                        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        profileImageView.setImageBitmap(decodedImage);
                    } else {
                        Glide.with(requireContext())
                                .load(profilePic)
                                .into(profileImageView);
                    }
                } else {
                    Glide.with(requireContext())
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

                UpdateUserRequest request = new UpdateUserRequest();
                request.setFirstName(binding.profileEditFirstname.getText().toString());
                request.setLastName(binding.profileEditLastname.getText().toString());
                request.setPhone(binding.profileEditPhone.getText().toString());
                request.setAddress(binding.profileEditAddress.getText().toString());
                request.setEmail(binding.profileEditEmail.getText().toString());
                request.setProfilePicture(encodedImage);

                String password = binding.profileEditPassword.getText().toString();
                if (!password.isEmpty()) {
                    request.setPassword(password);
                }

                profileViewModel.updateUserProfile(token, userId, request);
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
            exitEditMode();
        });

        return root;
    }

    private boolean isBase64(String str) {
        try {
            Base64.decode(str, Base64.DEFAULT);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImageUri);
                profileImageView.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

            } catch (IOException e) {
                Log.e("ProfileFragment", "Error loading image: " + e.getMessage());
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
        editmode = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
