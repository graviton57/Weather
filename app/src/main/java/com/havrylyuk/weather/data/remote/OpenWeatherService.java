package com.havrylyuk.weather.data.remote;

import com.havrylyuk.weather.data.model.ForecastWeather;
import com.havrylyuk.weather.data.model.Location;
import com.havrylyuk.weather.data.model.SearchResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 * Created by Igor Havrylyuk on 14.02.2017.
 */

public interface OpenWeatherService {

    @GET("forecast.json")
    Call<ForecastWeather> getWeather(@Query("key") String key, @Query("q") String city, @Query("days") String days);

    @GET("search.json")
    Call<List<SearchResult>> findCity(@Query("key") String key, @Query("q") String city);
}
