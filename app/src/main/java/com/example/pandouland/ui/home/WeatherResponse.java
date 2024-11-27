package com.example.pandouland.ui.home;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("name")
    public String cityName;

    @SerializedName("main")
    public Main main;

    @SerializedName("weather")
    public Weather[] weather;

    public static class Main {
        @SerializedName("temp")
        public double temperature;
    }

    public static class Weather {
        @SerializedName("description")
        public String description;
    }
}