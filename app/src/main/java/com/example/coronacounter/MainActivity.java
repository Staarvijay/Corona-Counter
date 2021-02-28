package com.example.coronacounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);

        try {
            DownloadTask task = new DownloadTask();
            task.execute("https://coronavirus-tracker-api.herokuapp.com/v2/locations");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String locationsinfo = jsonObject.getString("locations");
                Log.i("location info",locationsinfo);
                JSONArray arr = new JSONArray(locationsinfo);
                String message = "";
                for(int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String country = jsonPart.getString("country");
                    String countryPopulation = jsonPart.getString("country_population");
                    //String lastUpdated = jsonPart.getString("last_updated");
                    String Province = jsonPart.getString("province");



                    int confirmed = 0;
                    int deaths = 0;
                    int recovered = 0;
                    if (jsonPart.has("latest")) {
                        JSONObject latestObject = jsonPart.getJSONObject("latest");
                        confirmed = latestObject.getInt("confirmed");
                        deaths = latestObject.getInt("deaths");
                        recovered = latestObject.getInt("recovered");
                    }


                    message += country.toUpperCase() + "\r\n" + "Country Population: " + countryPopulation + "\r\n" ;
                    if(!Province.isEmpty()){
                        message += "Province: " + Province + "\r\n";
                    }
                    message += /*"Last Updated:" + lastUpdated + "\r\n" +*/ "Confirmed: " + confirmed + "\r\n" + "Deaths: " + deaths + "\r\n" + "Recovered: " + recovered + "\r\n" + "\r\n";

                    textView.setText(message);
                    textView.setMovementMethod(new ScrollingMovementMethod());
                }
//
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
}
