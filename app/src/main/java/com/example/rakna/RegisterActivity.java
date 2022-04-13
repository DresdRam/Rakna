package com.example.rakna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rakna.pojo.UserModel;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    TextView loginText;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference ref;
    TextInputEditText name, phone, email, password;
    Button signUp;
    String get_name, get_phone, get_email, get_password;
    UserModel model;
    FirebaseUser user;
    SpinKitView spinKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setAppLanguage(RegisterActivity.this);
        setContentView(R.layout.activity_register);
        initComponent();
        loginTextAction();
        signUpAction();
    }

    //authentication with Email & password
    private void createUser() {

        if (get_name.isEmpty()) {
            name.setError("Name is Required");
            spinKitView.setVisibility(View.INVISIBLE);
            return;
        }
        if (get_email.isEmpty()) {
            email.setError("Email is Required");
            spinKitView.setVisibility(View.INVISIBLE);
            return;
        }
        if (get_password.isEmpty()) {
            password.setError("Password is Required");
            spinKitView.setVisibility(View.INVISIBLE);
            return;
        }
        if (get_password.length() <= 6) {
            password.setError("Password is to Short ");
            spinKitView.setVisibility(View.INVISIBLE);
            return;
        }
        if (get_phone.isEmpty()) {
            phone.setError("phone is Required");
            return;
        }
        if (!nameIsValid(get_name)) {
            name.setError("Please Enter Valid Name");
            spinKitView.setVisibility(View.INVISIBLE);
            return;
        }
        if (!phoneIsValid(get_phone)) {
            phone.setError("Please Enter Valid Number Phone");
            spinKitView.setVisibility(View.INVISIBLE);
            return;
        }
        if (!emailIsValid(get_email)) {
            email.setError("Please Enter Valid Email");
            spinKitView.setVisibility(View.INVISIBLE);
            return;
        }
        auth.createUserWithEmailAndPassword(get_email, get_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    storeData();
                    Toast.makeText(RegisterActivity.this, "Your Register is Successful", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                    spinKitView.setVisibility(View.INVISIBLE);
                    //finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error !" + task.getException(), Toast.LENGTH_SHORT).show();
                    spinKitView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    //Store User information in Realtime database
    private void storeData() {
        get();
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users");
        model = new UserModel(user.getUid(), get_name, get_password, get_email, get_phone, "");
        ref.child(user.getUid()).setValue(model);
    }

    //declare Main Component
    private void initComponent() {
        loginText = findViewById(R.id.registerIntent_btn);
        name = findViewById(R.id.reg_name);
        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_password);
        phone = findViewById(R.id.reg_phone);
        signUp = findViewById(R.id.signUp_btn);
        spinKitView = findViewById(R.id.spin_kit2);
    }

    //this method to get text from EditText
    private void get() {
        get_name = name.getText().toString();
        get_email = email.getText().toString();
        get_password = password.getText().toString();
        get_phone = phone.getText().toString();
    }

    //regex for name
    private boolean nameIsValid(String s) {
        return Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z])$").matcher(s).matches();
    }

    //regex for phone
    private boolean phoneIsValid(String s) {
        return Pattern.compile("^01[0125][0-9]{8}").matcher(s).matches();
    }

    //regex for email
    private boolean emailIsValid(String s) {
        return Pattern.compile("^(.+)@(.+)$").matcher(s).matches();
    }

    //intent to Login Activity
    private void loginTextAction() {
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    //action to sign up button
    private void signUpAction() {
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                spinKitView.setVisibility(View.VISIBLE);
                get();
                createUser();
            }
        });
    }
}