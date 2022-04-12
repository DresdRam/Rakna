package com.example.rakna;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rakna.pojo.UserModel;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    TextView registerTxt;
    ImageView signWithGoogle;
    TextInputEditText email, password;
    Button logIn;
    String get_email, get_password;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    SpinKitView spinKitView;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private GoogleSignInClient mGoogleSignInClient;
    private final String TAG = "FIREBASE";
    

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

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                        if (googleSignInResult.isSuccess()) {
                            GoogleSignInAccount acct = googleSignInResult.getSignInAccount();
                            String personName = acct.getDisplayName();
                            String personEmail = acct.getEmail();
                            String personId = acct.getId();
                            Uri personPhoto = acct.getPhotoUrl();
                            String personPhoneURL = acct.getPhotoUrl().toString();
                            UserModel user = new UserModel(personId, personName, " ", personEmail, personPhoneURL, personPhoto.toString());
                            updateFirebaseData(user);
                            firebaseAuthWithGoogle(acct);
                        } else {
                            Log.i(TAG, googleSignInResult.toString());
                        }
                    }
                });
    }

    //check user is already logged in
    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }

    //Log in button Action
    private void logInAction() {
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinKitView.setVisibility(View.VISIBLE);
                signInWithFirebase();
            }
        });

    }

    //Sign in with Google Button
    private void signWithGoogleAction() {
        signWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    //Intent to Register Activity
    private void registerTxtAction() {
        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    //declare Main Component
    private void initComponent() {
        registerTxt = findViewById(R.id.loginIntent_btn);
        signWithGoogle = findViewById(R.id.signWithGoogle);
        email = findViewById(R.id.log_email);
        password = findViewById(R.id.log_password);
        logIn = findViewById(R.id.log_Btn);
        spinKitView = findViewById(R.id.spin_kit);
    }

    //this method to get text from EditText
    private void get() {
        get_email = email.getText().toString();
        get_password = password.getText().toString();
    }

    //authentication with Email & password
    private void signInWithFirebase() {
        get();
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
        auth.signInWithEmailAndPassword(get_email, get_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Your Login is Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    spinKitView.setVisibility(View.INVISIBLE);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Error !" + task.getException(), Toast.LENGTH_SHORT).show();
                    spinKitView.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        auth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication pass.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //create Request to Sign in with google
    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestProfile()
                .requestId()
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    //sign in with google
    private void signIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        activityResultLauncher.launch(intent);
    }

    private void updateFirebaseData(UserModel user) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(user.getUserUid()).setValue(user);
    }
}