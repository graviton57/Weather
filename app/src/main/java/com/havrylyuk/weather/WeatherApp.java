package com.havrylyuk.weather;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.havrylyuk.weather.data.local.ILocalDataSource;
import com.havrylyuk.weather.data.local.LocalDataSource;

/**
 *
 * Created by Igor Havrylyuk on 11.02.2017.
 */

public class WeatherApp extends Application {

    public static SharedPreferences sSharedPreferences;
    private ILocalDataSource localDataSource;

    @Override
    public void onCreate() {
        super.onCreate();
        localDataSource = LocalDataSource.getInstance(this);
        Fresco.initialize(this);
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    public ILocalDataSource getLocalDataSource() {
        return localDataSource;
    }
}
