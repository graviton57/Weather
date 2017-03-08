package com.havrylyuk.weather.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.havrylyuk.weather.BuildConfig;
import com.havrylyuk.weather.R;
import com.havrylyuk.weather.WeatherApp;
import com.havrylyuk.weather.dao.OrmCity;
import com.havrylyuk.weather.data.local.ILocalDataSource;
import com.havrylyuk.weather.util.PreferencesHelper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Igor Havrylyuk on 07.03.2017.
 */

public abstract class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSIONS_REQUEST_LOCATION = 101;
    private static final String LOG_TAG = BaseActivity.class.getSimpleName();

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    ILocalDataSource localDataSource;


    protected abstract int getLayout();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localDataSource = ((WeatherApp) getApplicationContext()).getLocalDataSource();
        setContentView(getLayout());
        if (PreferencesHelper.getInstance().isUseCurrentLocation(this)) {
            askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSIONS_REQUEST_LOCATION);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(BaseActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(BaseActivity.this, permission)) {
                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permission}, requestCode);
            }
         } else {
            if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
                buildGoogleApiClient();
            }
            if (BuildConfig.DEBUG){
                Log.d(LOG_TAG, "" + permission + " is already granted.");
            }
         }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSIONS_REQUEST_LOCATION:
                    buildGoogleApiClient();
                    break;
            }
            if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Permission granted");
        } else {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                updateCurrentLocation(lastLocation);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
      if (BuildConfig.DEBUG) Log.d(LOG_TAG, connectionResult.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (BuildConfig.DEBUG) Log.d(LOG_TAG,"onConnectionSuspended i="+i);
    }

    private void updateCurrentLocation(Location location) {
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                String country = addresses.get(0).getCountryName();
                String region = addresses.get(0).getAdminArea();
                String currentCity = addresses.get(0).getLocality();
                String currentAddress = addresses.get(0).getAddressLine(0);
                if (BuildConfig.DEBUG){
                Log.d(LOG_TAG, "You current country=" + country+ " region="+region +
                        " cityName=" + currentCity+" Address=" + currentAddress);
                Log.d(LOG_TAG,"lat="+String.valueOf(location.getLatitude())+"long="+String.valueOf(lastLocation.getLongitude()));
                }
                OrmCity yourCity = new OrmCity((long)1, currentCity, region,
                        country, location.getLatitude(), location.getLongitude());
               localDataSource.saveCity(yourCity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
