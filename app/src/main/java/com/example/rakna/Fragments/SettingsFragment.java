package com.example.rakna.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.rakna.BookingQRActivity;
import com.example.rakna.HomeActivity;
import com.example.rakna.LocaleHelper;
import com.example.rakna.LoginActivity;
import com.example.rakna.R;
import com.example.rakna.Pojo.User;
import com.example.rakna.SettingsCommunicator;
import com.firebase.ui.auth.AuthUI;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
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
    TextView username, qrcodeTxt;
    ImageView imageViewQr;
    CircleImageView userImage;
    SwitchMaterial trafficModeSwitch;
    private boolean selected, trafficModeEnabled;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        initComponent();
        setTrafficModeSwitch();
        setPreviousSelectedLang();
        setUserInfo();
        logoutAction();
        qrcodeTxtAction();
        imageViewQrAction();

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

        trafficModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("My_PREFERENCES", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("TrafficModeEnabled", b);
                editor.apply();
                SettingsCommunicator communicator = (SettingsCommunicator) getActivity();
                communicator.changeTrafficMode(b);
            }
        });

        return view;
    }

    private void setTrafficModeSwitch() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("My_PREFERENCES", Context.MODE_PRIVATE);
        trafficModeEnabled = sharedPreferences.getBoolean("TrafficModeEnabled", false);
        trafficModeSwitch.setChecked(trafficModeEnabled);
    }

    private void setUserInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                username.setText(user.getUserName());
                userImage.setVisibility(View.INVISIBLE);
                spinKitView.setVisibility(View.VISIBLE);
                if (user.getUri() != null) {
                    Picasso.get()
                            .load(user.getUri())
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
        qrcodeTxt = view.findViewById(R.id.qr_text);
        imageViewQr = view.findViewById(R.id.qr_image);
        trafficModeSwitch = view.findViewById(R.id.switch_traffic_mode);
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

    private void setPreviousSelectedLang() {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String language = shared.getString(LocaleHelper.SELECTED_LANGUAGE, LocaleHelper.ENGLISH);
        if (language.equals(LocaleHelper.ENGLISH)) {
            spinner.setSelection(0);
        } else if (language.equals(LocaleHelper.ARABIC)) {
            spinner.setSelection(1);
        }
    }

    private void qrcodeTxtAction() {
        qrcodeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BookingQRActivity.class));

            }
        });

    }

    private void imageViewQrAction() {
        imageViewQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BookingQRActivity.class));
            }
        });

    }

    private void setTrafficEnabled() {

    }
}