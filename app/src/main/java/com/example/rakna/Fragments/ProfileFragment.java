package com.example.rakna.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rakna.HomeActivity;
import com.example.rakna.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
TextInputEditText name,email,password,phone;
TextView username;
View view;
Uri imageUri;
StorageReference storageReference;
DatabaseReference reference;
FirebaseAuth auth=FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_profile, container, false);
        initComponent();
        profileImageAction();
        return view;
    }
    private void initComponent(){
        profileImage =view.findViewById(R.id.profile_image);
        name=view.findViewById(R.id.edit_name);
        email=view.findViewById(R.id.edit_email);
        password=view.findViewById(R.id.edit_password);
        phone=view.findViewById(R.id.edit_phone);
        username=view.findViewById(R.id.retrieved_name);
    }
    private void profileImageAction(){
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean pick=true;
                if(pick==true){
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                        }else pickImage();
                }else {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    }else pickImage();
                }
            }
        });
    }
    private void requestStoragePermission(){
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
    }
    private void requestCameraPermission(){
        requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
    }
    private boolean checkCameraPermission(){
        boolean res1= ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean res2=ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res1&&res2;
    }

    private boolean checkStoragePermission(){
        boolean res2=ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res2;
    }
    private void pickImage(){
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

    private void addToStorage(Uri uri){
        storageReference= FirebaseStorage.getInstance().getReference().child("MyImages");
        final StorageReference imageName=storageReference.child("Image1234");
        imageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        reference= FirebaseDatabase.getInstance()
                                .getReference("users").child(auth.getCurrentUser().getUid());
                        reference.setValue(uri.toString());

                    }
                });
            }
        });
    }

}