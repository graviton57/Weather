package com.havrylyuk.weather.data.model;


import com.google.gson.annotations.SerializedName;

public class ForecastWeather {
	
	@SerializedName("location")
	private Location location;
	
	@SerializedName("current")
	private Current current;
	
	@SerializedName("forecast")
	private Forecast forecast;

    @SerializedName("error")
    private Error error;

    public ForecastWeather() {
    }

    public Location getLocation()
     {
    	 return location;
     }
     public void setLocation(Location mLocation)
     {
    	 this.location = mLocation;
     }
     
     public Current getCurrent()
     {
    	 return current;
     }
     public void setCurrent(Current mCurrent)
     {
    	 this.current = mCurrent;
     }
     
     public Forecast getForecast()
     {
    	 return forecast;
     }
     public void setForecast(Forecast mForecast)
     {
    	 this.forecast = mForecast;
     }

    public Error getError() {
        return error;
    }

}
