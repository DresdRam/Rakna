package com.example.rakna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.rakna.Fragments.FavoritesFragment;
import com.example.rakna.Fragments.HomeFragment;
import com.example.rakna.Fragments.ProfileFragment;
import com.example.rakna.Fragments.SettingsFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class HomeActivity extends AppCompatActivity implements HomeCommunicator {

    BottomNavigationView navigationView;
    FrameLayout frameLayout;
    FloatingActionButton nearbyBtn;
    private Thread connectionThread;
    private int lastItemId;
    private boolean checkedConnection;
    final HomeFragment homeFragment = new HomeFragment();
    final FavoritesFragment favoritesFragment = new FavoritesFragment();
    final ProfileFragment profileFragment = new ProfileFragment();
    final SettingsFragment settingsFragment = new SettingsFragment();
    final FragmentManager supportFragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setAppLanguage(this);
        setContentView(R.layout.activity_home);
        initComponent();
        initConnectionThread();
        addFragmentsToManager();
        navigationViewAction();
        setNearbyBtnListener();
    }

    private void setNearbyBtnListener() {
        nearbyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeFragment.zoomToNearestMarker();
            }
        });
    }

    // declare the main component
    private void initComponent() {
        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.nav_home);
        frameLayout = findViewById(R.id.body_container);
        nearbyBtn = findViewById(R.id.floatingActionButton_nearby);
        lastItemId = R.id.nav_home;
        checkedConnection = false;
    }

    //First Fragment when Home Activity open
    private void addFragmentsToManager() {
        supportFragmentManager.beginTransaction().addToBackStack(null).add(R.id.body_container, favoritesFragment, "FavoritesFragment").hide(favoritesFragment).commit();
        supportFragmentManager.beginTransaction().addToBackStack(null).add(R.id.body_container, profileFragment, "ProfileFragment").hide(profileFragment).commit();
        supportFragmentManager.beginTransaction().addToBackStack(null).add(R.id.body_container, settingsFragment, "SettingsFragment").hide(settingsFragment).commit();
        supportFragmentManager.beginTransaction().addToBackStack(null).add(R.id.body_container, homeFragment, "HomeFragment").commit();
    }

    //this method to handle transaction between Fragments
    private void navigationViewAction() {
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    if (lastItemId != item.getItemId()) {
                        showHomeFragment(item.getItemId());
                    }
                }
                else if (item.getItemId() == R.id.nav_favorites) {
                    if (lastItemId != item.getItemId()) {
                        showFavoritesFragment(item.getItemId());
                    }
                }
                else if (item.getItemId() == R.id.nav_profile) {
                    if (lastItemId != item.getItemId()) {
                        showProfileFragment(item.getItemId());
                    }
                } else if (item.getItemId() == R.id.nav_setting) {
                    if (lastItemId != item.getItemId()) {
                        showSettingsFragment(item.getItemId());
                    }
                }
                return true;
            }
        });
    }

    private void showHomeFragment(int id){
        supportFragmentManager.beginTransaction().show(homeFragment).commit();
        supportFragmentManager.beginTransaction().hide(favoritesFragment).commit();
        supportFragmentManager.beginTransaction().hide(profileFragment).commit();
        supportFragmentManager.beginTransaction().hide(settingsFragment).commit();
        nearbyBtn.setVisibility(View.VISIBLE);
        lastItemId = id;
    }

    private void showFavoritesFragment(int id){
        supportFragmentManager.beginTransaction().hide(homeFragment).commit();
        supportFragmentManager.beginTransaction().show(favoritesFragment).commit();
        supportFragmentManager.beginTransaction().hide(profileFragment).commit();
        supportFragmentManager.beginTransaction().hide(settingsFragment).commit();
        nearbyBtn.setVisibility(View.GONE);
        lastItemId = id;
    }

    private void showProfileFragment(int id){
        supportFragmentManager.beginTransaction().hide(homeFragment).commit();
        supportFragmentManager.beginTransaction().hide(favoritesFragment).commit();
        supportFragmentManager.beginTransaction().show(profileFragment).commit();
        supportFragmentManager.beginTransaction().hide(settingsFragment).commit();
        nearbyBtn.setVisibility(View.GONE);
        lastItemId = id;
    }

    private void showSettingsFragment(int id){
        supportFragmentManager.beginTransaction().hide(homeFragment).commit();
        supportFragmentManager.beginTransaction().hide(favoritesFragment).commit();
        supportFragmentManager.beginTransaction().hide(profileFragment).commit();
        supportFragmentManager.beginTransaction().show(settingsFragment).commit();
        nearbyBtn.setVisibility(View.GONE);
        lastItemId = id;
    }

    private void initConnectionThread() {
        connectionThread = new Thread() {
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

        connectionThread.start();
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.setAppLanguage(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isOnline()){
            initConnectionThread();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(connectionThread != null){
            connectionThread.interrupt();
            checkedConnection = false;
        }
    }

    @Override
    public void navigateToParkingLocation() {
        homeFragment.navigateToParkingPlace();
    }

    @Override
    public void spectateParkingLocation() {
        homeFragment.spectateParkingPlace();
    }

    @Override
    public void zoomToParkingLocation(LatLng latLng){
        showHomeFragment(R.id.nav_home);
        navigationView.setSelectedItemId(R.id.nav_home);
        homeFragment.zoomToMarker(latLng);
    }
}