package com.lobxy.instagramclone.Model;

public class UserRegister {

    public UserRegister() {
    }

    String device_token, fullName, uid, userName, profileUrl, description;

    public UserRegister(String device_token, String fullName, String uid, String userName, String profileUrl, String description) {
        this.device_token = device_token;
        this.fullName = fullName;
        this.uid = uid;
        this.userName = userName;
        this.profileUrl = profileUrl;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
