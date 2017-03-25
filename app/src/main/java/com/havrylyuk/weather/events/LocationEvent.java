package com.havrylyuk.weather.events;

/**
 * Created by Igor Havrylyuk on 25.03.2017.
 */

public class LocationEvent {

    private String country;
    private String region;
    private String city;

    public LocationEvent(String country, String region, String city) {
        this.country = country;
        this.region = region;
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }
}
