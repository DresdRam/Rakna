package com.example.rakna.Pojo;

public class User {
    private String userUid;
    private String userName;
    private String userPassword;
    private String userEmail;
    private String userPhone;
    private String uri;
    private boolean booked;

    public User() {
    }

    public User(String userName, String userEmail, String userPhone, String uri) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.uri = uri;
    }

    public User(String userName, String userPassword, String userPhone) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.userPhone = userPhone;
    }

    public User(String userUid, String userName, String userPassword, String userEmail, String userPhone) {
        this.userUid = userUid;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
    }

    public User(String userUid, String userName, String userPassword, String userEmail, String userPhone, boolean booked) {
        this.userUid = userUid;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.booked = booked;
    }

    public User(String userUid, String userName, String userPassword, String userEmail, String userPhone, String uri) {
        this.userUid = userUid;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.uri = uri;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }
}
