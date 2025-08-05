package com.example.forecastfable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    private ListView listView;
    private WeatherListAdapter adapter;
    private ArrayList<WeatherDataItem> weatherDataList = new ArrayList<>();
    RelativeLayout RlWeather;
    TextView cityNameTV, tempratureTV, conditionTV;
    TextInputEditText cityEdt;
    ImageView blackBgIv, searchIv, iconIV;
    ProgressBar pgBar;
String cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_details);
        cityName = getIntent().getStringExtra("name");
        RlWeather = findViewById(R.id.RLWeather);
        cityNameTV = findViewById(R.id.idTVCityName);
        blackBgIv = findViewById(R.id.idIVBlack);
        searchIv = findViewById(R.id.idIVSearch);
        cityEdt = findViewById(R.id.idEdtCity);
        pgBar = findViewById(R.id.pgBar);

        listView = findViewById(R.id.listView);
        adapter = new WeatherListAdapter(this, weatherDataList);
        listView.setAdapter(adapter);
        if (cityName.isEmpty()){
            Toast.makeText(this, "Failed to get location!", Toast.LENGTH_SHORT).show();
            cityNameTV.setText("Lahore");
            getWeatherInfo("Lahore");
        }else {
            cityNameTV.setText(cityName);
        getWeatherInfo(cityName);}
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdt.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(DetailsActivity.this, "Please Enter a City Name!", Toast.LENGTH_SHORT).show();
                }else {
                    pgBar.setVisibility(View.VISIBLE);
                    cityNameTV.setText(city);
                    cityName = city;
                    getWeatherInfo(cityName);
                    weatherDataList.clear();
                }
            }
        });
    }
    void getWeatherInfo(String CityName){
        try {
            String encodedCityName = URLEncoder.encode(cityName, "UTF-8");
            String url = "https://api.weatherapi.com/v1/forecast.json?key=4b50afc879834dec82e10349230607&q="+encodedCityName+"&days=1&aqi=yes&alerts=yes";
            RequestQueue requestQueue = Volley.newRequestQueue(DetailsActivity.this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    pgBar.setVisibility(View.GONE);
                    try {
                        String cityNam = response.getJSONObject("location").getString("name");
                        cityNameTV.setText(cityNam);
                        JSONObject current = response.getJSONObject("current");
                        int isDay = current.getInt("is_day");
                        String temprature = current.getString("temp_c");
                        String condition = current.getJSONObject("condition").getString("text");
                        String conditinIcon = current.getJSONObject("condition").getString("icon");

                        String windKmph = current.getString("wind_kph");
                        String windMph = current.getString("wind_mph");
                        String wind = windKmph+" kmph("+windMph+" Mph)";

                        String windDir = current.getString("wind_dir");
                        String windDirInt = current.getString("wind_degree");
                        String windDirec = windDirInt+"° ("+windDir+")";

                        String pressureMb = current.getString("pressure_mb");
                        String pressureIn = current.getString("pressure_in");
                        String pressure = pressureMb+" mb("+pressureIn+" in)";

                        String humidity = current.getString("humidity");
                        String  cloud = current.getString("cloud");

                        String precepMm = current.getString("precip_mm");
                        String precepIn = current.getString("precip_in");
                        String Precipitation = precepMm+" mm("+precepIn+" in)";

                        String visiblityKm = current.getString("vis_km");
                        String visiblityM = current.getString("vis_miles");
                        String visibility = visiblityKm+" km("+visiblityM+" M)";

                        weatherDataList.add(new WeatherDataItem("Temperature", temprature + "°C"));
                        weatherDataList.add(new WeatherDataItem("Condition", condition));
                        weatherDataList.add(new WeatherDataItem("Wind Speed", wind));
                        weatherDataList.add(new WeatherDataItem("Wind Direction", windDirec));
                        weatherDataList.add(new WeatherDataItem("Pressure", pressure));
                        weatherDataList.add(new WeatherDataItem("Humidity", humidity));
                        weatherDataList.add(new WeatherDataItem("Cloudy Cover", cloud));
                        weatherDataList.add(new WeatherDataItem("Precipitation", Precipitation));
                        weatherDataList.add(new WeatherDataItem("Visibility", visibility));
                        adapter.notifyDataSetChanged();
                        if (isDay == 1) {
                            //day
                            Picasso.get().load("https://images.unsplash.com/photo-1504941214544-9c1c44559ab4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=387&q=80").into(blackBgIv);
                        } else {
                            Picasso.get().load("https://images.unsplash.com/photo-1472552944129-b035e9ea3744?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=387&q=80").into(blackBgIv);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(DetailsActivity.this, "Unsupported City Name", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

    }
}