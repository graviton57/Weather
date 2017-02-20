package com.havrylyuk.weather.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.havrylyuk.weather.dao.DaoMaster;


/**
 * This class insert fruits data in database
 * Created by Igor Havrylyuk on 02.02.2017.
 */

public class PopulateDaoMaster extends DaoMaster {

    public PopulateDaoMaster(SQLiteDatabase db) {
        super(db);
    }

    public static class OpenHelper extends DaoMaster.OpenHelper {

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            super.onCreate(db);
            for (String insert : SQL_INSERTS_CITIES_DATA) {
                db.execSQL(insert);
            }
        }

        final String[] SQL_INSERTS_CITIES_DATA = {
                "INSERT INTO ORM_CITY ( city_name, region, country, lat, lon ) " +
                        "VALUES ( 'Chernivtsi', 'Chernivetska Oblast', 'Ukraine', 48.3, 25.93 );",

        };


    }



}
