package com.example.sammywifilibrary;

/**
 * Created by channa on 6/6/18.
 */

public class wifi {

    private String status;
    private int userId;
    private int deviceId;

    public wifi Authentication(int device) {
        this.deviceId = device;
        return this;
    }

    public wifi UserRegistration(int userid) {
        this.userId = userid;
        return this;
    }

    public wifi DeviceRegistration(String location , String ssid , String userid) {
        return this;
    }
}
