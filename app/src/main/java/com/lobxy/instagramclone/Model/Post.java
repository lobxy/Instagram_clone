package com.lobxy.instagramclone.Model;

public class Post {

    public Post() {
    }

    private String time, imageDownloadUrl, caption, postId, fullName, profilePicImageUrl, uid;

    public Post(String time, String imageDownloadUrl, String caption, String postId, String fullName, String profilePicImageUrl, String uid) {
        this.time = time;
        this.imageDownloadUrl = imageDownloadUrl;
        this.caption = caption;
        this.postId = postId;
        this.fullName = fullName;
        this.profilePicImageUrl = profilePicImageUrl;
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageDownloadUrl() {
        return imageDownloadUrl;
    }

    public void setImageDownloadUrl(String imageDownloadUrl) {
        this.imageDownloadUrl = imageDownloadUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePicImageUrl() {
        return profilePicImageUrl;
    }

    public void setProfilePicImageUrl(String profilePicImageUrl) {
        this.profilePicImageUrl = profilePicImageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
