package com.havrylyuk.weather.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.havrylyuk.weather.BuildConfig;
import com.havrylyuk.weather.util.PreferencesHelper;

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

    public static void scheduleJob(Context context) {
        int interval = Integer.parseInt(PreferencesHelper.getInstance().getSyncInterval(context));
        FirebaseJobDispatcher dispatcher =
                new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job job = dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(WeatherJobService.class)
                .setTag("UpdateWeatherJob")
                .setRecurring(true)
                //.setTrigger(Trigger.executionWindow(30, 60))
                .setTrigger(Trigger.executionWindow(interval, interval + 60)) // default once per hour
                .setReplaceCurrent(true)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        int result = dispatcher.schedule(job);
        if (result != FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
            Log.e(LOG_TAG,"Error schedule request :" + result);
        }
    }
}
