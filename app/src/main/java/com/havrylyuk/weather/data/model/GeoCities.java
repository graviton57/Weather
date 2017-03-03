package com.havrylyuk.weather.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Igor Havrylyuk on 03.03.2017.
 */

public class GeoCities extends GeoApiResponse {

    private int totalResultsCount;

    @SerializedName("geonames")
    private List<GeoCity> cities;

    public List<GeoCity> getCities() {
        return cities;
    }

    public int getTotalResultsCount() {
        return totalResultsCount;
    }
}
