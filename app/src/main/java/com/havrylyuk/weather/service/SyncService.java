package com.havrylyuk.weather.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.havrylyuk.weather.BuildConfig;
import com.havrylyuk.weather.R;
import com.havrylyuk.weather.WeatherApp;
import com.havrylyuk.weather.activity.CitiesActivity;
import com.havrylyuk.weather.dao.OrmCity;
import com.havrylyuk.weather.dao.OrmWeather;
import com.havrylyuk.weather.data.local.ILocalDataSource;
import com.havrylyuk.weather.data.model.Current;
import com.havrylyuk.weather.data.model.ForecastDay;
import com.havrylyuk.weather.data.model.ForecastWeather;
import com.havrylyuk.weather.data.model.Hour;
import com.havrylyuk.weather.data.remote.ApiClient;
import com.havrylyuk.weather.data.remote.OpenWeatherService;
import com.havrylyuk.weather.util.Utility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

/**
 *
 * Created by Igor Havrylyuk on 14.02.2017.
 */

public class SyncService extends IntentService {

    private static final String LOG_TAG = SyncService.class.getSimpleName();
    public static final String EXTRA_KEY_SYNC ="com.havrylyuk.weather.intent.action.EXTRA_KEY_SYNC" ;
    private static final int START_SYNC = 1;
    private static final int END_SYNC = 2;
    private static final int ERROR_SYNC = 0;

    private ILocalDataSource localDataSource;
    private OpenWeatherService service;

    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null ) {
            if (BuildConfig.DEBUG) Log.d(LOG_TAG, " Beginning network data synchronization ");
            sendSyncStatus(START_SYNC);
            if (Utility.isNetworkAvailable(getApplicationContext())) {
                localDataSource = ((WeatherApp) getApplicationContext()).getLocalDataSource();
                service = ApiClient.getClient().create(OpenWeatherService.class);
                List<OrmCity> cities = localDataSource.getCityList();
                if (cities != null && !cities.isEmpty()) {
                    localDataSource.deleteAllForecast();//delete old forecast data
                    for (OrmCity city : cities) {
                        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getWeatherForCity city= "+city.getCity_name());
                        getWeatherForCity(city);
                    }
                } else  if (BuildConfig.DEBUG) Log.d(LOG_TAG, "empty cities table");
            } else    {
                Log.d(LOG_TAG,getString(R.string.no_internet));
                //Toast.makeText(this, getString(R.string.no_internet),Toast.LENGTH_LONG).show();
            }
            if (BuildConfig.DEBUG) Log.d(LOG_TAG, " End network data synchronization ");
            sendSyncStatus(END_SYNC);
        }
    }

    private void getWeatherForCity(OrmCity city) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Call<ForecastWeather> responseCall = service.getWeather(ApiClient.API_KEY, city.getCity_name(), "2");
        try {
            ForecastWeather response = responseCall.execute().body();
            if (response.getError() == null) {
                List<OrmWeather> ormWeatherList = new ArrayList<>();
                Current current = response.getCurrent();
                OrmWeather weather = new OrmWeather();
                weather.setCity_id(city.get_id());
                weather.setCity_name(response.getLocation().getName());
                weather.setDt(fmt.parse(current.getLastUpdated()));
                weather.setClouds(current.getCloud());
                weather.setHumidity(current.getHumidity());
                weather.setPressure(current.getPressureIn());
                weather.setTemp(current.getTempC());
                weather.setIs_day(current.getIs_day()==1);
                weather.setIcon(current.getCondition().getIcon());
                weather.setCondition_text(current.getCondition().getText());
                weather.setCondition_code(current.getCondition().getCode());
                weather.setWind_speed(current.getWindKph());
                Log.d(LOG_TAG,"wind_dir="+response.getCurrent().getWindDir());
                weather.setWind_dir(response.getCurrent().getWindDir());
                ormWeatherList.add(weather);

                for (ForecastDay forecastDay : response.getForecast().getForecastday()) {
                    for (Hour hour : forecastDay.getHours()) {
                        if (fmt.parse(hour.getTime()).after(Calendar.getInstance().getTime())) {//no save old forecast
                            weather = new OrmWeather();
                            weather.setCity_id(city.get_id());
                            weather.setCity_name(response.getLocation().getName());
                            weather.setDt(fmt.parse(hour.getTime()));
                            weather.setClouds(hour.getCloud());
                            weather.setHumidity(hour.getHumidity());
                            weather.setPressure(hour.getPressureMb());
                            weather.setTemp(hour.getTempC());
                            weather.setTemp_min(forecastDay.getDay().getMintempC());
                            weather.setTemp_max(forecastDay.getDay().getMaxtempC());
                            weather.setIcon(hour.getCondition().getIcon());
                            weather.setWind_speed(hour.getWindKph());
                            weather.setWind_dir(hour.getWindDir());
                            Log.d(LOG_TAG,"wind_dir="+hour.getWindDir());
                            weather.setRain(hour.getWillItRain());
                            weather.setSnow(hour.getWillItSnow());
                            weather.setCondition_text(hour.getCondition().getText());
                            weather.setCondition_code(hour.getCondition().getCode());
                            weather.setIs_day(hour.getIs_day()==1);
                            ormWeatherList.add(weather);
                        }
                    }
                }
                localDataSource.saveForecast(ormWeatherList);
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG,"location cityName="+response.getLocation().getName()
                            +"Current t=" + response.getCurrent().getFeelslikeC()
                            + "Current date=" + response.getCurrent().getLastUpdated()
                            +"Current icon=" + response.getCurrent().getCondition().getIcon());
                }
            } else {
                Log.e(LOG_TAG, response.getError().getMessage());
                Toast.makeText(getApplicationContext(), response.getError().getCode()+
                        ":"+response.getError().getMessage(),Toast.LENGTH_SHORT).show();
             }
            } catch (IOException e) {
                e.printStackTrace();
                sendSyncStatus(ERROR_SYNC);
            } catch (ParseException e) {
                e.printStackTrace();
        }

    }

    // send sync status
    private void sendSyncStatus(int status) {
        Intent intentUpdate = new Intent();
        intentUpdate.setAction(CitiesActivity.SyncContentReceiver.SYNC_RESPONSE_STATUS);
        intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
        intentUpdate.putExtra(EXTRA_KEY_SYNC, status);
        sendBroadcast(intentUpdate);
    }


}
