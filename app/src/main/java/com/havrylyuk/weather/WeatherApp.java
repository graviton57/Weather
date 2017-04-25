package com.havrylyuk.weather;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.havrylyuk.weather.data.local.ILocalDataSource;
import com.havrylyuk.weather.data.local.LocalDataSource;
import com.havrylyuk.weather.util.LocaleHelper;

/**
 *
 * Created by Igor Havrylyuk on 11.02.2017.
 */

public class WeatherApp extends Application {

    public static SharedPreferences sSharedPreferences;
    private ILocalDataSource localDataSource;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        localDataSource = LocalDataSource.getInstance(this);
        Fresco.initialize(this);
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    public ILocalDataSource getLocalDataSource() {
        return localDataSource;
    }
}
