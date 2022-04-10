package com.example.rakna;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity  {
TextView registerTxt;
ImageView signWithGoogle;
GoogleSignInOptions gso;
GoogleSignInClient gsc;
TextInputEditText email,password;
Button logIn;
String get_email,get_password;
FirebaseAuth auth=FirebaseAuth.getInstance();
SpinKitView spinKitView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponent();
        get();
        createRequest();
        registerTxtAction();
        signWithGoogleAction();
        logInAction();
    }
    //check user is already logged in
    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser()!=null){
            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();
        }
    }

    //Log in button Action
    private void logInAction(){
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinKitView.setVisibility(View.VISIBLE);
                signInWithFirebase();
            }
        });

    }
    //Sign in with Google Button
    private void signWithGoogleAction(){
        signWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }
    //Intent to Register Activity
    private void registerTxtAction(){
        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });
    }
    //declare Main Component
    private void initComponent(){
        registerTxt=findViewById(R.id.loginIntent_btn);
        signWithGoogle=findViewById(R.id.signWithGoogle);
        email=findViewById(R.id.log_email);
        password=findViewById(R.id.log_password);
        logIn=findViewById(R.id.log_Btn);
        spinKitView=findViewById(R.id.spin_kit);
    }
    //this method to get text from EditText
    private void get(){
        get_email=email.getText().toString();
        get_password=password.getText().toString();
    }
    //authentication with Email & password
    private void signInWithFirebase(){
        get();
        if(get_email.isEmpty()){
            email.setError("Email is Required");
            spinKitView.setVisibility(View.INVISIBLE);
            return;
        }if (get_password.isEmpty()){
            password.setError("Password is Required");
            spinKitView.setVisibility(View.INVISIBLE);
            return;
        }
        auth.signInWithEmailAndPassword(get_email,get_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Your Login is Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    spinKitView.setVisibility(View.INVISIBLE);
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this, "Error !"+task.getException(), Toast.LENGTH_SHORT).show();
                   spinKitView.setVisibility(View.INVISIBLE);
                }
            }
        });

    }
    //create Request to Sign in with google
    private void createRequest(){
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this,gso);
    }
    //sign in with google
    private void signIn(){
        Intent intent=gsc.getSignInIntent();
        startActivityForResult(intent,100);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            Task<GoogleSignInAccount> task =GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                finish();
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
}