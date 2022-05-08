package com.example.rakna.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rakna.R;
import com.example.rakna.pojo.MainViewModel;
import com.example.rakna.pojo.UserModel;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    SpinKitView spinKitView;
    CircleImageView profileImage;
    TextInputEditText name, password, phone;
    TextView username, userEmail;
    Button update;
    View view;
    Uri imageUri;
    StorageReference storageReference;
    DatabaseReference reference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private MainViewModel mainViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!isConnected()) {
            Toast.makeText(context, "Oops, Check Your Connection Please!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initComponent();
        update.setEnabled(false);
        retrieveData();
        profileImageAction();
        updateButtonAction();
        return view;
    }

    private void initComponent() {
        spinKitView = view.findViewById(R.id.spinKit_profile);
        profileImage = view.findViewById(R.id.profile_image);
        name = view.findViewById(R.id.edit_name);
        password = view.findViewById(R.id.edit_password);
        phone = view.findViewById(R.id.edit_phone);
        username = view.findViewById(R.id.retrieved_name);
        update = view.findViewById(R.id.update_button);
        userEmail = view.findViewById(R.id.retrieved_email);
    }

    private void retrieveData() {
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.init();
        mainViewModel.getProfile().observe(getViewLifecycleOwner(), new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                username.setText(userModel.getUserName());
                name.setText(userModel.getUserName());
                userEmail.setText(userModel.getUserEmail());
                password.setText(userModel.getUserPassword());
                phone.setText(userModel.getUserPhone());
                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        if (name.getText().toString().equals(userModel.getUserName()) && password.getText().toString().equals(userModel.getUserPassword())
                                && phone.getText().toString().equals(userModel.getUserPhone())) {
                            update.setEnabled(false);
                        } else {
                            update.setEnabled(true);
                        }
                    }


                    @Override
                    public void afterTextChanged(Editable editable) {


                    }
                };
                name.addTextChangedListener(textWatcher);
                password.addTextChangedListener(textWatcher);
                phone.addTextChangedListener(textWatcher);
                if (userModel.getUri() != null) {
                    Picasso.get().load(userModel.getUri()).into(profileImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            spinKitView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            profileImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.profile_image));
                            spinKitView.setVisibility(View.GONE);
                        }
                    });
                } else {
                    profileImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.profile_image));
                    spinKitView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void profileImageAction() {
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean pick = true;
                if (pick) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else pickImage();
                } else {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else pickImage();
                }
            }
        });
    }

    private void updateButtonAction() {
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nameIsValid(name.getText().toString())) {
                    name.setError("Enter Valid Name");
                    return;
                }
                if (!phoneIsValid(phone.getText().toString())) {
                    phone.setError("Enter Valid Number");
                    return;
                }
                if (name.getText().toString().isEmpty()) {
                    name.setError("Enter Your Name");
                    return;
                }
                if (phone.getText().toString().isEmpty()) {
                    phone.setError("Enter Your Phone");
                    return;
                }
                if (password.getText().toString().isEmpty()) {
                    password.setError("Enter Your Password");
                    return;
                }
                reference = FirebaseDatabase.getInstance().
                        getReference("Users").child(auth.getCurrentUser().getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child("userName").getValue(String.class).equals(name.getText().toString()) ||
                                !snapshot.child("userPhone").getValue(String.class).equals(phone.getText().toString())
                                || !snapshot.child("userPassword").getValue(String.class).equals(password.getText().toString())) {
                            update.setEnabled(false);
                            reference.child("userName").setValue(name.getText().toString());
                            reference.child("userPhone").setValue(phone.getText().toString());
                            reference.child("userPassword").setValue(password.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), "Data is Updated ", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            Toast.makeText(getActivity(), "You Add Same Data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });


    }

    //regex for name
    private boolean nameIsValid(String s) {
        return Pattern.compile("^[a-zA-Z]{4,}(?: [a-zA-Z]+){0,2}$").matcher(s).matches();
    }

    //regex for phone
    private boolean phoneIsValid(String s) {
        return Pattern.compile("^01[0125][0-9]{8}").matcher(s).matches();
    }

    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res1 && res2;
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void pickImage() {
        CropImage.activity()
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                imageUri = result.getUri();
                Picasso.get().load(imageUri).into(profileImage);
                addToStorage(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver resolver = getActivity().getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(resolver.getType(uri));
    }

    private void addToStorage(Uri uri) {
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        final StorageReference imageName = storageReference.child(System.currentTimeMillis() + "." + getExtension(uri));
        imageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        reference = FirebaseDatabase.getInstance()
                                .getReference("Users").child(auth.getCurrentUser().getUid()).child("uri");
                        reference.setValue(uri.toString());
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
}