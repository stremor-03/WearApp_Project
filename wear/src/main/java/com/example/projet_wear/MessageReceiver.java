package com.example.projet_wear;

import java.io.Serializable;

public class MessageReceiver extends Message implements Serializable {

    public MessageReceiver(int id, int student_id, double gps_lat, double gps_long, String student_message) {
        super(gps_lat,gps_long);
        this.id = id;
        this.student_id = student_id;
        this.student_message = student_message;
    }

    @Override
    public String toString() {
        return "MessageReceiver{" +
                "id=" + getId() +
                ", student_id=" + getStudent_id() +
                ", gps_lat=" + getLocation().getLatitude() +
                ", gps_long=" + getLocation().getLongitude() +
                ", message='" + getMessage() + '\'' +
                '}';
    }


}
