package com.example.projet_wear;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    private String URLHTTP;
    ArrayList<MessageReceiver> menuItems;
    private MainMenuAdapter mainMenuAdapter;
    RecyclerView recyclerView;

    private static final String TAG = "Mobile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.main_menu_view);

        requestQueue = Volley.newRequestQueue(this);
        URLHTTP = getResources().getString(R.string.urlServer);

        menuItems = new ArrayList<>();

        httpGET(URLHTTP);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainMenuAdapter = new MainMenuAdapter(this, menuItems);
        recyclerView.setAdapter(mainMenuAdapter);

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
                            Log.wtf(TAG,"send request");

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

    boolean containMessage(int id,ArrayList<MessageReceiver> list){

        for(MessageReceiver m : list){
            if(m.getId() == id)
                return false;
        }

        return true;
    }
}
