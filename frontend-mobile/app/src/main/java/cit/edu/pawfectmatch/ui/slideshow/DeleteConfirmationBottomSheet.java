package cit.edu.pawfectmatch.ui.slideshow;

import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment parentFragment = getParentFragment();
        Log.d(TAG, "Parent fragment: " + (parentFragment != null ? parentFragment.getClass().getSimpleName() : "null"));
        if (parentFragment instanceof OnDeleteConfirmedListener) {
            listener = (OnDeleteConfirmedListener) parentFragment;
        } else {
            Log.e(TAG, "Parent fragment does not implement OnDeleteConfirmedListener: " +
                    (parentFragment != null ? parentFragment.getClass().getSimpleName() : "null"));
            Toast.makeText(context, "Cannot perform delete action", Toast.LENGTH_SHORT).show();
            // Optionally dismiss to prevent further interaction
            dismiss();
        }
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

        messageText.setText(String.format("Are you sure you want to delete %s?", petName));

        confirmButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteConfirmed(petId);
            } else {
                Log.e(TAG, "Listener is null, cannot confirm delete");
                Toast.makeText(requireContext(), "Cannot perform delete action", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }
}