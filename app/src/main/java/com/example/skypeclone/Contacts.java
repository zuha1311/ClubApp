package com.example.skypeclone;

public class Contacts {

    String name,profileImage,bio,uid;

    public Contacts() {
    }

    public Contacts(String name, String profileImage, String bio, String uid) {
        this.name = name;
        this.profileImage = profileImage;
        this.bio = bio;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
