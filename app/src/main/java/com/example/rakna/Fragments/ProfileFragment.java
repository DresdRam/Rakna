package com.example.rakna.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rakna.HomeActivity;
import com.example.rakna.R;
import com.example.rakna.pojo.MainViewModel;
import com.example.rakna.pojo.UserModel;
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
import com.squareup.picasso.Request;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.internal.Util;


public class ProfileFragment extends Fragment {
    CircleImageView profileImage;
    TextInputEditText name, email, password, phone;
    TextView username;
    Button update;
    View view;
    Uri imageUri;
    StorageReference storageReference;
    DatabaseReference reference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private MainViewModel mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initComponent();
        name.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        phone.addTextChangedListener(textWatcher);
        profileImageAction();
        return view;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (name.getText().length() > i2 || password.getText().length() > i2
                    || phone.getText().length() > i2 || password.getText().length() > i2) {
                update.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        retrieveData();
    }

    private void retrieveData() {
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.init();
        mainViewModel.getProfile().observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                username.setText(userModel.getUserName());
                name.setText(userModel.getUserName());
                email.setText(userModel.getUserEmail());
                password.setText(userModel.getUserPassword());
                phone.setText(userModel.getUserPhone());
                if (userModel.getUri() != null) {
                    Picasso.get().load(userModel.getUri()).into(profileImage);
                } else {
                    profileImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.profile_image));
                }
            }

        });
    }

    private void initComponent() {
        profileImage = view.findViewById(R.id.profile_image);
        name = view.findViewById(R.id.edit_name);
        email = view.findViewById(R.id.edit_email);
        password = view.findViewById(R.id.edit_password);
        phone = view.findViewById(R.id.edit_phone);
        username = view.findViewById(R.id.retrieved_name);
        update = view.findViewById(R.id.update_button);
    }

    private void profileImageAction() {
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean pick = true;
                if (pick == true) {
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
        boolean res2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res2;
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

    private void addToStorage(Uri uri) {
        storageReference = FirebaseStorage.getInstance().getReference().child("MyImages");
        final StorageReference imageName = storageReference.child("Image1234");
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

}