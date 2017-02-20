package com.havrylyuk.weather.data.model;

import com.google.gson.annotations.SerializedName;

public class Current {

	@SerializedName("last_updated_epoch")
	private int last_updated_epoch;
	
	@SerializedName("wind_degree")
	private int wind_degree;
	
	@SerializedName("humidity")
	private int humidity;
	
	@SerializedName("cloud")
	private int cloud;
	
	@SerializedName("last_updated")
	private String last_updated;
	
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

    @SerializedName("is_day")
    private int is_day;

    @SerializedName("condition")
	private Condition condition;

    public Current() {
    }

    public int getIs_day() {
        return is_day;
    }

    public void setIs_day(int is_day) {
        this.is_day = is_day;
    }

    public int getLastUpdateEpoch()
     {
     	return last_updated_epoch;
     }
     public void setLastUpdateEpoch(int mLastUpdateEpoch)
     {
     	this.last_updated_epoch = mLastUpdateEpoch;
     }
     
     public String getLastUpdated()
     {
     	return last_updated;
     }
     public void setLastUpdated(String mLastUpdated)
     {
     	this.last_updated = mLastUpdated;
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
     
}
