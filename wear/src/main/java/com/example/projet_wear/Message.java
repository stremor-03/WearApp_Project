package com.example.projet_wear;

import android.location.Location;

import java.io.Serializable;

public abstract class Message implements Serializable {

    protected int id;
    protected int student_id;
    protected transient Location location = null;
    private double latitude;
    private double longitude;
    protected String student_message;

    public Message(double latitude, double longitude){
        location = new Location("Target");
        this.latitude = latitude;
        this.longitude = longitude;
        location.setLatitude(latitude);
        location.setLongitude(longitude);
    }

    public int getId() {
        return id;
    }

    public int getStudent_id() {
        return student_id;
    }

    public double getGps_lat() {
        return location.getLatitude();
    }

    public double getGps_long() {
        return location.getLongitude();
    }

    public Location getLocation() {
        if(location == null){
            location = new Location("Target");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
        }
        return location;
    }

    public String getMessage() {
        return student_message;
    }
}
