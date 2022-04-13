package com.example.rakna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.rakna.Fragments.HomeFragment;
import com.example.rakna.Fragments.ProfileFragment;
import com.example.rakna.Fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setAppLanguage(HomeActivity.this);
        setContentView(R.layout.activity_home);
        initComponent();
        HomeFragment homeFragment = new HomeFragment();
        SettingsFragment settingsFragment = new SettingsFragment();
        ProfileFragment profileFragment = new ProfileFragment();
        homeFragmentTransaction();
        navigationViewAction(homeFragment, profileFragment, settingsFragment);
    }

    // declare the main component
    private void initComponent() {
        navigationView = findViewById(R.id.bottom_navigation);
    }

    //First Fragment when Home Activity open
    private void homeFragmentTransaction() {
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new HomeFragment()).commit();
        navigationView.setSelectedItemId(R.id.nav_home);
    }

    //this method to handle transaction between Fragments
    private void navigationViewAction(HomeFragment homeFragment, ProfileFragment profileFragment, SettingsFragment settingsFragment) {
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        fragment = homeFragment;
                        break;
                    case R.id.nav_profile:
                        fragment = profileFragment;
                        break;
                    case R.id.nav_setting:
                        fragment = settingsFragment;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();

                return true;
            }
        });
    }
}