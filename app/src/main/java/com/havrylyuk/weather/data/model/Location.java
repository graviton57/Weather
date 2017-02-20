package com.havrylyuk.weather.data.model;


import com.google.gson.annotations.SerializedName;

public class Location  {

    @SerializedName("id")
    private Long id;
	@SerializedName("name")
	private String name;
	@SerializedName("region")
	private String region;
	@SerializedName("country")
	private String country;
	@SerializedName("tz_id")
	private String tz_id;
	@SerializedName("localtime")
	private String localtime;
	@SerializedName("lat")
	private double lat;
	@SerializedName("lon")
	private double lon;
	@SerializedName("localtime_epoch")
	private int localtime_epoch;

    public Location() {
    }

    public String getName()
    {
    	return name;
    }
    public void setName(String mName)
    {
    	this.name = mName;
    }

    public Long getId() {
        return id;
    }

    public String getRegion()
    {
    	return region;
    }
    public void setRegion(String mRegion)
    {
    	this.region = mRegion;
    }
    
    public String getCountry()
    {
    	return country;
    }
    public void setCountry(String mCountry)
    {
    	this.country = mCountry;
    }
    
    public double getLat()
    {
    	return lat;
    }
    public void setLat(double mLat)
    {
    	this.lat = mLat;
    }
    
    public double getLong()
    {
    	return lon;
    }
    public void setLong(double mLong)
    {
    	this.lon = mLong;
    }
    
    public String getTzId()
    {
    	return tz_id;
    }
    public void setTzId(String mTz_id)
    {
    	this.tz_id = mTz_id;
    }
    
    public int getLocaltimeEpoch()
    {
    	return localtime_epoch;
    }
    public void setLocaltimeEpoch(int mLocaltimeEpoch)
    {
    	this.localtime_epoch = mLocaltimeEpoch;
    }
    
    public String getLocaltime()
    {
    	return localtime;
    }
    public void setLocaltimeEpoch(String mLocaltime)
    {
    	this.localtime = mLocaltime;
    }
    
}
