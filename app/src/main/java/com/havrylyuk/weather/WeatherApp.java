package com.havrylyuk.weather;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.havrylyuk.weather.data.local.ILocalDataSource;
import com.havrylyuk.weather.data.local.LocalDataSource;
import com.squareup.picasso.Picasso;

/**
 *
 * Created by Igor Havrylyuk on 11.02.2017.
 */

public class WeatherApp extends Application {


    public static SharedPreferences sSharedPreferences;
    //private DaoSession daoSession;
    private ILocalDataSource localDataSource;


    @Override
    public void onCreate() {
        super.onCreate();
        //greenDAO
        localDataSource = LocalDataSource.getInstance(this);
       /* PopulateDaoMaster.OpenHelper helper = new PopulateDaoMaster.OpenHelper(this, DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        PopulateDaoMaster daoMaster = new PopulateDaoMaster(db);//populate city data
        daoSession = daoMaster.newSession();*/
        //Picasso
        Picasso picasso = new Picasso.Builder(this).build();
        Picasso.setSingletonInstance(picasso);

        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

   /* public DaoSession getDaoSession() {
        return daoSession;
    }*/

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    public ILocalDataSource getLocalDataSource() {
        return localDataSource;
    }
}
