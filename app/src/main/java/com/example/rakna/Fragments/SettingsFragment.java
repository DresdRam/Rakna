package com.example.rakna.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.rakna.HomeActivity;
import com.example.rakna.LocaleHelper;
import com.example.rakna.LoginActivity;
import com.example.rakna.R;
import com.example.rakna.Pojo.UserModel;
import com.firebase.ui.auth.AuthUI;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsFragment extends Fragment {

    Button logout;
    Spinner spinner;
    SpinKitView spinKitView;
    TextView username;
    CircleImageView userImage;
    private boolean selected;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        initComponent();
        setPreviousSelectedLang();
        setUserInfo();
        logoutAction();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent refreshMain = new Intent(getContext(), HomeActivity.class);
                if (position == 0) {
                    LocaleHelper.setLocale(getContext(), LocaleHelper.ENGLISH);
                    if (selected) {
                        getActivity().finish();
                        getContext().startActivity(refreshMain);
                    }
                } else if (position == 1) {
                    LocaleHelper.setLocale(getContext(), LocaleHelper.ARABIC);
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

    private void setUserInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                assert userModel != null;
                username.setText(userModel.getUserName());
                userImage.setVisibility(View.INVISIBLE);
                spinKitView.setVisibility(View.VISIBLE);
                if (userModel.getUri() != null) {
                    Picasso.get()
                            .load(userModel.getUri())
                            .into(userImage, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    userImage.setVisibility(View.VISIBLE);
                                    spinKitView.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    userImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.profile_image));
                                    userImage.setVisibility(View.VISIBLE);
                                    spinKitView.setVisibility(View.INVISIBLE);
                                }
                            });
                } else {
                    userImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.profile_image));
                    userImage.setVisibility(View.VISIBLE);
                    spinKitView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initComponent() {
        logout = view.findViewById(R.id.button_logout);
        spinKitView = view.findViewById(R.id.spinKit_settings);
        spinner = view.findViewById(R.id.spinner_settings_languages);
        selected = false;
        userImage = view.findViewById(R.id.imageView_settings_userImage);
        username = view.findViewById(R.id.textView_settings_userName);
    }

    private void logoutAction() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance().signOut(getActivity()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });

            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else {
            return false;
        }
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
}