package com.example.projet_wear;

public class MessageReceiver extends Message {

    public MessageReceiver(int id, int student_id, double gps_lat, double gps_long, String student_message) {
        this.id = id;
        this.student_id = student_id;
        this.gps_lat = gps_lat;
        this.gps_long = gps_long;
        this.student_message = student_message;
    }

    @Override
    public String toString() {
        return "MessageReceiver{" +
                "id=" + id +
                ", student_id=" + student_id +
                ", gps_lat=" + gps_lat +
                ", gps_long=" + gps_long +
                ", message='" + student_message + '\'' +
                '}';
    }


}
