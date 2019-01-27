package com.lobxy.instagramclone.Model;

public class UserRegister {

    public UserRegister() {
    }

    String device_token, fullname, uid, password, username, imageUrl;

    public UserRegister(String device_token, String fullname, String uid, String password, String username, String imageUrl) {
        this.device_token = device_token;
        this.fullname = fullname;
        this.uid = uid;
        this.password = password;
        this.username = username;
        this.imageUrl = imageUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGender() {
        return password;
    }

    public void setGender(String gender) {
        this.password = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
