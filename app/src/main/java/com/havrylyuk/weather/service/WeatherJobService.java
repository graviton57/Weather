package com.havrylyuk.weather.service;

import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.havrylyuk.weather.BuildConfig;

public class WeatherJobService extends JobService {

    private static final String LOG_TAG = WeatherJobService.class.getSimpleName();

    public WeatherJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters job) {
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onStartJob " + job.getTag());
        Intent intent = new Intent(this, WeatherService.class);
        startService(intent);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onStopJob " + job.getTag());
        return false;
    }

}
