package com.example.rakna;

import com.google.android.gms.maps.model.LatLng;

public interface HomeCommunicator {

    void navigateToParkingLocation();

    void spectateParkingLocation();

    void zoomToParkingLocation(LatLng latLng);

}
