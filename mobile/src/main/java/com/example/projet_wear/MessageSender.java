package com.example.projet_wear;

import org.json.JSONObject;

public class MessageSender extends Message {

    public MessageSender(int student_id, double gps_lat, double gps_long, String message) {

        this.student_id = student_id;
        this.gps_lat = gps_lat;
        this.gps_long = gps_long;
        this.student_message = message;
    }

    public JSONObject getJSON(){

        JSONObject o = new JSONObject();

        try {

            o.put("student_id", student_id);
            o.put("gps_lat", gps_lat);
            o.put("gps_long", gps_long);
            o.put("student_message", student_message);
        }catch(Exception e){
            e.printStackTrace();
        }

        return o;
    }
}
