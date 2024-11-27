package com.example.pandouland.ui.home;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

    // Interface pour les appels API
    public interface WeatherApiService {
        @GET("weather")
        Call<WeatherResponse> getWeather(
                @Query("q") String cityName,
                @Query("appid") String apiKey,
                @Query("units") String units
        );
    }

