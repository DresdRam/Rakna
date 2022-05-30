package com.example.rakna.Pojo;

public class CarSpot {
    private int spotNumber;
    private boolean isBooked;
    private String userName;

    public CarSpot() {
    }

    public CarSpot(int spotNumber, boolean isBooked) {
        this.spotNumber = spotNumber;
        this.isBooked = isBooked;
    }

    public CarSpot(int spotNumber, boolean isBooked, String userName) {
        this.spotNumber = spotNumber;
        this.isBooked = isBooked;
        this.userName = userName;
    }

    public int getSpotNumber() {
        return spotNumber;
    }

    public void setSpotNumber(int spotNumber) {
        this.spotNumber = spotNumber;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
