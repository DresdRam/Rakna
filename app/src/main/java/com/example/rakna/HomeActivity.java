package com.example.rakna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.rakna.Fragments.HomeFragment;
import com.example.rakna.Fragments.ProfileFragment;
import com.example.rakna.Fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class HomeActivity extends AppCompatActivity implements BottomSheetCommunicator {

    BottomNavigationView navigationView;
    FrameLayout frameLayout;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private SettingsFragment settingsFragment;
    private int lastItemId;
    private boolean checkedConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setAppLanguage(HomeActivity.this);
        setContentView(R.layout.activity_home);

        initComponent();
        connectionThreadInit();
        addFragmentsToManager();
        navigationViewAction();
    }

    // declare the main component
    private void initComponent() {
        navigationView = findViewById(R.id.bottom_navigation);
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        if (profileFragment == null) {
            profileFragment = new ProfileFragment();
        }
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
        }
        navigationView.setSelectedItemId(R.id.nav_home);
        lastItemId = R.id.nav_home;
        checkedConnection = false;
        frameLayout = findViewById(R.id.body_container);
    }

    //First Fragment when Home Activity open
    private void addFragmentsToManager() {
        getSupportFragmentManager().beginTransaction().add(R.id.body_container, profileFragment, "ProfileFragment").commit();
        getSupportFragmentManager().beginTransaction().add(R.id.body_container, settingsFragment, "SettingsFragment").commit();
        getSupportFragmentManager().beginTransaction().add(R.id.body_container, homeFragment, "HomeFragment").commit();
    }

    //this method to handle transaction between Fragments
    private void navigationViewAction() {
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    if (lastItemId != item.getItemId()) {
                        getSupportFragmentManager().beginTransaction().show(homeFragment).commit();
                        getSupportFragmentManager().beginTransaction().hide(profileFragment).commit();
                        getSupportFragmentManager().beginTransaction().hide(settingsFragment).commit();
                        lastItemId = item.getItemId();
                    }
                } else if (item.getItemId() == R.id.nav_profile) {
                    if (lastItemId != item.getItemId()) {
                        getSupportFragmentManager().beginTransaction().hide(homeFragment).commit();
                        getSupportFragmentManager().beginTransaction().show(profileFragment).commit();
                        getSupportFragmentManager().beginTransaction().hide(settingsFragment).commit();
                        lastItemId = item.getItemId();
                    }
                } else if (item.getItemId() == R.id.nav_setting) {
                    if (lastItemId != item.getItemId()) {
                        getSupportFragmentManager().beginTransaction().hide(homeFragment).commit();
                        getSupportFragmentManager().beginTransaction().hide(profileFragment).commit();
                        getSupportFragmentManager().beginTransaction().show(settingsFragment).commit();
                        lastItemId = item.getItemId();
                    }
                }
                return true;
            }
        });
    }

    private void connectionThreadInit() {
        Thread thread = new Thread() {
            public void run() {
                while(true){
                    if(!checkedConnection){
                        if (!isOnline()) {
                            checkedConnection = true;
                            Snackbar snackbar = Snackbar.make(frameLayout, "", Snackbar.LENGTH_INDEFINITE);
                            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                @Override
                                public void onShown(Snackbar transientBottomBar) {
                                    super.onShown(transientBottomBar);
                                    transientBottomBar.getView().findViewById(R.id.snackbar_action).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (isOnline()) {
                                                snackbar.dismiss();
                                                checkedConnection = false;
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
                    }
                }
            }
        };

        thread.start();
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void navigate() {
        homeFragment.navigateToParkingPlace();
    }

    @Override
    public void spectate() {
        homeFragment.spectateParkingPlace();
    }
}