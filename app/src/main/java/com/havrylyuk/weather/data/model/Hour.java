package com.havrylyuk.weather.data.model;


import com.google.gson.annotations.SerializedName;

public class Hour {
	
	@SerializedName("time_epoch")
	private int time_epoch;
	
	@SerializedName("wind_degree")
	private int wind_degree;
	
	@SerializedName("humidity")
	private int humidity;
	
	@SerializedName("cloud")
	private int cloud;
	
	@SerializedName("will_it_rain")
	private int will_it_rain;
	
	@SerializedName("will_it_snow")
	private int will_it_snow;
	
	@SerializedName("time")
	private String time;
	
	@SerializedName("wind_dir")
	private String wind_dir;
	
	@SerializedName("temp_c")
	private double temp_c;
	
	@SerializedName("temp_f")
	private double temp_f;
	
	@SerializedName("wind_mph")
	private double wind_mph;
	
	@SerializedName("wind_kph")
	private double wind_kph;
	
	@SerializedName("pressure_mb")
	private double pressure_mb;
	
	@SerializedName("pressure_in")
	private double pressure_in;
	
	@SerializedName("precip_mm")
	private double precip_mm;
	
	@SerializedName("precip_in")
	private double precip_in;
	
	@SerializedName("feelslike_c")
	private double feelslike_c;
	
	@SerializedName("feelslike_f")
	private double feelslike_f;
	
	@SerializedName("windchill_c")
	private double windchill_c;
	
	@SerializedName("windchill_f")
	private double windchill_f;
	
	@SerializedName("heatindex_c")
	private double heatindex_c;
	
	@SerializedName("heatindex_f")
	private double heatindex_f;
	
	@SerializedName("dewpoint_c")
	private double dewpoint_c;
	
	@SerializedName("dewpoint_f")
	private double dewpoint_f;
	
	@SerializedName("condition")
	private Condition condition = new Condition();

    @SerializedName("is_day")
    private int is_day;

    public Hour() {
    }

    public int getIs_day() {
        return is_day;
    }

    public void setIs_day(int is_day) {
        this.is_day = is_day;
    }

    public int getTimeEpoch()
    {
    	return time_epoch;
    }
    public void setTimeEpoch(int mTimeEpoch)
    {
    	this.time_epoch = mTimeEpoch;
    }
    
    public String getTime()
    {
    	return time;
    }
    public void setTime(String mTime)
    {
    	this.time = mTime;
    }
    
    public double getTempC()
    {
    	return temp_c;
    }
    public void setTempC(Double mTempC)
    {
    	this.temp_c = mTempC;
    }
    
    public double getTempF()
    {
    	return temp_f;
    }
    public void setTempF(Double mTempF)
    {
    	this.temp_f = mTempF;
    }
    
    public Condition getCondition()
    {
    	return condition;
    }
    public void setCondition(Condition mCondition)
    {
    	this.condition = mCondition;
    }
    
    public double getWindMph()
    {
    	return wind_mph;
    }
    public void setWindMph(double mWindMph)
    {
    	this.wind_mph = mWindMph;
    }
    
    public double getWindKph()
    {
    	return wind_kph;
    }
    public void setWindKph(double mWindKph)
    {
    	this.wind_kph = mWindKph;
    }
    
    public int getWindDegree()
    {
    	return wind_degree;
    }
    public void setWindDegree(int mWindDegree)
    {
    	this.wind_degree = mWindDegree;
    }
    
    public String getWindDir()
    {
    	return wind_dir;
    }
    public void setWindDir(String mWindDir)
    {
    	this.wind_dir = mWindDir;
    }
    
    public double getPressureMb()
    {
    	return pressure_mb;
    }
    public void setPressureMb(double mPressureMb)
    {
    	this.pressure_mb = mPressureMb;
    }
    
    public double getPressureIn()
    {
    	return pressure_in;
    }
    public void setPressureIn(double mPressureIn)
    {
    	this.pressure_in = mPressureIn;
    }
    
    public double getPrecipMm()
    {
    	return precip_mm;
    }
    public void setPrecipMm(double mPrecipMm)
    {
    	this.precip_mm = mPrecipMm;
    }
    
    public double getPrecipIn()
    {
    	return precip_in;
    }
    public void setPrecipIn(double mPrecipIn)
    {
    	this.precip_in = mPrecipIn;
    }
    
    public int getHumidity()
    {
    	return humidity;
    }
    public void setHumidity(int mHumidity)
    {
    	this.humidity = mHumidity;
    }
    
    public int getCloud()
    {
    	return cloud;
    }
    public void setCloud(int mCloud)
    {
    	this.cloud = mCloud;
    }
    
    public double getFeelslikeC()
    {
    	return feelslike_c;
    }
    public void setFeelslikeC(double mFeelslikeC)
    {
    	this.feelslike_c = mFeelslikeC;
    }
    
    public double getFeelslikeF()
    {
    	return feelslike_f;
    }
    public void setFeelslikeF(double mFeelslikeF)
    {
    	this.feelslike_f = mFeelslikeF;
    }
    
    public double getWindchillC()
    {
    	return windchill_c;
    }
    public void setWindchillC(double mWindchillC)
    {
    	this.windchill_c = mWindchillC;
    }
    
    public double getWindchillF()
    {
    	return windchill_f;
    }
    public void setWindchillF(double mWindchillF)
    {
    	this.windchill_f = mWindchillF;
    }
    
    public double getHeatindexC()
    {
    	return heatindex_c;
    }
    public void setHeatindexC(double mHeatindexC)
    {
    	this.heatindex_c = mHeatindexC;
    }
    
    public double getHeatindexF()
    {
    	return heatindex_f;
    }
    public void setHeatindexF(double mHeatIndexF)
    {
    	this.heatindex_f = mHeatIndexF;
    }
    
    public double getDewpointC()
    {
    	return dewpoint_c;
    }
    public void setDewpointC(double mDewpointC)
    {
    	this.dewpoint_c = mDewpointC;
    }
    
    public double getDewpointF()
    {
    	return dewpoint_f;
    }
    public void setDewpointF(double mDewpointF)
    {
    	this.dewpoint_f = mDewpointF;
    }
    
    public int getWillItRain()
    {
    	return will_it_rain;
    }
    public void setWillItRain(int mWillItRain)
    {
    	this.will_it_rain = mWillItRain;
    }
    
    public int getWillItSnow()
    {
    	return will_it_snow;
    }
    public void setWillItSnow(int mWillItSnow)
    {
    	this.will_it_snow = mWillItSnow;
    }
    
}
