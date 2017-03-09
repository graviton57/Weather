package com.havrylyuk.weather.service;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
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
import com.havrylyuk.weather.data.remote.OpenWeatherService;
import com.havrylyuk.weather.data.remote.WeatherApiClient;
import com.havrylyuk.weather.util.PreferencesHelper;
import com.havrylyuk.weather.util.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.havrylyuk.weather.service.WeatherService.ACTION_DATA_UPDATED;
import static com.havrylyuk.weather.service.WeatherService.EXTRA_KEY_SYNC;
import static com.havrylyuk.weather.service.WeatherService.FORECAST_COUNT_DAYS;

public class WeatherJobService extends JobService {

    private static final String LOG_TAG = WeatherJobService.class.getSimpleName();
    private ILocalDataSource localDataSource;
    private SimpleDateFormat fmt;
    private boolean isMetric;

    private static final int START_SYNC = 1;
    private static final int END_SYNC = 2;
    private static final int ERROR_SYNC = 0;

    public WeatherJobService() {

    }

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d(LOG_TAG, "onStartJob job=" + job.toString());
        localDataSource = ((WeatherApp) getApplicationContext()).getLocalDataSource();
        fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        PreferencesHelper pref = PreferencesHelper.getInstance();
        isMetric = getString(R.string.pref_unit_default_value)
                .equals(pref.getUnits(getString(R.string.pref_unit_key)));
        loadDataFromNetwork(job);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(LOG_TAG, "onStopJob job=" + job.toString());
        return false;
    }

    private void loadDataFromNetwork(JobParameters parameters) {
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, " Beginning network data synchronization ");
        updateSyncStatus(START_SYNC);
        if (Utility.isNetworkAvailable(getApplicationContext())) {
            final OpenWeatherService service = WeatherApiClient.getClient().create(OpenWeatherService.class);
            List<OrmCity> cities = localDataSource.getCityList();
            if (cities != null && !cities.isEmpty()) {
                localDataSource.deleteAllForecast();//delete old forecast data
                for (OrmCity city : cities) {
                    getWeatherForCity(service, city);
                }

            } else  if (BuildConfig.DEBUG) Log.d(LOG_TAG, "empty cities table");

        } else    {
            Log.d(LOG_TAG,getString(R.string.no_internet));
        }
        updateSyncStatus(END_SYNC);
        //Tell the framework that the job has completed and doesnot needs to be reschedule
        jobFinished(parameters, false);
    }

    private void getWeatherForCity(OpenWeatherService service, final OrmCity city) {
        String latLng = String.valueOf(city.getLat()) + " , " + String.valueOf(city.getLon());
        Call<ForecastWeather> responseCall =
                service.getWeather(BuildConfig.WEATHER_API_KEY, latLng, String.valueOf(FORECAST_COUNT_DAYS));
        responseCall.enqueue(new Callback<ForecastWeather>() {
            @Override
            public void onResponse(Call<ForecastWeather> call, Response<ForecastWeather> response) {
                if (response.body().getError() == null) {
                    List<OrmWeather> ormWeatherList = new ArrayList<>();
                    try {
                        ormWeatherList.add(getCurrentOrmWeather(city.get_id(), response.body()));
                        getHourlyOrmWeather(city.get_id(), response.body(), ormWeatherList);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    localDataSource.saveForecast(ormWeatherList);
                } else {
                    Log.e(LOG_TAG, response.body().getError().getMessage());
                    Toast.makeText(getApplicationContext(), response.body().getError().getCode()+
                            ":"+response.body().getError().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForecastWeather> call, Throwable t) {
                Log.e(LOG_TAG, t.toString());
                Toast.makeText(getApplicationContext(), t.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private OrmWeather getCurrentOrmWeather(long cityId, ForecastWeather response ) throws ParseException {
        Current current = response.getCurrent();
        OrmWeather weather = new OrmWeather();
        weather.setCity_id(cityId);
        weather.setCity_name(response.getLocation().getName());
        weather.setDt(fmt.parse(current.getLastUpdated()));
        weather.setClouds(current.getCloud());
        weather.setHumidity(current.getHumidity());
        weather.setPressure(isMetric ? convertMbToMmHg(current.getPressureMb()) : current.getPressureIn());
        weather.setTemp(isMetric?current.getTempC():current.getTempF());
        weather.setIs_day(current.getIs_day()==1);
        weather.setIcon(current.getCondition().getIcon());
        weather.setCondition_text(current.getCondition().getText());
        weather.setCondition_code(current.getCondition().getCode());
        weather.setWind_speed(isMetric ? convertKphToMps(current.getWindKph()) : current.getWindMph());
        weather.setWind_dir(response.getCurrent().getWindDir());
        return weather;
    }

    private void getHourlyOrmWeather(long cityId, ForecastWeather response, List<OrmWeather> ormWeatherList) throws ParseException {
        for (ForecastDay forecastDay : response.getForecast().getForecastday()) {
            for (Hour hour : forecastDay.getHours()) {
                if (fmt.parse(hour.getTime()).after(Calendar.getInstance().getTime())) {//no save old forecast
                    OrmWeather weather = new OrmWeather();
                    weather.setCity_id(cityId);
                    weather.setCity_name(response.getLocation().getName());
                    weather.setDt(fmt.parse(hour.getTime()));
                    weather.setClouds(hour.getCloud());
                    weather.setHumidity(hour.getHumidity());
                    weather.setPressure(isMetric ? convertMbToMmHg(hour.getPressureMb()) : hour.getPressureIn());
                    weather.setTemp(isMetric ? hour.getTempC() : hour.getTempF());
                    weather.setTemp_min(isMetric ?forecastDay.getDay().getMintempC():forecastDay.getDay().getMintempF());
                    weather.setTemp_max(isMetric ?forecastDay.getDay().getMaxtempC():forecastDay.getDay().getMaxtempF());
                    weather.setIcon(hour.getCondition().getIcon());
                    weather.setWind_speed(isMetric ? convertKphToMps(hour.getWindKph()) : hour.getWindMph());
                    weather.setWind_dir(hour.getWindDir());
                    weather.setRain(hour.getWillItRain());
                    weather.setSnow(hour.getWillItSnow());
                    weather.setCondition_text(hour.getCondition().getText());
                    weather.setCondition_code(hour.getCondition().getCode());
                    weather.setIs_day(hour.getIs_day()==1);
                    ormWeatherList.add(weather);
                }
            }
        }
    }

    private void updateSyncStatus(int status) {
        Intent intentUpdate = new Intent();
        intentUpdate.setAction(ACTION_DATA_UPDATED);
        intentUpdate.setPackage(getPackageName());
        intentUpdate.putExtra(EXTRA_KEY_SYNC, status);
        sendBroadcast(intentUpdate);
    }

    //convert MilliBars to mmHg.
    private Double convertMbToMmHg(Double pressureInMb){
        return pressureInMb * 0.750062;
    }

    // convert Km per hour to m/s
    private Double convertKphToMps(Double windKph){
        return windKph * 0.277778;
    }
}
