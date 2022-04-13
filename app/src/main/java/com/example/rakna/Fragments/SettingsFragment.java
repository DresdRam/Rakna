package com.example.rakna.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.example.rakna.HomeActivity;
import com.example.rakna.LocaleHelper;
import com.example.rakna.LoginActivity;
import com.example.rakna.R;
import com.firebase.ui.auth.AuthUI;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    View view;
    Button logOutButton;
    Spinner spinner;
    SpinKitView spinKitView;
    private Context localeContext;
    private boolean selected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LocaleHelper.setAppLanguage(getActivity());
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        logOutButton = view.findViewById(R.id.button_logout);
        spinKitView = view.findViewById(R.id.spinKit_settings);
        spinner = view.findViewById(R.id.spinner_settings_languages);
        selected = false;

        setPreviousSelectedLang();

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutUser();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent refreshMain = new Intent(getContext(), HomeActivity.class);
                if (position == 0) {
                    localeContext = LocaleHelper.setLocale(getContext(), LocaleHelper.ENGLISH);
                    if (selected) {
                        getActivity().finish();
                        getContext().startActivity(refreshMain);
                    }
                } else if (position == 1) {
                    localeContext = LocaleHelper.setLocale(getContext(), LocaleHelper.ARABIC);
                    if (selected) {
                        getActivity().finish();
                        getContext().startActivity(refreshMain);
                    }
                }
                selected = true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

        return view;
    }

    private void setPreviousSelectedLang() {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String language = shared.getString(LocaleHelper.SELECTED_LANGUAGE, LocaleHelper.ENGLISH);
        if (language.equals(LocaleHelper.ENGLISH)) {
            spinner.setSelection(0);
        } else if (language.equals(LocaleHelper.ARABIC)) {
            spinner.setSelection(1);
        }
    }

    private void signOutUser() {
        AuthUI.getInstance().signOut(getActivity());
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();

    }
}