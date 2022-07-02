package com.example.rakna.Pojo;

public class CarSpot {
    private int spotNumber;
    private boolean isBooked;
    private String userName;
    private String uid;
    private boolean isParked;
    private String QR;

    public CarSpot(int spotNumber, boolean isBooked, String userName, String uid, boolean isParked) {
        this.spotNumber = spotNumber;
        this.isBooked = isBooked;
        this.userName = userName;
        this.uid = uid;
        this.isParked = isParked;
    }

    public CarSpot() {
    }

    public CarSpot(int spotNumber, boolean isBooked) {
        this.spotNumber = spotNumber;
        this.isBooked = isBooked;
    }

    public CarSpot(int spotNumber, boolean isBooked, String userName, String uid) {
        this.spotNumber = spotNumber;
        this.isBooked = isBooked;
        this.userName = userName;
        this.uid = uid;
    }

    public CarSpot(int spotNumber, boolean isBooked, String userName) {
        this.spotNumber = spotNumber;
        this.isBooked = isBooked;
        this.userName = userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public boolean isParked() {
        return isParked;
    }

    public void setParked(boolean parked) {
        isParked = parked;
    }

    public String getQR() {
        return QR;
    }

    public void setQR(String QR) {
        this.QR = QR;
    }
}
