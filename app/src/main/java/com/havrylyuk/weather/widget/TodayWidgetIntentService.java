package com.havrylyuk.weather.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.havrylyuk.weather.R;
import com.havrylyuk.weather.activity.CitiesActivity;
import com.havrylyuk.weather.dao.OrmWeather;
import com.havrylyuk.weather.data.local.ILocalDataSource;
import com.havrylyuk.weather.data.local.LocalDataSource;
import com.havrylyuk.weather.util.Utility;

/**
 * Created by Igor Havrylyuk on 04.03.2017.
 */

public class TodayWidgetIntentService extends IntentService {


    public TodayWidgetIntentService() {
        super("TodayWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                TodayWidgetProvider.class));

        // Extract the weather data from the LocalDataSource
        ILocalDataSource localDataSource = LocalDataSource.getInstance(this);
        OrmWeather ormWeather = localDataSource.getSingleForecast(1);

        String description = "Clear";//ormWeather.getCondition_text();
        String cityName = "Chernivtsi";
        String date = "Нд, 05 Бер, 12:34";
        //String weatherIcon = "http:" + ormWeather.getIcon();
        final String weatherIcon ="http://cdn.apixu.com/weather/64x64/day/113.png";
        double temp = 15;//ormWeather.getTemp();
        String formattedTemperature = Utility.formatTemperature(this, temp);

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            final RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_today_large);
            // Add the data to the RemoteViews
            Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadWeatherIcon(views, weatherIcon);
                }
            });
            //views.setImageViewResource(R.id.widget_icon, R.drawable.ic_clear);
            setRemoteContentDescription(views, description);
            views.setTextViewText(R.id.widget_description, description);
            views.setTextViewText(R.id.widget_city_name, cityName);
            //views.setViewVisibility(R.id.widget_button_update, View.GONE);
            views.setTextViewText(R.id.widget_weather_date, date);
            views.setTextViewText(R.id.widget_high_temperature, formattedTemperature);
            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, CitiesActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            // Create an Intent to Update weather
            Intent updateIntent = new Intent(this.getApplicationContext(),
                                             TodayWidgetProvider.class);
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingUpdateIntent =
                    PendingIntent.getBroadcast(getApplicationContext(), 0, updateIntent,
                                                  PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_button_update, pendingUpdateIntent);
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void loadWeatherIcon(final RemoteViews views, String imageUri) {
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(imageUri))
                .setRotationOptions(RotationOptions.autoRotate())
                .setRequestPriority(Priority.HIGH)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        final DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {

            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                if (dataSource.isFinished() && bitmap != null){
                    Log.d(TodayWidgetIntentService.class.getSimpleName(),"load bitmap success!");
                    Bitmap bmp = Bitmap.createBitmap(bitmap);
                    views.setImageViewBitmap(R.id.widget_icon, bmp);
                    dataSource.close();
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                Log.e(TodayWidgetIntentService.class.getSimpleName(), "onFailureImpl.");
                if (dataSource != null) {
                    dataSource.close();
                }
            }
        }, CallerThreadExecutor.getInstance());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }
}
