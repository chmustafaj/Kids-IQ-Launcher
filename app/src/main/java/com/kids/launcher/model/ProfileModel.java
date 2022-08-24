package com.kids.launcher.model;

import android.graphics.Bitmap;

public class ProfileModel {
    String userName;
    String birthDay;
    Bitmap image;

    public ProfileModel(String userName, String birthDay, Bitmap image) {
        this.userName = userName;
        this.birthDay = birthDay;
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
