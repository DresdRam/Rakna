package com.example.rakna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.rakna.Fragments.HomeFragment;
import com.example.rakna.Fragments.ProfileFragment;
import com.example.rakna.Fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity implements BottomSheetCommunicator {

    BottomNavigationView navigationView;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private SettingsFragment settingsFragment;
    private int lastItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setAppLanguage(HomeActivity.this);
        setContentView(R.layout.activity_home);

        initComponent();
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

    @Override
    public void navigate() {
        homeFragment.navigateToParkingPlace();
    }

    @Override
    public void spectate() {
        homeFragment.spectateParkingPlace();
    }
}