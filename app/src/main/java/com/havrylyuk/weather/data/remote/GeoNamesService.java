package com.havrylyuk.weather.data.remote;

import com.havrylyuk.weather.data.model.GeoCities;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 * Created by Igor Havrylyuk on 14.02.2017.
 */

public interface GeoNamesService {

    //http://api.geonames.org/searchJSON?
    // name_startsWith=Chern
    // &maxRows=10
    // &lang=ru
    // &username=graviton57
    // &cities='cities15000'

    @GET("searchJSON")
    Call<GeoCities> findCity(
            @Query("name_startsWith") String cityName,
            @Query("maxRows") int maxRows,
            @Query("lang") String lang,
            @Query("cities") String cities,
            @Query("username") String userName);
}
