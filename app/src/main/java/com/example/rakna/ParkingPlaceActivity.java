package com.example.rakna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class ParkingPlaceActivity extends AppCompatActivity {

    RecyclerView carsRecyclerview;
    CarsAdapter mAdapter;
    TextView addressTextView;
    TextView freeTextView;
    ImageButton backButton;
    ImageView currentAnimatedCar;
    TextView currentAnimatedTV;
    CircularProgressIndicator loadingAnimation;
    RelativeLayout relativeLayout;
    private ArrayList<Car> parkedCars;
    private ArrayList<Boolean> booleanParkedCars;
    private ArrayList<Boolean> tempBooleanParkedCars;
    private Toast mToast;
    private int[] carsDrawablesArray;
    private ParkingPlace parkingPlace;
    private DatabaseReference databaseReference;
    private boolean firstTimeData, checkedConnection;
    private final int LEAVING_CAR = -1;
    private final int PARKING_CAR = 1;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setAppLanguage(ParkingPlaceActivity.this);
        setContentView(R.layout.activity_parking_place);

        initComponents();
        initBackBtnListener();
        initConnectionThread();
        startLoadingAnimation();
        setupFireBase();
    }

    private void initBackBtnListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initComponents() {
        addressTextView = findViewById(R.id.textView_address);
        freeTextView = findViewById(R.id.textView_free);
        backButton = findViewById(R.id.button_back_arrow);
        loadingAnimation = findViewById(R.id.circular_progress_indicator);
        relativeLayout = findViewById(R.id.parking_place_layout);
        firstTimeData = false;
        checkedConnection = false;
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        random = new Random();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Parking Locations").child(getIntent().getStringExtra("ParkingPlaceAddress"));
        carsDrawablesArray = new int[]{R.drawable.ic_car_one, R.drawable.ic_car_two, R.drawable.ic_car_three, R.drawable.ic_car_four, R.drawable.ic_car_five};
        addressTextView.setSelected(true);

    }

    private void setupFireBase() {
        ValueEventListener positionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parkingPlace = dataSnapshot.getValue(ParkingPlace.class);
                assert parkingPlace != null;
                booleanParkedCars = parkingPlace.getPositions();
                if (parkingPlace.getFree() == parkingPlace.getTotal()) {
                    freeTextView.setText(getResources().getString(R.string.full));
                    freeTextView.setTextColor(getResources().getColor(R.color.red));
                } else {
                    freeTextView.setText(String.valueOf(parkingPlace.getFree()));
                    freeTextView.setTextColor(getResources().getColor(R.color.white));
                }

                if (!firstTimeData) {
                    firstTimeData = true;
                    addressTextView.setText(parkingPlace.getAddress());
                    tempBooleanParkedCars = booleanParkedCars;
                    setupCarsData();
                    setupParkingRecyclerview();
                } else {
                    compareBetweenLists();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.addValueEventListener(positionListener);
    }

    private void compareBetweenLists() {
        for (int i = 0; i <= booleanParkedCars.size() - 1; i++) {
            if (booleanParkedCars.get(i) != tempBooleanParkedCars.get(i)) {
                if (booleanParkedCars.get(i)) {
                    updatePlaceStatus(i, PARKING_CAR);
                } else {
                    updatePlaceStatus(i, LEAVING_CAR);
                }
            }
        }
        tempBooleanParkedCars = booleanParkedCars;
    }

    private void updatePlaceStatus(int position, int status) {
        carsRecyclerview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean animated = false;

            @Override
            public void onGlobalLayout() {
                if (!animated) {
                    if (status == LEAVING_CAR) {
                        animateLeavingCar(position);
                    } else {
                        animateParkingCar(position);
                    }
                    animated = true;
                }
            }
        });
    }

    private void animateLeavingCar(int position) {
        currentAnimatedCar = carsRecyclerview.getChildAt(position).findViewById(R.id.iv_car);
        currentAnimatedTV = carsRecyclerview.getChildAt(position).findViewById(R.id.tv_park_number);
        ObjectAnimator moveAnimation;
        if (currentAnimatedCar.getScaleX() == 1f) {
            moveAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, "translationX", 50);
        } else {
            moveAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, "translationX", -50);
        }
        ObjectAnimator fadeAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, View.ALPHA, 1, 0);
        moveAnimation.setDuration(800);
        fadeAnimation.setDuration(800);
        moveAnimation.start();
        fadeAnimation.start();
    }

    private void animateParkingCar(int position) {
        currentAnimatedCar = carsRecyclerview.getChildAt(position).findViewById(R.id.iv_car);
        currentAnimatedTV = carsRecyclerview.getChildAt(position).findViewById(R.id.tv_park_number);
        currentAnimatedCar.setImageResource(carsDrawablesArray[random.nextInt(5)]);
        currentAnimatedCar.setVisibility(View.VISIBLE);
        ObjectAnimator moveAnimation;
        if (currentAnimatedCar.getScaleX() == 1f) {
            moveAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, "translationX", -0);
        } else {
            moveAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, "translationX", 0);
        }
        ObjectAnimator fadeAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, View.ALPHA, 0, 1);
        moveAnimation.setDuration(800);
        fadeAnimation.setDuration(800);
        moveAnimation.start();
        fadeAnimation.start();
    }

    private void setupCarsData() {
        Random random = new Random();

        parkedCars = new ArrayList<>();

        for (int i = 0; i <= booleanParkedCars.size() - 1; i++) {
            parkedCars.add(new Car(booleanParkedCars.get(i), carsDrawablesArray[random.nextInt(5)]));
        }
    }

    private void setupParkingRecyclerview() {
        int columns = parkingPlace.getColumns();
        carsRecyclerview = findViewById(R.id.rv_parking);
        mAdapter = new CarsAdapter(this, parkedCars, columns);
        carsRecyclerview.setAdapter(mAdapter);
        carsRecyclerview.setLayoutManager(new GridLayoutManager(this, columns));
        stopLoadingAnimation();
    }


    private void initConnectionThread() {
        Thread thread = new Thread() {
            public void run() {
                while(true){
                    if(!checkedConnection){
                        if (!isOnline()) {
                            checkedConnection = true;
                            Snackbar snackbar = Snackbar.make(relativeLayout, "", Snackbar.LENGTH_INDEFINITE);
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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.setAppLanguage(this);
    }

    private void startLoadingAnimation() {
        loadingAnimation.show();
    }

    private void stopLoadingAnimation() {
        loadingAnimation.hide();
    }
}

