package com.havrylyuk.weather.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.havrylyuk.weather.BuildConfig;
import com.havrylyuk.weather.service.WeatherService;

/**
 * Created by Igor Havrylyuk on 04.03.2017.
 */
public class TodayWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = TodayWidgetProvider.class.getSimpleName();


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, TodayWidgetIntentService.class));
        if (BuildConfig.DEBUG) Log.d(LOG_TAG,"onUpdate()");
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, TodayWidgetIntentService.class));
        if (BuildConfig.DEBUG) Log.d(LOG_TAG,"onAppWidgetOptionsChanged()");
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (WeatherService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            Intent updateIntent = new Intent(context, TodayWidgetIntentService.class);
            boolean sync = intent.getIntExtra(WeatherService.EXTRA_KEY_SYNC, 0) == 1;
            if (BuildConfig.DEBUG) Log.d(LOG_TAG,"onReceive ACTION_DATA_UPDATED sync="+sync);
            updateIntent.putExtra(WeatherService.EXTRA_KEY_SYNC, intent.getIntExtra(WeatherService.EXTRA_KEY_SYNC, 0));
            context.startService(updateIntent);
        }
        if (TodayWidgetIntentService.ACTION_APPWIDGET_REFRESH.equals(intent.getAction())){
            context.startService(new Intent(context, WeatherService.class));
            if (BuildConfig.DEBUG) Log.d(LOG_TAG,"onReceive ACTION_APPWIDGET_REFRESH start WeatherService");
        }
    }

}

