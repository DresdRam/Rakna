package com.example.rakna.pojo;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Repository {
    private UserModel model = new UserModel();
    private static Repository instance;
    private Context context;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public MutableLiveData<UserModel> getData() {
        FirebaseUser user = auth.getCurrentUser();
        final MutableLiveData<UserModel> data = new MutableLiveData<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().
                getReference("Users").child(user.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    model.setUserName(snapshot.child("userName").getValue(String.class));
                    model.setUserEmail(snapshot.child("userEmail").getValue(String.class));
                    model.setUserPassword(snapshot.child("userPassword").getValue(String.class));
                    model.setUserPhone(snapshot.child("userPhone").getValue(String.class));
                    model.setUri(snapshot.child("uri").getValue(String.class));
                    data.setValue(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return data;
    }

}
