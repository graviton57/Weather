package com.havrylyuk.weather.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Forecast {
	
	@SerializedName("forecastday")
	private ArrayList<ForecastDay> forecastday = new ArrayList<>();

    public Forecast() {
    }

    public ArrayList<ForecastDay> getForecastday()
    {
    	return forecastday;
    }
    public void setForecastday(ArrayList<ForecastDay> mForecastday)
    {
    	this.forecastday = mForecastday;
    }
	
	
}
