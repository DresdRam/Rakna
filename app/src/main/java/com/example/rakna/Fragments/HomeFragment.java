package com.example.rakna.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.rakna.Pojo.CarSpot;
import com.example.rakna.Pojo.ParkingPlace;
import com.example.rakna.ParkingPlaceActivity;
import com.example.rakna.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class HomeFragment extends Fragment implements RoutingListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {

    private static final String TAG = "MapsFragment";
    private final String GOOGLE_API_KEY = "AIzaSyAG9B9kFnwVFhS19WeWv27t-PNrixyYssg";
    private final String PARKING_PLACE = "Parking Place";
    private Routing routing;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private ActivityResultLauncher<String[]> mPermissionResult;
    private AlertDialog permissionDialog;
    private Marker currentClickedMarker;
    private List<Polyline> polyLines = null;
    private ArrayList<LatLng> placesCoordinates;
    private MapBottomSheetFragment bottomSheetFragment;
    private boolean dialogIsShown;
    private boolean firstLaunch;
    private Location userLastKnownLocation;
    View view;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Marker userLocationMarker;
    Circle userLocationAccuracyCircle;
    String[] permissionsArray;


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
            if (mMap != null) {
                userLastKnownLocation = locationResult.getLastLocation();
                setUserLocationMarker(locationResult.getLastLocation());
                if (firstLaunch) {
                    zoomToUserLocation();
                    firstLaunch = false;
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initComponents();
        initActivityResultLauncher();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_google_map);
        mapFragment.getMapAsync(this);
        createDummyData();
        return view;
    }

    private void createDummyData() {
        CarSpot carSpot = new CarSpot(0, true, "Mahmoud Salah");
        CarSpot carSpot1 = new CarSpot(1, false);
        CarSpot carSpot2 = new CarSpot(2, true, "Mohammed Hossam");
        CarSpot carSpot3 = new CarSpot(3, false);
        CarSpot carSpot4 = new CarSpot(4, true, "Mahmoud Magdy");
        ArrayList<CarSpot> carSpots = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        users.add("carSpot");
        users.add("");
        users.add("");
        users.add("");
        users.add("carSpot4");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Parking").child("Parked").child("14 Abou Bakr El-Sedeek, Al Bitash Sharq, Dekhela, Alexandria Governorate");
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Booking").child("14 Abou Bakr El-Sedeek, Al Bitash Sharq, Dekhela, Alexandria Governorate");
        reference.setValue(users);
    }

    public void zoomToNearestMarker() {
        if (placesCoordinates != null || placesCoordinates.size() > 0) {
            Double minDistance = null;
            LatLng nearestMarker = null;
            for (LatLng latLng :
                    placesCoordinates) {
                Double distance = getDistanceInMeter(latLng);
                if (distance != -1) {
                    if (minDistance != null) {
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestMarker = latLng;
                        }
                    } else {
                        minDistance = distance;
                        nearestMarker = latLng;
                    }
                }
            }
            if (nearestMarker != null) {
                zoomToMarker(nearestMarker);
            }
        }
    }

    public void zoomToMarker(LatLng latLng) {
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16), 600, null);
        }
    }

    private Double getDistanceInMeter(LatLng markerLatLng) {
        if (userLastKnownLocation != null) {
            Location endPoint = new Location("locationB");
            endPoint.setLatitude(markerLatLng.latitude);
            endPoint.setLongitude(markerLatLng.longitude);

            return (Double) (double) userLastKnownLocation.distanceTo(endPoint);

        } else return (double) -1;
    }

    private void initComponents() {
        dialogIsShown = false;
        firstLaunch = true;
        placesCoordinates = new ArrayList<>();
        permissionsArray = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        geocoder = new Geocoder(getActivity());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void initActivityResultLauncher() {
        mPermissionResult = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        boolean coarseLoc = false;
                        boolean fineLoc = false;
                        for (Map.Entry<String, Boolean> entry :
                                result.entrySet()) {
                            if (entry.getKey().equals(permissionsArray[0])) {
                                fineLoc = entry.getValue();
                            } else {
                                coarseLoc = entry.getValue();
                            }
                            Log.i("PERMISSIONS", entry.getKey());
                            Log.i("PERMISSIONS", entry.getValue().toString());
                        }
                        if (!coarseLoc && !fineLoc) {
                            showAlertDialog();
                        } else {
                            enableUserLocation();
                            zoomToUserLocation();
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        loadPlaces();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setUserLocationMarker(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_car_location));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            userLocationMarker = mMap.addMarker(markerOptions);
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, mMap.getCameraPosition().zoom));
        } else {
            //use the previously created marker
            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(location.getBearing());
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mMap.getCameraPosition().zoom));
        }

        if (userLocationAccuracyCircle == null) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.strokeWidth(4);
            circleOptions.strokeColor(Color.argb(255, 30, 189, 255));
            circleOptions.fillColor(Color.argb(32, 30, 189, 255));
            circleOptions.radius(location.getAccuracy());
            userLocationAccuracyCircle = mMap.addCircle(circleOptions);
        } else {
            userLocationAccuracyCircle.setCenter(latLng);
            userLocationAccuracyCircle.setRadius(location.getAccuracy());
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            mPermissionResult.launch(permissionsArray);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }


    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16), 600, null);
                }
            });
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "onMapLongClick: " + latLng.toString());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(streetAddress)
                        .draggable(true)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void navigateToParkingPlace() {
        dismissBottomSheet();
        cancelRouting();
        route();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (dialogIsShown) {
                permissionDialog.dismiss();
            }
        }
    }

    private void route() {
        LatLng start = new LatLng(userLocationMarker.getPosition().latitude, userLocationMarker.getPosition().longitude);
        LatLng end = new LatLng(currentClickedMarker.getPosition().latitude, currentClickedMarker.getPosition().longitude);
        Log.e("Start", start.latitude + "," + start.longitude);
        Log.e("End", end.latitude + "," + end.longitude);
        routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .alternativeRoutes(true)
                .withListener(this)
                .key(GOOGLE_API_KEY)
                .waypoints(start, end)
                .build();
        routing.execute();
    }

    private void cancelRouting() {
        if (routing != null) {
            routing.cancel(true);
        }
    }

    public void spectateParkingPlace() {
        dismissBottomSheet();
        Intent intent = new Intent(getActivity(), ParkingPlaceActivity.class);
        intent.putExtra("ParkingPlaceAddress", currentClickedMarker.getSnippet());
        startActivity(intent);
    }

    private void loadPlaces() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Parking").child("Parking Locations");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap :
                        snapshot.getChildren()) {
                    ParkingPlace parkingPlace = snap.getValue(ParkingPlace.class);
                    placesCoordinates.add(new LatLng(parkingPlace.getLatitude(), parkingPlace.getLongitude()));
                    createPlaceMarker(parkingPlace);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Can Not Load Data From RTD");
            }
        });
    }

    private void createPlaceMarker(ParkingPlace parkingPlace) {
        LatLng latLng = new LatLng(parkingPlace.getLatitude(), parkingPlace.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(PARKING_PLACE);
        markerOptions.snippet(parkingPlace.getAddress());
        markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_parking_location_icon));
        mMap.addMarker(markerOptions);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (Objects.equals(marker.getTitle(), PARKING_PLACE)) {
            currentClickedMarker = marker;
            showBottomSheet();
        }
        return true;
    }

    private void showBottomSheet() {
        bottomSheetFragment = new MapBottomSheetFragment();
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), "BottomSheetFragment");
    }

    private void dismissBottomSheet() {
        if (bottomSheetFragment != null) {
            bottomSheetFragment.dismiss();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d(TAG, "onMarkerDragStart: ");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d(TAG, "onMarkerDrag: ");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d(TAG, "onMarkerDragEnd: ");
        LatLng latLng = marker.getPosition();
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                marker.setTitle(streetAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(getActivity().getResources().getString(R.string.permissionsNeeded))
                .setMessage(getActivity().getResources().getString(R.string.thisAppNeedsLocationPermission))
                .setPositiveButton(getActivity().getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getActivity().getResources().getString(R.string.decline), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                    }
                });
        permissionDialog = dialogBuilder.create();
        permissionDialog.show();
        dialogIsShown = true;
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        route();
        Log.e("Error", e.getMessage());
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routes, int shortestRouteIndex) {
        if (polyLines != null) {
            polyLines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();

        polyLines = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {

            if (i == shortestRouteIndex) {
                polyOptions.color(getResources().getColor(R.color.red));
                polyOptions.width(5);
                polyOptions.addAll(routes.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polyLines.add(polyline);

            }
        }
    }

    @Override
    public void onRoutingCancelled() {
        route();
    }

}