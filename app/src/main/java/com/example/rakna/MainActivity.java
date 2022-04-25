package com.example.rakna;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    TextView noInternetTV;
    Button retryBtn;
    SpinKitView spinKitView;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setAppLanguage(this);
        setContentView(R.layout.activity_main);

        initComponents();
        setRetryBtnListener();

        if (isInternetAvailable()) {
            createSplashHandler();
        } else {
            relativeLayout.setVisibility(View.VISIBLE);
            noInternetTV.setVisibility(View.VISIBLE);
        }

    }

    private void setRetryBtnListener() {
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryBtn.setVisibility(View.INVISIBLE);
                spinKitView.setVisibility(View.VISIBLE);
                if(isInternetAvailable()){
                    noInternetTV.setVisibility(View.GONE);
                    createSplashHandler();
                }else{
                    delayRetryButton();
                }
            }
        });
    }

    private void initComponents() {
        noInternetTV = findViewById(R.id.textView_internet_error);
        retryBtn = findViewById(R.id.button_retry);
        spinKitView = findViewById(R.id.progress_splash_button);
        relativeLayout = findViewById(R.id.layout_button);
    }

    private void delayRetryButton(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                retryBtn.setVisibility(View.VISIBLE);
                spinKitView.setVisibility(View.INVISIBLE);
            }
        }, 2000);
    }

    private void createSplashHandler() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }, 3000);
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null){
            return networkInfo.isConnected();
        }else
            return false;
    }
}