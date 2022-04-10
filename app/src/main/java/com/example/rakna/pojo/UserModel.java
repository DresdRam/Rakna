package com.example.rakna.pojo;

public class UserModel {
    private String userUid;
    private String userName;
    private String userPassward;
    private String userEmail;
    private String userPhone;

    public UserModel(String userUid, String userName, String userPassward, String userEmail, String userPhone) {
        this.userUid = userUid;
        this.userName = userName;
        this.userPassward = userPassward;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
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

    public String getUserPassward() {
        return userPassward;
    }

    public void setUserPassward(String userPassward) {
        this.userPassward = userPassward;
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
}
