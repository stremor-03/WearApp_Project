package com.example.projet_wear;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WearGPS";
    final static String urlServer = "https://hmin309-embedded-systems.herokuapp.com/message-exchange/messages/";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        (new BG_Sender()).execute(new MessageSender(20400, 12.44,544.2,"Bob"));

    }

    private boolean hasGps() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    class BG_Receiver extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids)
        {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urlServer);

                urlConnection = (HttpURLConnection) url
                        .openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader isw = new InputStreamReader(in);
                int data = isw.read();
                StringBuffer s = new StringBuffer();
                while (data != -1) {
                    char current = (char) data;
                    data = isw.read();
                    s.append(current);
                }

                return s.toString();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            try {

                JSONArray user = (new JSONArray(s));

                JSONObject o = user.getJSONObject(1);
                MessageReceiver m = new MessageReceiver(o.getInt("id"),o.getInt("student_id"),o.getDouble("gps_lat"),
                        o.getDouble("gps_long"),o.getString("student_message"));

                Log.wtf(TAG,m.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class BG_Sender extends AsyncTask<MessageSender, Void, String> {

        @Override
        protected String doInBackground(MessageSender... messageSenders) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urlServer);

                urlConnection = (HttpURLConnection) url
                        .openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");

                OutputStream os = urlConnection.getOutputStream();
                os.write(messageSenders[0].getJSON().toString().getBytes("UTF-8"));
                os.close();

                Log.wtf(TAG,urlConnection.getResponseCode() + " R");
                return messageSenders[0].getJSON().toString();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }

}
