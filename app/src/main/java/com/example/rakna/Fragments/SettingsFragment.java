package com.example.rakna.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.rakna.LoginActivity;
import com.example.rakna.R;
import com.example.rakna.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;


public class SettingsFragment extends Fragment {
Button logout;
FirebaseAuth auth =FirebaseAuth.getInstance();
View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_settings, container, false);
        initComponent();
        logoutAction();
        return view;
    }
    private void initComponent(){
       logout=view.findViewById(R.id.button_logout);

    }
    private void logoutAction(){
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });
    }
}