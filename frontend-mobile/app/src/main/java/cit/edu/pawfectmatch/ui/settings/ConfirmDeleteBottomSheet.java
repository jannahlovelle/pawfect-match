package cit.edu.pawfectmatch.ui.settings;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import cit.edu.pawfectmatch.R;

public class ConfirmDeleteBottomSheet extends BottomSheetDialogFragment {

    private OnDeleteConfirmedListener listener;

    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDeleteConfirmedListener) {
            listener = (OnDeleteConfirmedListener) context;
        } else if (getParentFragment() instanceof OnDeleteConfirmedListener) {
            listener = (OnDeleteConfirmedListener) getParentFragment();
        } else {
            throw new ClassCastException("Host must implement OnDeleteConfirmedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_confirm_delete, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button confirmButton = view.findViewById(R.id.confirm_delete_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        confirmButton.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteConfirmed();
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());
    }
}
