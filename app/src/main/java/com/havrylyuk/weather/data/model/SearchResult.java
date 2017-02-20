package com.havrylyuk.weather.data.model;

import com.google.gson.annotations.SerializedName;
    /*{
        "id": 2494280,
        "name": "Chernivtsi, Chernivets'ka Oblast', Ukraine",
        "region": "Chernivets'ka Oblast'",
        "country": "Ukraine",
        "lat": 48.3,
        "lon": 25.93,
        "url": "chernivtsi-chernivetska-oblast-ukraine"
        }*/

/**
 *
 * Created by Igor Havrylyuk on 17.02.2017.
 */

public class SearchResult {

    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("region")
    private String region;
    @SerializedName("country")
    private String country;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lon")
    private double lon;
    @SerializedName("url")
    private String url;


    public SearchResult() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
