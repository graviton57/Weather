package com.havrylyuk.weather.util;

import android.content.SharedPreferences;

import com.havrylyuk.weather.WeatherApp;

import org.greenrobot.greendao.annotation.NotNull;

/**
 * Preferences Helper
 * Created by Igor Havrylyuk on 11.02.2017.
 */

public class PreferencesHelper {

    private static PreferencesHelper sInstance = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //singleton
    public static PreferencesHelper getInstance() {
        if(sInstance == null) {
            sInstance = new PreferencesHelper();
        }
        return sInstance;
    }

    public PreferencesHelper() {
        this.sharedPreferences = WeatherApp.getSharedPreferences();
        this.editor = this.sharedPreferences.edit();
    }

    //for save data in SharedPreferences
    public void setUnits(String name, @NotNull String units){
        editor.putString(name, units);
        editor.apply();
    }

    //for the loading of data from SharedPreferences
    public String getUnits(String prefName){
        return sharedPreferences.getString(prefName, "metric");
    }


    public void setForecastDays(String name, int days){
        editor.putInt(name, days);
        editor.apply();
    }


    public int getForecastDays(String prefName){
        return sharedPreferences.getInt(prefName, 7);
    }

    public void setUseLocation(String name, boolean useLocation){
        editor.putBoolean(name, useLocation);
        editor.apply();
    }

    public boolean getUseLocation(String prefName){
        return sharedPreferences.getBoolean(prefName, true);
    }


}
