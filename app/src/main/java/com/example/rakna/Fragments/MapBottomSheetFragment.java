package com.example.rakna.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rakna.HomeCommunicator;
import com.example.rakna.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MapBottomSheetFragment extends BottomSheetDialogFragment {


    View view;
    Button navigateBtn;
    Button spectateBtn;
    HomeCommunicator activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_bottom_sheet, container, false);
        initComponents();
        navigateListener();
        spectateListener();
        return view;
    }

    private void navigateListener() {
        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.navigateToParkingLocation();
            }
        });
    }

    private void spectateListener() {
        spectateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.spectateParkingLocation();
            }
        });
    }

    private void initComponents() {
        navigateBtn = view.findViewById(R.id.button_navigate);
        spectateBtn = view.findViewById(R.id.button_spectate);
        activity = (HomeCommunicator) getActivity();
    }
}