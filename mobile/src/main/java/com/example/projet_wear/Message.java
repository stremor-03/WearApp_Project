package com.example.projet_wear;

public abstract class Message {

    protected int id;
    protected int student_id;
    protected double gps_lat;
    protected double gps_long;
    protected String student_message;

    public int getId() {
        return id;
    }

    public int getStudent_id() {
        return student_id;
    }

    public double getGps_lat() {
        return gps_lat;
    }

    public double getGps_long() {
        return gps_long;
    }

    public String getMessage() {
        return student_message;
    }
}
