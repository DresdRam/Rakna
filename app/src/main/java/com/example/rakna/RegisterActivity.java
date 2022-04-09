package com.example.rakna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity{
TextView loginText;
FirebaseAuth auth = FirebaseAuth.getInstance();
FirebaseDatabase database;
DatabaseReference ref;
TextInputEditText name,phone,email,password;
Button signUp;
String get_name,get_phone,get_email,get_password;
UserModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponent();
        loginTextAction();
        signUpAction();
    }

    //authentication with Email & password
    public void createUser(){

        if (get_name.isEmpty()){
            name.setError("Name is Required");
            return;
        }if (get_email.isEmpty()) {
            email.setError("Email is Required");
            return;
        }if (get_password.isEmpty()){
            password.setError("Password is Required");
            return;
        }if (get_phone.isEmpty()){
            phone.setError("phone is Required");
            return;
        }if(nameIsValid(get_name)==false){
            name.setError("Please Enter Valid Name");
            return;
        }if (phoneIsValid(get_phone)==false){
            phone.setError("Please Enter Valid Number Phone");
            return;
        }
        auth.createUserWithEmailAndPassword(get_email,get_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Your Register is Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this,HomeActivity.class));
                    finish();
                }else {
                    Toast.makeText(RegisterActivity.this, "Error !"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Store User information in Realtime database
    public void storeData(){
        initComponent();
        model=new UserModel(auth.getCurrentUser().getUid(),get_name,get_password,get_email,get_phone);
        ref.child(auth.getCurrentUser().getUid()).setValue(model);
    }

    //declare Main Component
    public void initComponent(){
        loginText=findViewById(R.id.registerIntent_btn);
        database=FirebaseDatabase.getInstance();
        ref=database.getReference("Users");
        name=findViewById(R.id.reg_name);
        email=findViewById(R.id.reg_email);
        password=findViewById(R.id.reg_password);
        phone=findViewById(R.id.reg_phone);
        signUp=findViewById(R.id.signUp_btn);
    }
    //this method to get text from EditText
    public void get(){
        get_name=name.getText().toString();
        get_email=email.getText().toString();
        get_password=password.getText().toString();
        get_phone=phone.getText().toString();
    }
    //regex for name
    private boolean nameIsValid(String s) {
        return Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z])$").matcher(s).matches();
    }
    //regex for phone
    private boolean phoneIsValid(String s) {
        return Pattern.compile("^01[0125][0-9]{8}").matcher(s).matches();
    }
    //intent to Login Activity
    public void loginTextAction(){
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });
    }
    //action to sign up button
    public void signUpAction(){
     signUp.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            get();
            createUser();
            storeData();
         }
     });
    }
}