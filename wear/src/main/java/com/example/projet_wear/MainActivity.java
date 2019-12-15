package com.example.projet_wear;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.AnalogClock;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.wear.widget.BoxInsetLayout.*;
import androidx.wear.widget.WearableRecyclerView;

import androidx.wear.widget.WearableLinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private static final String TAG = "WearGPS";
    private SensorManager sensorManager;

    private static final float SHAKE_THRESHOLD = 1.1f;
    private static final int SHAKE_WAIT_TIME_MS = 250;
    private long mShakeTime = 0;

    RequestQueue requestQueue;
    private String URLHTTP;
    WearableRecyclerView recyclerView;
    ArrayList<MessageReceiver> menuItems;
    MainMenuAdapter mainMenuAdapter;
    CustomAnalogClock customAnalogClock;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);
        URLHTTP = getResources().getString(R.string.urlServer);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if(hasGps())
            sendCoords();

        recyclerView = findViewById(R.id.main_menu_view);
        customAnalogClock = (CustomAnalogClock) findViewById(R.id.analog_clock);
        customAnalogClock.init(MainActivity.this, R.drawable.default_face, R.drawable.default_hour_hand, R.drawable.default_minute_hand, 0, false, false);
        customAnalogClock.setScale(1.1f);

        recyclerView.setEdgeItemsCenteringEnabled(true);
        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this));

        menuItems = new ArrayList<>();

        httpGET(URLHTTP);

        mainMenuAdapter = new MainMenuAdapter(this, menuItems);
        recyclerView.setAdapter(mainMenuAdapter);

        setAmbientEnabled();
    }

    protected void sendCoords() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Location l = locationResult.getLastLocation();

                        MessageSender m = new MessageSender(2188,l.getLatitude(),l.getLongitude(),"hello, it's QueenB !");
                        httpPOST(URLHTTP,m);
                    }

                },
                Looper.myLooper()).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.wtf(TAG,e.toString());
            }
        });
    }

    private boolean hasGps() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    public void httpGET(final String url){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            ArrayList<MessageReceiver> items = new ArrayList<>();
                            JSONArray user = (new JSONArray(response));

                            for(int id = 0; id < user.length(); id++) {
                                JSONObject o = user.getJSONObject(id);

                                items.add(new MessageReceiver(o.getInt("id"), o.getInt("student_id"), o.getDouble("gps_lat"),
                                        o.getDouble("gps_long"), o.getString("student_message")));
                            }

                            for(MessageReceiver m : new ArrayList<>(items)){
                                if(containMessage(m.getId(), menuItems)){//new items ?
                                    menuItems.add(m);
                                }
                            }

                            for(MessageReceiver m : new ArrayList<>(menuItems)){
                                if(containMessage(m.getId(), items)){//new items ?
                                    menuItems.remove(m);
                                }
                            }

                            mainMenuAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Log.wtf(TAG,"Response ERROR from server "+responseBody);
                        }catch (Exception e){
                            Log.wtf(TAG,e.toString());
                        }
                    }
                });

        requestQueue.add(stringRequest);
    }

    boolean containMessage(int id,ArrayList<MessageReceiver> list){

        for(MessageReceiver m : list){
            if(m.getId() == id)
                return false;
        }

        return true;
    }

    public void httpPOST(final String url,final MessageSender message){

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, url, message.getJSON(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + " i am queen");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    Log.wtf(TAG,"Response ERROR from server "+responseBody);
                }catch (Exception e){
                    Log.wtf(TAG,e.toString());
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        requestQueue.add(jsonObjReq);
    }

    private void detectShake(SensorEvent event) {
        long now = System.currentTimeMillis();

        if((now - mShakeTime) > SHAKE_WAIT_TIME_MS) {
            mShakeTime = now;

            float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
            float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
            float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;

            float gForce = (float)Math.sqrt(Math.pow(gX,2) + Math.pow(gY,2)+ Math.pow(gZ,2));

            if(gForce > SHAKE_THRESHOLD) {
                Log.wtf(TAG,"shake");
                httpGET(URLHTTP);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            detectShake(event);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {

        super.onPause();
        sensorManager.unregisterListener(this);

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        mainMenuAdapter.changeAmbient();

        findViewById(R.id.bg_menu).setBackgroundColor(ContextCompat.getColor(this,
                R.color.bg_color_enable));

        findViewById(R.id.bg_menu).setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

        recyclerView.setVisibility(View.GONE);
        customAnalogClock.setVisibility(View.VISIBLE);

        Log.wtf("TAG","Mode On");
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();

        customAnalogClock.setAutoUpdate(true);


    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        mainMenuAdapter.changeAmbient();

        findViewById(R.id.bg_menu).setBackgroundColor(ContextCompat.getColor(this,
                R.color.bg_color_disenable));

        findViewById(R.id.bg_menu).setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        recyclerView.setVisibility(View.VISIBLE);
        customAnalogClock.setVisibility(View.GONE);

        httpGET(URLHTTP);
        mainMenuAdapter.notifyDataSetChanged();

        Log.wtf("TAG","Mode Off");
    }
}
