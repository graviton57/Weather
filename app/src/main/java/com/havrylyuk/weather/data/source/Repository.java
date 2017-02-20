package com.havrylyuk.weather.data.source;

import android.content.Intent;

import com.havrylyuk.weather.dao.OrmCity;
import com.havrylyuk.weather.dao.OrmWeather;
import com.havrylyuk.weather.data.local.ILocalDataSource;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by Igor Havrylyuk on 16.02.2017.
 */

public class Repository implements RepositoryDataSource {

    private static Repository INSTANCE;
    private ILocalDataSource mLocalDataSource;

    private Repository(ILocalDataSource localDataSource ) {
        mLocalDataSource = localDataSource;

    }

    public static Repository getInstance(ILocalDataSource localDataSource ) {
        if (INSTANCE == null) {
            INSTANCE = new Repository(localDataSource);
        }
        return INSTANCE;
    }


    @Override
    public List<OrmWeather> getForecast(int cityId, boolean isNetworkAvailable) {
        if (isNetworkAvailable) {
            Intent intent = new Intent();

        } else {
            return mLocalDataSource.getForecast(cityId);
        }
        return null;
    }

    @Override
    public List<OrmWeather> getForecast(int cityId, Date date, boolean isNetworkAvailable) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY) - 6);
        if (isNetworkAvailable) {

        } else {
            return mLocalDataSource.getForecast(cityId, date);
        }
        return null;
    }

    @Override
    public OrmWeather getSingleForecast(int cityId, boolean isNetworkAvailable) {
        return null;
    }

    @Override
    public void refreshAllForecast(List<OrmWeather> forecast) {
        mLocalDataSource.refreshAllForecast(forecast);
    }

    @Override
    public void refreshForecast(int cityId, List<OrmWeather> forecast) {
        mLocalDataSource.refreshForecast(cityId, forecast);
    }

    @Override
    public void deleteAllForecast() {
        mLocalDataSource.deleteAllForecast();
    }

    @Override
    public void deleteForecast(int cityId) {
        mLocalDataSource.deleteForecast(cityId);
    }

    @Override
    public void deleteCity(OrmCity city) {
        mLocalDataSource.deleteCity(city);
    }

    @Override
    public void saveForecast(List<OrmWeather> forecast) {
        mLocalDataSource.saveForecast(forecast);
    }

    @Override
    public List<OrmCity> getCityList() {
        return mLocalDataSource.getCityList();
    }

    @Override
    public void saveCities(List<OrmCity> cities) {
        mLocalDataSource.saveCities(cities);
    }

    @Override
    public void saveCity(OrmCity city) {
        mLocalDataSource.saveCity(city);
    }
}
