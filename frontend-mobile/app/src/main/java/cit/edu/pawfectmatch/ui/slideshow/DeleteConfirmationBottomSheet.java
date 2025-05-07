package cit.edu.pawfectmatch.ui.slideshow;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import cit.edu.pawfectmatch.R;

public class DeleteConfirmationBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_PET_ID = "pet_id";
    private static final String ARG_PET_NAME = "pet_name";
    private static final String TAG = "DeleteConfirmationBottomSheet";
    private String petId;
    private String petName;
    private OnDeleteConfirmedListener listener;

    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed(String petId);
    }

    public static DeleteConfirmationBottomSheet newInstance(String petId, String petName) {
        DeleteConfirmationBottomSheet fragment = new DeleteConfirmationBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_PET_ID, petId);
        args.putString(ARG_PET_NAME, petName);
        fragment.setArguments(args);
        return fragment;
    }

    // Explicitly set the listener
    public void setOnDeleteConfirmedListener(OnDeleteConfirmedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            petId = getArguments().getString(ARG_PET_ID);
            petName = getArguments().getString(ARG_PET_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_delete_confirmation, container, false);

        TextView messageText = view.findViewById(R.id.delete_message);
        Button confirmButton = view.findViewById(R.id.delete_confirm);
        Button cancelButton = view.findViewById(R.id.delete_cancel);

        // Set confirmation message
        String message = petName != null ? String.format("Are you sure you want to delete %s?", petName) : "Are you sure you want to delete this pet?";
        messageText.setText(message);

        // Confirm deletion
        confirmButton.setOnClickListener(v -> {
            if (listener != null && petId != null) {
                listener.onDeleteConfirmed(petId);
            } else {
                Log.e(TAG, "Cannot confirm delete: listener=" + (listener == null ? "null" : "set") + ", petId=" + (petId == null ? "null" : petId));
                Toast.makeText(requireContext(), "Cannot perform delete action", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });

        // Cancel deletion
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }
}