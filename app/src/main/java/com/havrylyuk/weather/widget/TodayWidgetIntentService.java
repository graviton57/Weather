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
import com.havrylyuk.weather.WeatherApp;
import com.havrylyuk.weather.activity.CitiesActivity;
import com.havrylyuk.weather.dao.OrmCity;
import com.havrylyuk.weather.dao.OrmWeather;
import com.havrylyuk.weather.data.local.ILocalDataSource;
import com.havrylyuk.weather.service.WeatherService;
import com.havrylyuk.weather.util.PreferencesHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *
 * Created by Igor Havrylyuk on 04.03.2017.
 */

public class TodayWidgetIntentService extends IntentService {

    public static final String ACTION_APPWIDGET_REFRESH = "om.havrylyuk.weather.ACTION_APPWIDGET_REFRESH";

    private static final String LOG_TAG = TodayWidgetIntentService.class.getSimpleName();

    public TodayWidgetIntentService() {
        super("TodayWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SimpleDateFormat format = new SimpleDateFormat("EE, dd MMM, HH:mm", Locale.getDefault());
        PreferencesHelper pref = PreferencesHelper.getInstance();
        boolean isMetric = getString(R.string.pref_unit_default_value)
                .equals(pref.getUnits(this));
        if (intent != null  ) {
            boolean sync = intent.getIntExtra(WeatherService.EXTRA_KEY_SYNC, 0) == 1;
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                    TodayWidgetProvider.class));
            ILocalDataSource localDataSource = ((WeatherApp) getApplicationContext()).getLocalDataSource();
            if (localDataSource.getCityList() != null && !localDataSource.getCityList().isEmpty()) {
            OrmCity ormCity = localDataSource.getCityList().get(0);
            String cityName = null;
            String description = null;
            String date = null;
            String wind = null;
            String weatherIcon = null;
            String formatTemp = null;
            String formatTempMax = null;
            String formatTempMin = null;
            if (ormCity != null) {
                 cityName = ormCity.getCity_name();
                 OrmWeather ormWeather = localDataSource.getSingleForecast(ormCity.get_id());
                if (ormWeather != null) {
                     description = ormWeather.getCondition_text();
                     date = format.format(ormWeather.getDt());
                     wind = getString(R.string.format_wind, ormWeather.getWind_speed(),
                             isMetric?"m/s":"mph", ormWeather.getWind_dir());
                     weatherIcon = "http:" + ormWeather.getIcon();
                     formatTemp = getString(R.string.format_widget_temperature, ormWeather.getTemp(),isMetric?"°C":"°F");
                     formatTempMax = getString(R.string.format_widget_temperature, ormWeather.getTemp_max(),isMetric?"°C":"°F");
                     formatTempMin = getString(R.string.format_widget_temperature, ormWeather.getTemp_min(),isMetric?"°C":"°F");
                }
              }
            for (int appWidgetId : appWidgetIds) {
                int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);
                int defaultWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
                int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_large_width);
                int layoutId;
                if (widgetWidth >= largeWidth) {
                    layoutId = R.layout.widget_today_large;
                } else if (widgetWidth >= defaultWidth) {
                    layoutId = R.layout.widget_today;
                } else {
                    layoutId = R.layout.widget_today_small;
                }
                final RemoteViews views = new RemoteViews(getPackageName(), layoutId);
                    //final RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_today_large);
                    views.setViewVisibility(R.id.widget_button_update, sync ? View.GONE : View.VISIBLE);
                    views.setViewVisibility(R.id.widget_progress_bar, sync ? View.VISIBLE : View.GONE);
                    setRemoteContentDescription(views, description);
                    if (wind!=null) views.setTextViewText(R.id.widget_wind, wind);
                    if (description!=null) views.setTextViewText(R.id.widget_description, description);
                    if (cityName!=null) views.setTextViewText(R.id.widget_city_name, cityName);
                    if (date!=null) views.setTextViewText(R.id.widget_weather_date, date);
                    if (formatTemp!=null) views.setTextViewText(R.id.widget_high_temperature, formatTemp);
                    if (formatTempMax!=null) views.setTextViewText(R.id.widget_max_temperature, formatTempMin);
                    if (formatTempMin!=null) views.setTextViewText(R.id.widget_low_temperature, formatTempMin);
                    if (weatherIcon != null) loadWeatherIcon(appWidgetManager, appWidgetId, views, weatherIcon);
                    // Create an Intent to launch CitiesActivity
                    Intent launchIntent = new Intent(this, CitiesActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
                    views.setOnClickPendingIntent(R.id.widget, pendingIntent);
                    // Create an Intent to Update weather
                    Intent updateIntent = new Intent(this.getApplicationContext(), TodayWidgetProvider.class);
                    updateIntent.setAction(ACTION_APPWIDGET_REFRESH);
                    updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                    PendingIntent pendingUpdateIntent =
                            PendingIntent.getBroadcast(getApplicationContext(), 0, updateIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.widget_button_update, pendingUpdateIntent);
                    // Tell the AppWidgetManager to perform an update on the current app widget
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            }
       }
    }

    private void loadWeatherIcon(final AppWidgetManager appWidgetManager, final int appWidgetId,
                                 final RemoteViews views, String imageUri) {
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
                    Bitmap bmp = Bitmap.createBitmap(bitmap);
                    views.setImageViewBitmap(R.id.widget_icon, bmp);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                    dataSource.close();
                }
            }
            @Override
            public void onFailureImpl(DataSource dataSource) {
                Log.e(LOG_TAG, "Failure load bitmap");
                if (dataSource != null) {
                    dataSource.close();
                }
            }
        }, CallerThreadExecutor.getInstance());
    }

    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }

    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return  getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
    }
}
