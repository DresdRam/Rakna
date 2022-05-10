package com.example.rakna.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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
import com.google.protobuf.Any;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Map;
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
    private ActivityResultLauncher<Any> activityResultLauncher;
    private ActivityResultLauncher<String[]> mPermissionResult;


    private ActivityResultContract<Any, Uri> cropActivityResultContract;

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
        initActivityResultLauncher();
        initActivityResultPermission();
        return view;
    }

    private void initActivityResultPermission() {
        mPermissionResult = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                    }
                });
    }

    private void initActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(cropActivityResultContract, new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    imageUri = result;
                    Picasso.get().load(imageUri).into(profileImage);
                    addToStorage(imageUri);
                }
            }
        });
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
        cropActivityResultContract = new ActivityResultContract<Any, Uri>() {
            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, Any input) {
                return CropImage.activity().setAspectRatio(20, 20).getIntent(getActivity());
            }

            @Override
            public Uri parseResult(int resultCode, @Nullable Intent intent) {
                try {
                    return CropImage.getActivityResult(intent).getUri();
                } catch (Exception e) {
                    return null;
                }
            }
        };
    }

    private void retrieveData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                name.setText(userModel.getUserName());
                password.setText(userModel.getUserPassword());
                phone.setText(userModel.getUserPhone());
                username.setText(userModel.getUserName());
                userEmail.setText(userModel.getUserEmail());
                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        update.setEnabled(!name.getText().toString().equals(userModel.getUserName()) || !password.getText().toString().equals(userModel.getUserPassword())
                                || !phone.getText().toString().equals(userModel.getUserPhone()));
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
                update.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void profileImageAction() {
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkStoragePermission() && !checkCameraPermission()) {
                    requestStoragePermission();
                    requestCameraPermission();
                } else pickImage();
            }
        });
    }

    private void updateButtonAction() {
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nameIsValid(name.getText().toString())) {
                    name.setError(getString(R.string.enterVName));
                    return;
                }
                if (!phoneIsValid(phone.getText().toString())) {
                    phone.setError(getString(R.string.enterVNumber));
                    return;
                }
                if (name.getText().toString().isEmpty()) {
                    name.setError(getString(R.string.enterYName));
                    return;
                }
                if (phone.getText().toString().isEmpty()) {
                    phone.setError(getString(R.string.enterYPhone));
                    return;
                }
                if (password.getText().toString().isEmpty()) {
                    password.setError(getString(R.string.enterYpassord));
                    return;
                }
                if (password.length() <= 6) {
                    password.setError(getString(R.string.passordShort));
                    spinKitView.setVisibility(View.INVISIBLE);
                    return;
                }
                reference = FirebaseDatabase.getInstance().
                        getReference("Users").child(auth.getCurrentUser().getUid());
                reference.child("userName").setValue(name.getText().toString());
                reference.child("userPhone").setValue(phone.getText().toString());
                reference.child("userPassword").setValue(password.getText().toString());
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
        mPermissionResult.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    private void requestCameraPermission() {
        mPermissionResult.launch(new String[]{Manifest.permission.CAMERA});
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
        activityResultLauncher.launch(null);
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