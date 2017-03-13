package com.havrylyuk.weather.data.local;

import android.content.Context;

import com.havrylyuk.weather.BuildConfig;
import com.havrylyuk.weather.dao.DaoMaster;
import com.havrylyuk.weather.dao.DaoSession;
import com.havrylyuk.weather.dao.OrmCity;
import com.havrylyuk.weather.dao.OrmCityDao;
import com.havrylyuk.weather.dao.OrmWeather;
import com.havrylyuk.weather.dao.OrmWeatherDao;
import com.havrylyuk.weather.events.ChangeEvent;
import com.havrylyuk.weather.util.PreferencesHelper;
import com.havrylyuk.weather.util.Utility;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by Igor Havrylyuk on 14.02.2017.
 */
public class LocalDataSource implements ILocalDataSource {

    private static final String DB_NAME = "weather.db";
    private static ILocalDataSource INSTANCE;
    private DaoSession daoSession;
    private Context context;

    private LocalDataSource(Context context) {
        this.context = context;
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        daoSession = daoMaster.newSession();
    }

    public static ILocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public List<OrmWeather> getForecast(final int cityId) {
        return loadForecastFromDb(cityId);
    }

    private List<OrmWeather> loadForecastFromDb(int cityId) {
        OrmWeatherDao weatherDao = daoSession.getOrmWeatherDao();
        return weatherDao.queryBuilder()
                .where(OrmWeatherDao.Properties.City_id.eq(cityId))
                .build()
                .list();
    }

    @Override
    public OrmWeather getSingleForecast(long cityId) {
        return loadSingleForecastFromDb(cityId);
    }

    private OrmWeather loadSingleForecastFromDb(long cityId) {
        OrmWeatherDao weatherDao = daoSession.getOrmWeatherDao();
        List<OrmWeather> forecast = weatherDao.queryBuilder()
                .where(OrmWeatherDao.Properties.City_id.eq(cityId))
                .orderAsc(OrmWeatherDao.Properties.Dt)
                .limit(1)
                .build()
                .list();
        if (forecast.size() < 1) {
            return null;
        } else {
            return forecast.get(0);
        }
    }

    @Override
    public List<OrmWeather> getForecast(final int cityId, final Date date) {
        return loadForecastFromDb(cityId, date);
    }

    private List<OrmWeather> loadForecastFromDb(int cityId, Date date) {
        OrmWeatherDao weatherDao = daoSession.getOrmWeatherDao();
        List<OrmWeather> forecast = weatherDao.queryBuilder()
                .where(OrmWeatherDao.Properties.Dt.between(getStartOfDayInMillis(date),
                        getEndOfDayInMillis(date)),
                        OrmWeatherDao.Properties.City_id.eq(cityId))
                .build().list();
        Utility.sortWeatherHour(forecast);
        return forecast;
    }

    @Override
    public List<OrmCity> getCityList() {
        return loadCityListFromDb();
    }

    private List<OrmCity> loadCityListFromDb() {
        OrmCityDao cityDao = daoSession.getOrmCityDao();
        List<OrmCity> cities = cityDao.loadAll();
        if (cities.size() > 0) {
            return cities;
        } else {
            if (!PreferencesHelper.getInstance().isUseCurrentLocation(context) && BuildConfig.DEBUG) {
                cities.add(new OrmCity((long) 2, "Kiev", "Kievska Oblast", "Ukraine", 50.43, 30.52));
                cities.add(new OrmCity((long) 3, "London", "Greater London", "United Kingdom", 51.52, -0.11));
                cities.add(new OrmCity((long) 4, "Madrid", "Madrid", "Spain", 40.4, -3.68));
                saveCities(cities);
            }
            return cities;
        }
    }

    private Date getStartOfDayInMillis(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getEndOfDayInMillis(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getStartOfDayInMillis(date));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    @Override
    public void saveCities(List<OrmCity> cities) {
        OrmCityDao cityDao = daoSession.getOrmCityDao();
        cityDao.insertInTx(cities);
        if (cities!=null && !cities.isEmpty()){
            EventBus.getDefault().post(new ChangeEvent(ChangeEvent.CHANGE_CONTENT));
        }

    }

    @Override
    public void saveCity(OrmCity city) {
        OrmCityDao cityDao = daoSession.getOrmCityDao();
        cityDao.insertOrReplace(city);
        EventBus.getDefault().post(new ChangeEvent(ChangeEvent.CHANGE_CONTENT));
    }

    @Override
    public void refreshAllForecast(List<OrmWeather> forecast) {
        OrmWeatherDao weatherDao = daoSession.getOrmWeatherDao();
        weatherDao.deleteAll();
        weatherDao.insertInTx(forecast);
    }

    @Override
    public void refreshForecast(int cityId, List<OrmWeather> forecast) {
        OrmWeatherDao weatherDao = daoSession.getOrmWeatherDao();
        weatherDao.queryBuilder()
                .where(OrmWeatherDao.Properties.City_id.eq((long) cityId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
        weatherDao.insertInTx(forecast);
    }

    @Override
    public void deleteAllForecast() {
        OrmWeatherDao weatherDao = daoSession.getOrmWeatherDao();
        weatherDao.deleteAll();
    }

    @Override
    public void deleteForecast(long cityId) {
        OrmWeatherDao weatherDao = daoSession.getOrmWeatherDao();
        weatherDao.queryBuilder()
                .where(OrmWeatherDao.Properties.City_id.eq(cityId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    @Override
    public void saveForecast(List<OrmWeather> forecast) {
        OrmWeatherDao weatherDao = daoSession.getOrmWeatherDao();
        weatherDao.insertInTx(forecast);
    }

    @Override
    public void deleteCity(OrmCity city) {
        OrmCityDao cityDao = daoSession.getOrmCityDao();
        cityDao.delete(city);
    }


}
