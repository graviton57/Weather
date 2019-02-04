package com.havrylyuk.weather.data.model;


import com.google.gson.annotations.SerializedName;

public class Day {

	@SerializedName("maxtemp_c")
	private double maxtemp_c;

	@SerializedName("maxtemp_f")
	private double maxtemp_f;

	@SerializedName("mintemp_c")
	private double mintemp_c;

	@SerializedName("mintemp_f")
	private double mintemp_f;

	@SerializedName("avgtemp_c")
	private double avgtemp_c;
	
	@SerializedName("avgtemp_f")
	private double avgtemp_f;
	
	@SerializedName("maxwind_mph")
	private double maxwind_mph;
	
	@SerializedName("maxwind_kph")
	private double maxwind_kph;
	
	@SerializedName("totalprecip_mm")
	private double totalprecip_mm;
	
	@SerializedName("totalprecip_in")
	private double totalprecip_in;

	@SerializedName("avghumidity")
    private int humidity;

    @SerializedName("condition")
    private Condition mCondition;

    public Day() {
    }

    public double getMaxtempC()
    {
    	return maxtemp_c;
    }
    public void setMaxtempC(double mMaxtemp_c)
    {
    	this.maxtemp_c = mMaxtemp_c;
    }
    
    public double getMaxtempF()
    {
    	return maxtemp_f;
    }
    public void setMaxtempF(double mMaxtemp_f)
    {
    	this.maxtemp_f = mMaxtemp_f;
    }
    
    public double getMintempC()
    {
    	return mintemp_c;
    }
    public void setMintempC(double mMintemp_c)
    {
    	this.mintemp_c = mMintemp_c;
    }
    
    public double getMintempF()
    {
    	return mintemp_f;
    }
    public void setMintempF(double mMintemp_f)
    {
    	this.mintemp_f = mMintemp_f;
    }
    
    public double getAvgtempC()
    {
    	return avgtemp_c;
    }
    public void setAvgtempC(double mAvgtemp_c)
    {
    	this.avgtemp_c = mAvgtemp_c;
    }
    
    public double getAvgtempF()
    {
    	return avgtemp_f;
    }
    public void setAvgtempF(double mAvgtemp_f)
    {
    	this.avgtemp_f = mAvgtemp_f;
    }
    
    public double getMaxwindMph()
    {
    	return maxwind_mph;
    }
    public void setMaxwindMph(double mMaxwind_mph)
    {
    	this.maxwind_mph = mMaxwind_mph;
    }
    
    public double getMaxwindKph()
    {
    	return maxwind_kph;
    }
    public void setMaxwindKph(double mMaxwind_kph)
    {
    	this.maxwind_kph = mMaxwind_kph;
    }
    
    public double getTotalprecipMm()
    {
    	return totalprecip_mm;
    }
    public void setTotalprecipMm(double mTotalprecip_mm)
    {
    	this.totalprecip_mm = mTotalprecip_mm;
    }
    
    public double getTotalprecipIn()
    {
    	return totalprecip_in;
    }
    public void setTotalprecipIn(double mTotalprecip_in)
    {
    	this.totalprecip_in = mTotalprecip_in;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(final int humidity) {
        this.humidity = humidity;
    }

    public Condition getCondition()
    {
    	return mCondition;
    }
    public void setCondition(Condition mCondition)
    {
    	this.mCondition = mCondition;
    }
    
}
