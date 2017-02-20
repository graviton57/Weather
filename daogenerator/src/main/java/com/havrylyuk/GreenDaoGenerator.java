package com.havrylyuk;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;


public class GreenDaoGenerator {

    public static final int SCHEMA_VERSION = 1;
    public static final String DEFAULT_PACKAGE = "com.havrylyuk.weather.dao";

    public static void main(String[] args) {
        Schema schema = new Schema(SCHEMA_VERSION, DEFAULT_PACKAGE);
        createCityTable(schema);
        createWeatherTable(schema);
        try {
            new DaoGenerator().generateAll(schema, "app/src/main/java/");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void createCityTable(Schema schema){
        Entity note= schema.addEntity("OrmCity");
        note.addLongProperty("_id").primaryKey();
        note.addStringProperty("city_name");
        note.addStringProperty("region");
        note.addStringProperty("country");
        note.addDoubleProperty("lat");
        note.addDoubleProperty("lon");
    }

    private static void createWeatherTable(Schema schema){
        Entity note= schema.addEntity("OrmWeather");
        note.addIdProperty();
        note.addLongProperty("city_id");
        note.addStringProperty("city_name");
        note.addDateProperty("dt");
        note.addDoubleProperty("temp");
        note.addDoubleProperty("temp_min");
        note.addDoubleProperty("temp_max");
        note.addDoubleProperty("pressure");
        note.addIntProperty("humidity");
        note.addIntProperty("clouds");
        note.addDoubleProperty("wind_speed");
        note.addStringProperty("wind_dir");
        note.addIntProperty("rain");
        note.addIntProperty("snow");
        note.addStringProperty("icon");
        note.addIntProperty("condition_code");
        note.addStringProperty("condition_text");
        note.addBooleanProperty("is_day");
    }

}
