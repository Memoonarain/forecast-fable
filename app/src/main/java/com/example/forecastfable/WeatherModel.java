package com.example.forecastfable;

public class WeatherModel {
    private String time, temp, icon, windSpeed;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public WeatherModel(String time, String temp, String icon, String windSpeed) {
        this.time = time;
        this.temp = temp;
        this.icon = icon;
        this.windSpeed = windSpeed;
    }
}
