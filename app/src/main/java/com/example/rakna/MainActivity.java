package com.example.rakna;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;

    private long milliseconds, splashTime;
    private boolean splashActive, paused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setAppLanguage(this);
        setContentView(R.layout.activity_main);

        initComponents();
        initConnectionThread();
    }

    private void initConnectionThread() {
        Thread thread = new Thread() {
            public void run(){
                try {
                    while(splashActive && milliseconds < splashTime){
                        if(!paused){
                            milliseconds += 100;
                            sleep(100);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    if(!isOnline()){
                        Snackbar snackbar = Snackbar.make(relativeLayout, "", Snackbar.LENGTH_INDEFINITE);
                        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            @Override
                            public void onShown(Snackbar transientBottomBar) {
                                super.onShown(transientBottomBar);
                                transientBottomBar.getView().findViewById(R.id.snackbar_action).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(isOnline()){
                                            goToHome();
                                            snackbar.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                        snackbar.setText(getResources().getString(R.string.noInternetConnection))
                                .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                })
                                .setTextColor(getResources().getColor(R.color.white))
                                .setActionTextColor(getResources().getColor(R.color.white))
                                .setBackgroundTint(getResources().getColor(R.color.red));
                        snackbar.show();
                    }
                    else {
                        goToHome();
                    }
                }
            }
        };

        thread.start();
    }

    private void goToHome(){
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void initComponents() {
        milliseconds = 0;
        splashTime = 900;
        splashActive = true;
        paused = false;
        relativeLayout = findViewById(R.id.main_relative_layout);
    }
}