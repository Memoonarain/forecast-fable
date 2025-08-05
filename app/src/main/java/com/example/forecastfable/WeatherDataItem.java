package com.example.forecastfable;
public class WeatherDataItem {
    private String label;
    private String value;

    public WeatherDataItem(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}
