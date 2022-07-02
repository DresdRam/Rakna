package com.example.rakna;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rakna.Pojo.Car;
import com.example.rakna.Pojo.Favorite;
import com.example.rakna.Pojo.CarSpot;
import com.example.rakna.Pojo.ParkingPlace;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ParkingPlaceActivity extends AppCompatActivity implements ParkingItemClickListener {

    RecyclerView carsRecyclerview;
    CarsAdapter mAdapter;
    TextView addressTextView;
    TextView freeTextView, currentAnimatedButton;
    ImageButton backButton, addToFavoritesBtn;
    ImageView currentAnimatedCar;
    Button bookBtn, cancelButton;
    CircularProgressIndicator loadingAnimation;
    RelativeLayout relativeLayout;
    private ArrayList<Car> parkedCars;
    private ArrayList<Boolean> booleanParkedCars;
    private ArrayList<Boolean> tempBooleanParkedCars;
    private ArrayList<CarSpot> bookingList;
    private Toast mToast;
    private String parkingPlaceName;
    private int[] carsDrawablesArray;
    private ParkingPlace parkingPlace;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private boolean firstTimeData, checkedConnection, isFavorite, alreadyBooked;
    private final int LEAVING_CAR = -1;
    private final int PARKING_CAR = 1;
    private Random random;
    private Favorite favorite;
    private String userName;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setAppLanguage(ParkingPlaceActivity.this);
        setContentView(R.layout.activity_parking_place);
        initComponents();

        initBackBtnListener();
        initFavoritesBtnListener();
        initConnectionThread();
        startLoadingAnimation();
        setupFireBase();
        bookBtnAction();
        cancelBookingBtnAction();
    }

    private void cancelBookingBtnAction() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllBookingInfo();
            }
        });
    }

    private void getAllBookingInfo() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String location = snapshot.child("Users").child(firebaseUser.getUid()).child("location").getValue(String.class);
                String carSpot = snapshot.child("Users").child(firebaseUser.getUid()).child("carSpot").getValue(String.class);
                if(!Objects.equals(location, "") || !Objects.equals(carSpot, "")){
                    removeBooking(location, carSpot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeBooking(String location, String carSpot) {
        databaseReference.child("Users").child(firebaseUser.getUid()).child("booked").setValue(false);
        databaseReference.child("Users").child(firebaseUser.getUid()).child("BookCode").setValue("None");
        databaseReference.child("Users").child(firebaseUser.getUid()).child("location").setValue("None");
        databaseReference.child("Users").child(firebaseUser.getUid()).child("carSpot").setValue("None");
        databaseReference.child("Booking").child(location).child(carSpot).child("booked").setValue(false);
        databaseReference.child("Booking").child(location).child(carSpot).child("uid").setValue("None");
        databaseReference.child("Booking").child(location).child(carSpot).child("QR").setValue("None");
        databaseReference.child("Booking").child(location).child(carSpot).child("userName").setValue("None");
    }

    private void initBackBtnListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initFavoritesBtnListener() {
        addToFavoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlaceToFavorites();
            }
        });
    }

    private void addPlaceToFavorites() {
        DatabaseReference favoritesReference = FirebaseDatabase.getInstance().getReference("Favorites").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (favorite != null) {
            if (isFavorite) {
                favoritesReference.child(favorite.getLocation()).setValue(null);
                Toast.makeText(this, getResources().getString(R.string.removedFromFavorites), Toast.LENGTH_LONG).show();
            } else {
                favoritesReference.child(favorite.getLocation()).setValue(favorite);
                Toast.makeText(this, getResources().getString(R.string.addedToFavorites), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initComponents() {
        addressTextView = findViewById(R.id.textView_address);
        freeTextView = findViewById(R.id.textView_free);
        bookBtn = findViewById(R.id.book_btn);
        cancelButton = findViewById(R.id.cancel_btn);
        backButton = findViewById(R.id.button_back_arrow);
        addToFavoritesBtn = findViewById(R.id.button_add_to_favorites);
        loadingAnimation = findViewById(R.id.circular_progress_indicator);
        relativeLayout = findViewById(R.id.parking_place_layout);
        firstTimeData = false;
        checkedConnection = false;
        isFavorite = false;
        parkingPlaceName = getIntent().getStringExtra("ParkingPlaceAddress");
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        random = new Random();
        bookingList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        carsDrawablesArray = new int[]{R.drawable.ic_car_one, R.drawable.ic_car_two, R.drawable.ic_car_three, R.drawable.ic_car_four, R.drawable.ic_car_five};
        addressTextView.setSelected(true);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    private void setupFireBase() {
        ValueEventListener positionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userName = snapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userName").getValue().toString();
                if (snapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("booked").exists())
                alreadyBooked = snapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("booked").getValue(boolean.class);
                if (alreadyBooked) {
                    bookBtn.setEnabled(false);
                    cancelButton.setVisibility(View.VISIBLE);
                } else {
                    bookBtn.setEnabled(true);
                    cancelButton.setVisibility(View.INVISIBLE);
                }
                parkingPlace = snapshot.child("Parking").child("Parking Locations").child(parkingPlaceName).getValue(ParkingPlace.class);
                assert parkingPlace != null;
                booleanParkedCars = parkingPlace.getPositions();


                if (parkingPlace.getFree() == parkingPlace.getTotal()) {
                    freeTextView.setText(getResources().getString(R.string.full));
                    bookBtn.setEnabled(false);
                    freeTextView.setTextColor(getResources().getColor(R.color.red));
                } else {
                    freeTextView.setText(String.valueOf(parkingPlace.getFree()));
                    freeTextView.setTextColor(getResources().getColor(R.color.white));
                }

                if (parkingPlace != null) {
                    if (snapshot.child("Favorites").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild(parkingPlaceName)) {
                        addToFavoritesBtn.setBackground(ContextCompat.getDrawable(ParkingPlaceActivity.this, R.drawable.ic_favorites));
                        isFavorite = true;
                    } else {
                        addToFavoritesBtn.setBackground(ContextCompat.getDrawable(ParkingPlaceActivity.this, R.drawable.ic_favorite_border));
                        isFavorite = false;
                    }
                }

                bookingList.clear();
                for (DataSnapshot dataSnapshot :
                        snapshot.child("Booking").child(parkingPlaceName).getChildren()) {
                    CarSpot carSpot = dataSnapshot.getValue(CarSpot.class);
                    bookingList.add(carSpot);
                }

                if (!firstTimeData) {
                    firstTimeData = true;
                    favorite = new Favorite();
                    favorite.setLocation(parkingPlace.getAddress());
                    favorite.setTotalSpots(String.valueOf(parkingPlace.getTotal()));
                    favorite.setLatitude(parkingPlace.getLatitude());
                    favorite.setLongitude(parkingPlace.getLongitude());
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
        currentAnimatedButton = carsRecyclerview.getChildAt(position).findViewById(R.id.button_book_select);
        ObjectAnimator moveAnimation;
        if (currentAnimatedCar.getScaleX() == 1f) {
            moveAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, "translationX", 50);
        } else {
            moveAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, "translationX", -50);
        }
        ObjectAnimator fadeAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, View.ALPHA, 1, 0);
        ObjectAnimator fadeAnimation2 = ObjectAnimator.ofFloat(currentAnimatedButton, View.ALPHA, 0, 1);
        moveAnimation.setDuration(800);
        fadeAnimation.setDuration(800);
        fadeAnimation2.setDuration(800);
        moveAnimation.start();
        fadeAnimation.start();
        fadeAnimation2.start();
    }

    private void animateParkingCar(int position) {
        currentAnimatedCar = carsRecyclerview.getChildAt(position).findViewById(R.id.iv_car);
        currentAnimatedButton = carsRecyclerview.getChildAt(position).findViewById(R.id.button_book_select);
        currentAnimatedCar.setImageResource(carsDrawablesArray[random.nextInt(5)]);
        currentAnimatedCar.setVisibility(View.VISIBLE);
        ObjectAnimator moveAnimation;
        if (currentAnimatedCar.getScaleX() == 1f) {
            moveAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, "translationX", -0);
        } else {
            moveAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, "translationX", 0);
        }
        ObjectAnimator fadeAnimation = ObjectAnimator.ofFloat(currentAnimatedCar, View.ALPHA, 0, 1);
        ObjectAnimator fadeAnimation2 = ObjectAnimator.ofFloat(currentAnimatedButton, View.ALPHA, 1, 0);
        moveAnimation.setDuration(800);
        fadeAnimation.setDuration(800);
        fadeAnimation2.setDuration(400);
        moveAnimation.start();
        fadeAnimation.start();
        fadeAnimation2.start();
        setBookedToFalse(position);

    }

    private void setBookedToFalse(int position) {
        CarSpot carSpot = new CarSpot(position, false);
        databaseReference.child("Booking").child(parkingPlaceName).child(String.valueOf(position)).setValue(carSpot);
    }

    private void setupCarsData() {
        Random random = new Random();

        parkedCars = new ArrayList<>();

        for (int i = 0; i < booleanParkedCars.size(); i++) {
            parkedCars.add(new Car(booleanParkedCars.get(i), carsDrawablesArray[random.nextInt(5)]));
        }
    }

    private void setupParkingRecyclerview() {
        int columns = parkingPlace.getColumns();
        carsRecyclerview = findViewById(R.id.rv_parking);
        mAdapter = new CarsAdapter(this, parkedCars, bookingList, columns, this);
        carsRecyclerview.setAdapter(mAdapter);
        carsRecyclerview.setLayoutManager(new GridLayoutManager(this, columns));
        stopLoadingAnimation();
    }


    private void initConnectionThread() {
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    if (!checkedConnection) {
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

    @Override
    public void onButtonClicked(int position) {

    }


    @Override
    public void onCarClicked(int position) {
        databaseReference.child("Parking").child("Parked").child(parkingPlaceName).child(String.valueOf(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isParked = booleanParkedCars.get(position);
                if (isParked) {
                    if (snapshot.exists()) {
                        mToast.cancel();
                        mToast.setText(getResources().getString(R.string.parkedBy) + snapshot.getValue().toString());
                        mToast.show();
                    } else {
                        mToast.cancel();
                        mToast.setText(getResources().getString(R.string.parkedBy) + " " + getResources().getString(R.string.nonRaknaUser));
                        mToast.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void checkCarSpots() {
        for (int i = 0; i < booleanParkedCars.size(); i++) {
            currentAnimatedButton = carsRecyclerview.getChildAt(i).findViewById(R.id.button_book_select);
            CarSpot carSpot = bookingList.get(i);
            if (!booleanParkedCars.get(i)) {
                if (!carSpot.isBooked()) {
                    String QR = generateRandomKey();
                    carSpot.setBooked(true);
                    carSpot.setParked(false);
                    carSpot.setUserName(userName);
                    carSpot.setUid(firebaseUser.getUid());
                    carSpot.setQR(QR);
                    checkQrCode(QR);
                    databaseReference.child("Booking").child(parkingPlaceName).child(String.valueOf(i)).setValue(carSpot);
                    databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("booked").setValue(true);
                    databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("location").setValue(parkingPlaceName);
                    databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("carSpot").setValue(String.valueOf(i));
                    currentAnimatedButton.setText(getResources().getString(R.string.booked));
                    bookBtn.setEnabled(false);
                    break;
                }
            }
        }
    }

    private void bookBtnAction() {
        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCarSpots();
            }
        });

    }

    private String generateRandomKey() {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(16);

        for (int i = 0; i < 16; ++i) {
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        return sb.toString();
    }

    private void checkQrCode(String QR) {
        databaseReference.child("Users").child(firebaseUser.getUid()).child("BookCode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    databaseReference.child("Users").child(firebaseUser.getUid()).child("BookCode").
                            setValue(QR).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    startActivity(new Intent(ParkingPlaceActivity.this,BookingQRActivity.class));
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}

