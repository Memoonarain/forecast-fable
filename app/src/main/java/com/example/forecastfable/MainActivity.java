package com.example.forecastfable;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    RelativeLayout RlWeather;
    TextView cityNameTV, tempratureTV, conditionTV;
    TextInputEditText cityEdt;
    ImageView blackBgIv, searchIv, iconIV;
    ProgressBar pgBar;
    RecyclerView recyclerView;
    private ArrayList<WeatherModel> weatherModelArrayList = new ArrayList<>();
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName="";
    Button buttonDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_main);
        buttonDetails = findViewById(R.id.btnDetails);
        RlWeather = findViewById(R.id.RLWeather);
        cityNameTV = findViewById(R.id.idTVCityName);
        tempratureTV = findViewById(R.id.idTVTemprature);
        conditionTV = findViewById(R.id.idTVCondition);
        blackBgIv = findViewById(R.id.idIVBlack);
        searchIv = findViewById(R.id.idIVSearch);
        cityEdt = findViewById(R.id.idEdtCity);
        iconIV = findViewById(R.id.idIVIcon);
        pgBar = findViewById(R.id.pgBar);
        recyclerView = findViewById(R.id.rView);

        weatherAdapter = new WeatherAdapter(MainActivity.this, weatherModelArrayList);
        recyclerView.setAdapter(weatherAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(MainActivity.this, SplashScreen.class));
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            cityName = getCityName(location.getLongitude(), location.getLatitude());
            cityNameTV.setText(cityName);
            getWeatherInfo(cityName);
        } else {
            getWeatherInfo("lahore");
            Toast.makeText(this, "Failed to get Location, Maybe location is turned of or Your are not connected to Internet", Toast.LENGTH_SHORT).show();
            // Handle the case where location is null, show an error or request location updates
        }
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdt.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter a City Name!", Toast.LENGTH_SHORT).show();
                }else {
                    cityNameTV.setText(city);
                    pgBar.setVisibility(View.VISIBLE);
                    cityName = city;
                    getWeatherInfo(cityName);
                }
            }
        });
        buttonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DetailsActivity.class).putExtra("name", cityName));
            }
        });

    }

    private String getCityName(double longitude, double latitude){
        String cityName = "Loading..";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
            for (Address adr: addresses){
                if (adr != null) {
                    String city = adr.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                    }

                }
                if (cityName=="Loading.."){
                     cityName= "Not found";
                        Log.d(TAG, "getCityName: CITY NOT FOUND");
                        Toast.makeText(this, "User City Not Found...", Toast.LENGTH_SHORT).show();

                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }
    private void getWeatherInfo(String cityName){
        try {
            String encodedCityName = URLEncoder.encode(cityName, "UTF-8");
            String url = "https://api.weatherapi.com/v1/forecast.json?key=4b50afc879834dec82e10349230607&q="+encodedCityName+"&days=1&aqi=yes&alerts=yes";
            Log.d(TAG, "getWeatherInfo: "+url);
            cityNameTV.setText(cityName);
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("WeatherResponse", response.toString());  // response is the JSON string

                    pgBar.setVisibility(View.GONE);
                    RlWeather.setVisibility(View.VISIBLE);
                    weatherModelArrayList.clear();

                    try {
                        String cityNam = response.getJSONObject("location").getString("name");
                        cityNameTV.setText(cityNam);
                        String temprature = response.getJSONObject("current").getString("temp_c");
                        tempratureTV.setText(temprature + "°C");
                        int isDay = response.getJSONObject("current").getInt("is_day");
                        String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                        String conditinIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                        Picasso.get().load("http:".concat(conditinIcon)).into(iconIV);
                        conditionTV.setText(condition);
                        if (isDay == 1) {
                            //day
                            Picasso.get().load("https://images.unsplash.com/photo-1504941214544-9c1c44559ab4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=387&q=80").into(blackBgIv);
                        } else {
                            Picasso.get().load("https://images.unsplash.com/photo-1472552944129-b035e9ea3744?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=387&q=80").into(blackBgIv);
                        }
                        JSONObject forecastObj = response.getJSONObject("forecast");
                        JSONObject forecast0  = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                        JSONArray hourArray = forecast0.getJSONArray("hour");

                        for (int i = 0; i < hourArray.length(); i++){
                            JSONObject hourObj = hourArray.getJSONObject(i);
                            String time = hourObj.getString("time");
                            String temper = hourObj.getString("temp_c");
                            String img = hourObj.getJSONObject("condition").getString("icon");
                            String wind = hourObj.getString("wind_kph");
                            weatherModelArrayList.add(new WeatherModel(time, temper, img, wind));
                        }
                        weatherAdapter.notifyDataSetChanged();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Something went wrong, Maybe you have entered wrong city name...", Toast.LENGTH_SHORT).show();

                    Log.d("WeatherResponse", error.getMessage().toString());
                    Log.d("WeatherResponse", error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(MainActivity.this, "Unsupported City Name", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

    }

}