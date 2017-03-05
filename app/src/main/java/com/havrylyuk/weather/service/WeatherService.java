package com.havrylyuk.weather.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.havrylyuk.weather.BuildConfig;
import com.havrylyuk.weather.R;
import com.havrylyuk.weather.WeatherApp;
import com.havrylyuk.weather.dao.OrmCity;
import com.havrylyuk.weather.dao.OrmWeather;
import com.havrylyuk.weather.data.local.ILocalDataSource;
import com.havrylyuk.weather.data.model.Current;
import com.havrylyuk.weather.data.model.ForecastDay;
import com.havrylyuk.weather.data.model.ForecastWeather;
import com.havrylyuk.weather.data.model.Hour;
import com.havrylyuk.weather.data.remote.WeatherApiClient;
import com.havrylyuk.weather.data.remote.OpenWeatherService;
import com.havrylyuk.weather.util.PreferencesHelper;
import com.havrylyuk.weather.util.Utility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

/**
 *
 * Created by Igor Havrylyuk on 14.02.2017.
 */

public class WeatherService extends IntentService {

    public static final int FORECAST_COUNT_DAYS = 7;
    public static final String EXTRA_KEY_SYNC =
            "com.havrylyuk.weather.intent.action.EXTRA_KEY_SYNC" ;
    public static final String ACTION_DATA_UPDATED =
            "com.havrylyuk.weather.app.ACTION_DATA_UPDATED";

    private static final String LOG_TAG = WeatherService.class.getSimpleName();
    private static final int START_SYNC = 1;
    private static final int END_SYNC = 2;
    private static final int ERROR_SYNC = 0;



    private ILocalDataSource localDataSource;
    private OpenWeatherService service;
    private final SimpleDateFormat fmt;
    private boolean isMetric;

    public WeatherService() {
        super("WeatherService");
        fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null ) {
            PreferencesHelper pref = PreferencesHelper.getInstance();
            isMetric = "metric".equals(pref.getUnits(getString(R.string.pref_unit_key)));
            if (BuildConfig.DEBUG) Log.d(LOG_TAG, " Beginning network data synchronization ");
            updateSyncStatus(START_SYNC);
            if (Utility.isNetworkAvailable(getApplicationContext())) {
                localDataSource = ((WeatherApp) getApplicationContext()).getLocalDataSource();
                service = WeatherApiClient.getClient().create(OpenWeatherService.class);
                List<OrmCity> cities = localDataSource.getCityList();
                if (cities != null && !cities.isEmpty()) {
                    localDataSource.deleteAllForecast();//delete old forecast data
                    for (OrmCity city : cities) {
                        getWeatherForCity(city);
                    }

                } else  if (BuildConfig.DEBUG) Log.d(LOG_TAG, "empty cities table");
            } else    {
                Log.d(LOG_TAG,getString(R.string.no_internet));
            }
            if (BuildConfig.DEBUG) Log.d(LOG_TAG, " End network data synchronization ");
            updateSyncStatus(END_SYNC);
        }
    }

    private void getWeatherForCity(OrmCity city) {
        String latLng = String.valueOf(city.getLat()) + " , " + String.valueOf(city.getLon());
        Call<ForecastWeather> responseCall =
                service.getWeather(BuildConfig.WEATHER_API_KEY, latLng, String.valueOf(FORECAST_COUNT_DAYS));
        try {
            ForecastWeather response = responseCall.execute().body();
            if (response.getError() == null) {
                List<OrmWeather> ormWeatherList = new ArrayList<>();
                ormWeatherList.add(getCurrentOrmWeather(city.get_id(),response));
                getHourlyOrmWeather(city.get_id(), response, ormWeatherList);
                localDataSource.saveForecast(ormWeatherList);
            } else {
                Log.e(LOG_TAG, response.getError().getMessage());
                Toast.makeText(getApplicationContext(), response.getError().getCode()+
                        ":"+response.getError().getMessage(),Toast.LENGTH_SHORT).show();
             }
            } catch (IOException e) {
                e.printStackTrace();
                updateSyncStatus(ERROR_SYNC);
            }
    }

    private OrmWeather getCurrentOrmWeather(long cityId, ForecastWeather response ) {
        Current current = response.getCurrent();
        OrmWeather weather = new OrmWeather();
        weather.setCity_id(cityId);
        weather.setCity_name(response.getLocation().getName());
        try {
            weather.setDt(fmt.parse(current.getLastUpdated()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        weather.setClouds(current.getCloud());
        weather.setHumidity(current.getHumidity());
        weather.setPressure(isMetric ? current.getPressureMb() * 0.750062 : current.getPressureIn());//convert to mmHg.
        weather.setTemp(isMetric?current.getTempC():current.getTempF());
        weather.setIs_day(current.getIs_day()==1);
        weather.setIcon(current.getCondition().getIcon());
        weather.setCondition_text(current.getCondition().getText());
        weather.setCondition_code(current.getCondition().getCode());
        weather.setWind_speed(isMetric ? current.getWindKph() * 0.277778 : current.getWindMph());//convert to m/s
        weather.setWind_dir(response.getCurrent().getWindDir());
        return weather;
    }

    private void getHourlyOrmWeather(long cityId, ForecastWeather response, List<OrmWeather> ormWeatherList) {
        for (ForecastDay forecastDay : response.getForecast().getForecastday()) {
            for (Hour hour : forecastDay.getHours()) {
                try {
                    if (fmt.parse(hour.getTime()).after(Calendar.getInstance().getTime())) {//no save old forecast
                        OrmWeather weather = new OrmWeather();
                        weather.setCity_id(cityId);
                        weather.setCity_name(response.getLocation().getName());
                        weather.setDt(fmt.parse(hour.getTime()));
                        weather.setClouds(hour.getCloud());
                        weather.setHumidity(hour.getHumidity());
                        weather.setPressure(isMetric ? hour.getPressureMb() * 0.750062 : hour.getPressureIn());
                        weather.setTemp(isMetric ? hour.getTempC() : hour.getTempF());
                        weather.setTemp_min(isMetric ?forecastDay.getDay().getMintempC():forecastDay.getDay().getMintempF());
                        weather.setTemp_max(isMetric ?forecastDay.getDay().getMaxtempC():forecastDay.getDay().getMaxtempF());
                        weather.setIcon(hour.getCondition().getIcon());
                        weather.setWind_speed(isMetric ? hour.getWindKph() * 0.277778 : hour.getWindMph());
                        weather.setWind_dir(hour.getWindDir());
                        weather.setRain(hour.getWillItRain());
                        weather.setSnow(hour.getWillItSnow());
                        weather.setCondition_text(hour.getCondition().getText());
                        weather.setCondition_code(hour.getCondition().getCode());
                        weather.setIs_day(hour.getIs_day()==1);
                        ormWeatherList.add(weather);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateSyncStatus(int status) {
        Intent intentUpdate = new Intent();
        intentUpdate.setAction(ACTION_DATA_UPDATED);
       // intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
        intentUpdate.setPackage(getPackageName());
        intentUpdate.putExtra(EXTRA_KEY_SYNC, status);
        sendBroadcast(intentUpdate);
    }

}
