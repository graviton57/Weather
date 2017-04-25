package com.havrylyuk.weather.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ForecastDay {
	

	@SerializedName("date")
	private String date;
	
	@SerializedName("date_epoch")
	private int date_epoch;
	
	@SerializedName("day")
	private Day day = new Day();
	
	@SerializedName("astro")
	private Astro astro = new Astro();
	
	@SerializedName("hour")
	private ArrayList<Hour> hour = new ArrayList<>();

    public ForecastDay() {
    }

    public String getDate()
    {
    	return date;
    }

    public void setDate(String mDate)
    {
    	this.date = mDate;
    }
    
    public int getDateEpoch()
    {
    	return date_epoch;
    }
    public void setDateEpoch(int mDateEpoch)
    {
    	this.date_epoch = mDateEpoch;
    }
    
    public Day getDay()
    {
    	return day;
    }
    public void setDay(Day mDay)
    {
    	this.day = mDay;
    }
    
    public Astro getAstro()
    {
    	return astro;
    }
    public void setAstro(Astro mAstro)
    {
    	this.astro = mAstro;
    }
    
    public ArrayList<Hour> getHours()
    {
    	return hour;
    }
    public void setHours(ArrayList<Hour> mHour)
    {
    	this.hour = mHour;
    }
}
