package com.example.rakna;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private ArrayList<Car> parkedCars;
    private ArrayList<Boolean> booleanParkedCars;
    private ArrayList<Boolean> tempBooleanParkedCars;
    private Toast mToast;
    private int[] carsDrawablesArray;
    private ParkingPlace parkingPlace;
    private DatabaseReference databaseReference;
    private boolean firstTimeData;
    private final int LEAVING_CAR = -1;
    private final int PARKING_CAR = 1;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_place);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        firstTimeData = false;
        random = new Random();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Parking Locations").child("Module1");

        addressTextView = findViewById(R.id.textView_address);
        freeTextView = findViewById(R.id.textView_free);
        backButton = findViewById(R.id.button_back_arrow);

        carsDrawablesArray = new int[]{R.drawable.ic_car_one, R.drawable.ic_car_two, R.drawable.ic_car_three, R.drawable.ic_car_four, R.drawable.ic_car_five};

        addressTextView.setSelected(true);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setupFireBase();
    }

    private void setupFireBase() {
        ValueEventListener positionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parkingPlace = dataSnapshot.getValue(ParkingPlace.class);
                assert parkingPlace != null;
                booleanParkedCars = parkingPlace.getPositions();
                freeTextView.setText(String.valueOf(parkingPlace.getFree()));

                if(!firstTimeData){
                    firstTimeData = true;
                    addressTextView.setText(parkingPlace.getAddress());
                    tempBooleanParkedCars = booleanParkedCars;
                    setupCarsData();
                    setupParkingRecyclerview();
                }
                else
                {
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
        for(int i = 0; i <= booleanParkedCars.size() - 1; i++){
            if(booleanParkedCars.get(i) != tempBooleanParkedCars.get(i)){
                if(booleanParkedCars.get(i)){
                    updatePlaceStatus(i, PARKING_CAR);
                }else{
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
                if(!animated){
                    if(status == LEAVING_CAR){
                        animateLeavingCar(position);
                    }
                    else
                    {
                        animateParkingCar(position);
                    }
                    animated = true;
                }
            }
        });
    }

    private void animateLeavingCar(int position){
        currentAnimatedCar = carsRecyclerview.getChildAt(position).findViewById(R.id.iv_car);
        currentAnimatedTV = carsRecyclerview.getChildAt(position).findViewById(R.id.tv_park_number);
        ObjectAnimator moveAnimation;
        if(currentAnimatedCar.getScaleX() == 1f){
            moveAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, "translationX", 50);
        }else {
            moveAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, "translationX", -50);
        }
        ObjectAnimator fadeAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, View.ALPHA, 1, 0);
        moveAnimation.setDuration(800);
        fadeAnimation.setDuration(800);
        moveAnimation.start();
        fadeAnimation.start();
    }
    private void animateParkingCar(int position){
        currentAnimatedCar = carsRecyclerview.getChildAt(position).findViewById(R.id.iv_car);
        currentAnimatedTV = carsRecyclerview.getChildAt(position).findViewById(R.id.tv_park_number);
        currentAnimatedCar.setImageResource(carsDrawablesArray[random.nextInt(5)]);
        currentAnimatedCar.setVisibility(View.VISIBLE);
        ObjectAnimator moveAnimation;
        if(currentAnimatedCar.getScaleX() == 1f){
            moveAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, "translationX", -0);
        }else {
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

        for(int i = 0; i <= booleanParkedCars.size() - 1; i++){
            parkedCars.add(new Car(booleanParkedCars.get(i), carsDrawablesArray[random.nextInt(5)]));
        }
    }

    private void setupParkingRecyclerview() {
        int columns = parkingPlace.getColumns();
        carsRecyclerview = findViewById(R.id.rv_parking);
        mAdapter = new CarsAdapter(this, parkedCars, clickListener, columns);
        carsRecyclerview.setAdapter(mAdapter);
        carsRecyclerview.setLayoutManager(new GridLayoutManager(this, columns));
    }

    ItemClickListener clickListener = new ItemClickListener() {
        @Override
        public void onItemClick(int position) {
            mToast.cancel();
            mToast.setText(String.valueOf(position));
            mToast.show();
        }
    };
}

