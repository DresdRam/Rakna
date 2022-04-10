package com.example.rakna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.rakna.Fragments.HomeFragemt;
import com.example.rakna.Fragments.ProfileFragment;
import com.example.rakna.Fragments.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
BottomNavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initComponent();
        homeFragmentTransaction();
        navigationViewAction();
    }
    // declare the main component
    private void initComponent(){
        navigationView =findViewById(R.id.bottom_navigation);
    }
    //First Fragment when Home Activity open
    private void homeFragmentTransaction(){
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container,new HomeFragemt()).commit();
        navigationView.setSelectedItemId(R.id.nav_home);
    }
    //this method to handle transaction between Fragments
    private void navigationViewAction(){
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment =null;
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        fragment=new HomeFragemt();
                        break;
                    case R.id.nav_profile:
                        fragment=new ProfileFragment();
                        break;
                    case R.id.nav_setting:
                        fragment=new SettingFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.body_container,fragment).commit();

                return true;
            }
        });
    }
}