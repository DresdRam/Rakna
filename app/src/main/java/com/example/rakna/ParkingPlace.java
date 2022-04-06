package com.example.rakna;

import java.util.ArrayList;

public class ParkingPlace {
    private String address;
    private Double latitude;
    private Double longitude;
    private int total;
    private int free;
    private int columns;
    private ArrayList<Boolean> positions;

    public  ParkingPlace(){

    }

    public ParkingPlace(String address, Double latitude, Double longitude, int total, int free, int columns, ArrayList<Boolean> positions){
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.total = total;
        this.free = free;
        this.columns = columns;
        this.positions = positions;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public ArrayList<Boolean> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<Boolean> positions) {
        this.positions = positions;
    }
}
