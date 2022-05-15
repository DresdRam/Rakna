package com.example.rakna.Pojo;

public class Favorite {

    String location, totalSpots;
    Double latitude, longitude;

    public Favorite() {
    }

    public Favorite(String location, String totalSpots, double latitude, double longitude) {
        this.location = location;
        this.totalSpots = totalSpots;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTotalSpots() {
        return totalSpots;
    }

    public void setTotalSpots(String totalSpots) {
        this.totalSpots = totalSpots;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
