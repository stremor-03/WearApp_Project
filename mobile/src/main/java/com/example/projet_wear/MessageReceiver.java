package com.example.projet_wear;

public class MessageReceiver extends Message {

    public MessageReceiver(int id, int student_id, double gps_lat, double gps_long, String student_message) {
        super(gps_lat,gps_long);
        this.id = id;
        this.student_id = student_id;
        this.student_message = student_message;
    }

    public MessageReceiver() {
        super(5.5, 6.6);
        this.id = 0;
        this.student_id = 0;
        this.student_message = "test";
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
