package com.havrylyuk.weather.data.source;

import com.havrylyuk.weather.dao.OrmCity;
import com.havrylyuk.weather.dao.OrmWeather;

import java.util.Date;
import java.util.List;

/**
 *
 * Created by Igor Havrylyuk on 16.02.2017.
 */

public interface RepositoryDataSource {

    List<OrmWeather> getForecast(int cityId, boolean isNetworkAvailable);

    List<OrmWeather> getForecast(int cityId, Date date, boolean isNetworkAvailable);

    OrmWeather getSingleForecast(final int cityId, boolean isNetworkAvailable);

    void refreshAllForecast(List<OrmWeather> forecast);

    void refreshForecast(int cityId, List<OrmWeather> forecast);

    void deleteAllForecast();

    void deleteForecast(int cityId);

    void deleteCity(OrmCity city);

    void saveForecast(List<OrmWeather> forecast);

    List<OrmCity> getCityList();

    void saveCities(List<OrmCity> cities);

    void saveCity(OrmCity city);
}
