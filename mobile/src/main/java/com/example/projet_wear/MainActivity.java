package com.example.projet_wear;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MainActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    private String URLHTTP;
    ArrayDeque<MessageReceiver> queueItems = new ArrayDeque<MessageReceiver>(){

        @Override
        public boolean add(MessageReceiver messageReceiver) {
            if(this.size() >= 20)
                this.removeFirst();
            return super.add(messageReceiver);
        }
    };

    private MainMenuAdapter mainMenuAdapter;
    final static int myStudentID = 1077948453;
    private Button mSendButton;
    private EditText mMessage;
    RecyclerView recyclerView;

    int delayRefresh =5000;
    Handler refresh;
    private final Runnable fRefresher = new Runnable()
    {
        public void run()
        {
            httpGET(URLHTTP);
            refresh.postDelayed(fRefresher, delayRefresh);
        }

    };

    private static final String TAG = "Mobile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSendButton = (Button) findViewById(R.id.bt_sendmessage);
        mMessage = (EditText) findViewById(R.id.message);
        recyclerView = (RecyclerView)findViewById(R.id.main_menu_view);

        requestQueue = Volley.newRequestQueue(this);
        URLHTTP = getResources().getString(R.string.urlServer);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainMenuAdapter = new MainMenuAdapter(this, queueItems);
        recyclerView.setAdapter(mainMenuAdapter);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCoords(mMessage.getText().toString());
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                mMessage.setText(null);
            }
        });

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                linearLayoutManager.scrollToPosition(0);
            }
        });

        httpGET(URLHTTP);
        refresh = new Handler();
        refresh.postDelayed(fRefresher,delayRefresh);
    }

    public void httpGET(final String url){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            ArrayDeque<MessageReceiver> queue = new ArrayDeque<MessageReceiver>(){

                                @Override
                                public boolean add(MessageReceiver messageReceiver) {
                                    if(this.size() >= 20)
                                        this.removeFirst();
                                    return super.add(messageReceiver);
                                }
                            };

                            JSONArray user = (new JSONArray(response));

                            for(int id = 0; id < user.length(); id++) {
                                JSONObject o = user.getJSONObject(id);

                                queue.add(new MessageReceiver(o.getInt("id"), o.getInt("student_id"), o.getDouble("gps_lat"),
                                        o.getDouble("gps_long"), o.getString("student_message")));
                            }

                            boolean onChange = false;

                            for(MessageReceiver m : queue){
                                if(!containMessage(m.getId(), queueItems)){
                                    onChange = true;
                                    queueItems.add(m);
                                }
                            }

                            if(onChange)
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

    boolean containMessage(int id,ArrayDeque<MessageReceiver> queue){

        for(MessageReceiver m : queue){
            if(m.getId() == id)
                return true;
        }

        return false;
    }

    public void httpPOST(final String url,final MessageSender message){

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,
                url,
                message.getJSON(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        httpGET(url);
                    }
                },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                    JSONObject result = null;

                    if (jsonString.length() > 0)
                        result = new JSONObject(jsonString);

                    return Response.success(result,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        requestQueue.add(jsonObjReq);
    }

    protected void sendCoords(final String message) {

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

                        MessageSender m = new MessageSender(MainActivity.myStudentID,l.getLatitude(),l.getLongitude(),message);
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
}
