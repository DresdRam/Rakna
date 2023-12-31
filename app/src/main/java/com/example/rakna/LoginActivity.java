package com.example.rakna;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rakna.Pojo.User;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    TextView registerTxt;
    ImageView signWithGoogle;
    TextInputEditText email, password;
    Button logIn;
    String get_email, get_password;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    SpinKitView spinKitView;
    ActivityResultLauncher<Intent> activityResultLauncher;
    LoadingDialog loadingDialog;
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

        if (!isConnected()) {
            showDialog();
        }
        if (auth.getCurrentUser() != null) {
            Toast.makeText(this, R.string.alreadyLogin, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }

    private void checkUser() {


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
        loadingDialog = new LoadingDialog(this);
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
        loadingDialog.show(getResources().getString(R.string.signingIn));
        auth.signInWithEmailAndPassword(get_email, get_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, R.string.loginSuccess, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    spinKitView.setVisibility(View.INVISIBLE);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.error) + task.getException(), Toast.LENGTH_SHORT).show();
                    spinKitView.setVisibility(View.INVISIBLE);
                    loadingDialog.dismiss();
                }
            }
        });

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        loadingDialog.show(getResources().getString(R.string.signingIn));
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String personName = acct.getDisplayName();
                            String personEmail = acct.getEmail();
                            Uri personPhoto = acct.getPhotoUrl();
                            User user = new User(personName, personEmail, "+0000000000", personPhoto.toString(), getRandomKey(16));
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                            Query query = databaseReference.orderByChild("userEmail").equalTo(personEmail);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        updateFirebaseData(user);
                                    } else {
                                        Toast.makeText(LoginActivity.this, R.string.emailAlready, Toast.LENGTH_SHORT).show();
                                        goToHome();
                                    }
                                    goToHome();
                                    loadingDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    loadingDialog.dismiss();
                                }
                            });
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.authenticathinFaild, Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, R.string.authenticathinFaild, Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        Toast.makeText(LoginActivity.this, R.string.authenticationPass, Toast.LENGTH_SHORT).show();
        LoginActivity.this.finish();
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

    private void updateFirebaseData(User user) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(auth.getCurrentUser().getUid()).setValue(user);
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Please check Your Internet Connection")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
        builder.show();
    }

    private String getRandomKey(int sizeOfKeyString) {
        Random random = new Random();
        String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
        StringBuilder sb = new StringBuilder(sizeOfKeyString);

        for (int i = 0; i < sizeOfKeyString; ++i) {
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));

        }
        return sb.toString();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.setAppLanguage(this);
    }

}