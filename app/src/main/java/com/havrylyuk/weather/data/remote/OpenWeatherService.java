package com.havrylyuk.weather.data.remote;

import com.havrylyuk.weather.data.model.ForecastWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 * Created by Igor Havrylyuk on 14.02.2017.
 */

public interface OpenWeatherService {

    @GET("forecast.json")
    Call<ForecastWeather> getWeather(
            @Query("key") String key,
            @Query("q") String LatLng,
            @Query("days") String days);

}
