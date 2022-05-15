package com.example.rakna.Pojo;

public class Car {
    Boolean isBusy;
    Integer carImageResource;


    public Car(Boolean isBusy, Integer carImageResource) {
        this.isBusy = isBusy;
        this.carImageResource = carImageResource;
    }

    public Boolean getBusy() {
        return isBusy;
    }

    public void setBusy(Boolean busy) {
        isBusy = busy;
    }

    public Integer getCarImageResource() {
        return carImageResource;
    }

    public void setCarImageResource(Integer carImageResource) {
        this.carImageResource = carImageResource;
    }
}
