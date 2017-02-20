package com.havrylyuk.weather.data.model;


import com.google.gson.annotations.SerializedName;

public class Astro   {

	@SerializedName("sunrise")
	private String sunrise;
	
	@SerializedName("sunset")
	private String sunset;
	
	@SerializedName("moonrise")
	private String moonrise;
	
	@SerializedName("moonset")
	private String moonset;

    public Astro() {
    }

    public String getSunrise()
    {
    	return sunrise;
    }
    public void setSunrise(String mSunrise)
    {
    	this.sunrise = mSunrise;
    }
    
    public String getSunset()
    {
    	return sunset;
    }
    public void setSunset(String mSunset)
    {
    	this.sunset = mSunset;
    }
    
    public String getMoonrise()
    {
    	return moonrise;
    }
    public void setMoonrise(String mMoonrise)
    {
    	this.moonrise = mMoonrise;
    }
    
    public String getMoonset()
    {
    	return moonset;
    }
    public void setMoonset(String mMoonset)
    {
    	this.moonset = mMoonset;
    }
}
