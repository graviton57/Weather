package com.havrylyuk.weather.data.model;


import com.google.gson.annotations.SerializedName;

public class Condition  {

	@SerializedName("text")
	private String text;
	
	@SerializedName("icon")
	private String icon;
	
	@SerializedName("code")
	private int code;

    public Condition() {
    }

    public String getText()
    {
    	return text;
    }
    public void setText(String mText)
    {
    	this.text = mText;
    }
    
    public String getIcon()
    {
    	return icon;
    }
    public void setIcon(String mIcon)
    {
    	this.icon = mIcon;
    }
    
    public int getCode()
    {
    	return code;
    }
    public void setCode(int mCode)
    {
    	this.code = mCode;
    }
    
}
