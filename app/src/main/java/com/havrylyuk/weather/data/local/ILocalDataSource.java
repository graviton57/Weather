package com.havrylyuk.weather.data.local;


import com.havrylyuk.weather.dao.OrmCity;
import com.havrylyuk.weather.dao.OrmWeather;

import java.util.Date;
import java.util.List;


/**
 *
 * Created by Igor Havrylyuk on 14.02.2017.
 */
public interface ILocalDataSource {

    List<OrmWeather> getForecast(int cityId);

    List<OrmWeather> getForecast(int cityId, Date date);

    OrmWeather getSingleForecast(final long cityId);

    void refreshAllForecast(List<OrmWeather> forecast);

    void refreshForecast(int cityId, List<OrmWeather> forecast);

    void deleteAllForecast();

    void deleteForecast(long cityId);

    void deleteCity(OrmCity city);

    void saveForecast(List<OrmWeather> forecast);

    List<OrmCity> getCityList();

    void saveCities(List<OrmCity> cities);

    void saveCity(OrmCity city);
}
