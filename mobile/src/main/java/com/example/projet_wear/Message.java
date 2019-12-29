package com.example.projet_wear;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.Serializable;

public abstract class Message implements Serializable {

    protected int id;
    protected int student_id;
    protected transient Location location = null;
    private double latitude;
    private double longitude;
    protected String student_message;
    int backgroundID;

    public Message(double latitude, double longitude){
        location = new Location("Target");
        this.latitude = latitude;
        this.longitude = longitude;
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        if(this.student_id == MainActivity.myStudentID){
            backgroundID = R.drawable.shape_bg_outgoing_bubble;
        }else{
            backgroundID = R.drawable.shape_bg_incoming_bubble;
        }

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

    @Override
    public String toString() {
        return String.valueOf(this.student_id);
    }

    public void setMessageView(Context context, MainMenuAdapter.RecyclerViewHolder holder){

        holder.messageView.setBackground(context.getDrawable(this.backgroundID));
        holder.studentIDView.setBackground(context.getDrawable(this.backgroundID));

        if(this.getStudent_id() == MainActivity.myStudentID){

            holder.studentIDView.setVisibility(View.GONE);
            holder.messageView.setVisibility(View.GONE);

            holder.studentIDView_R.setVisibility(View.VISIBLE);
            holder.messageView_R.setVisibility(View.VISIBLE);

            holder.studentIDView_R.setText(String.valueOf(getStudent_id()));
            holder.messageView_R.setText(String.valueOf(getMessage()));
        }else{
            holder.studentIDView.setText(String.valueOf(getStudent_id()));
            holder.messageView.setText(String.valueOf(getMessage()));
        }


    }
}
