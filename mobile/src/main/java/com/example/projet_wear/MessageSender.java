package com.example.projet_wear;

import org.json.JSONObject;

public class MessageSender extends Message {

    public MessageSender(int student_id, double gps_lat, double gps_long, String message) {
        super(gps_lat,gps_long);
        this.student_id = student_id;
        this.student_message = message;
    }

    public JSONObject getJSON(){

        JSONObject o = new JSONObject();

        try {
            o.put("student_id", getStudent_id());
            o.put("gps_lat", getLocation().getAltitude());
            o.put("gps_long", getLocation().getLongitude());
            o.put("student_message", getMessage());
        }catch(Exception e){
            e.printStackTrace();
        }

        return o;
    }
}
