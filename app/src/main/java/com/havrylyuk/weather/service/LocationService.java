package com.havrylyuk.weather.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.havrylyuk.weather.events.LocationEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LocationService extends IntentService {

    public static final String LOCATION_DATA_EXTRA = "com.havrylyuk.earthquakes.LOCATION_DATA_EXTRA";

    public LocationService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Location location = intent.getParcelableExtra(LOCATION_DATA_EXTRA);
            LocationEvent locationEvent = parseLocation(location);
            EventBus.getDefault().postSticky(locationEvent);
        }
    }

    public LocationEvent parseLocation(Location location) {
        String country = "";
        String region= "";
        String city= "";
        if (location == null) {
            return new LocationEvent(country, region, city);
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses  = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                if (addresses.get(0).getLocality() != null) {
                    city = addresses.get(0).getLocality();
                    country = addresses.get(0).getCountryName();
                    region = addresses.get(0).getAdminArea();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new LocationEvent(country, region, city);
    }
}
