package com.havrylyuk.weather.data.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

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
