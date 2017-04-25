# Weather

<p align="center">
  <img src="screenshot/logo.png" >
</p>

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Weather-brightgreen.svg?style=flat)](https://android-arsenal.com/details/3/5631)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![API](https://img.shields.io/badge/API-16%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=16)


Weather Android App using apixu API https://www.apixu.com

Icon weather set made by apixu from https://www.apixu.com/api.aspx

Searching cities is supported by more than 200 languages.

List of cities and flags from http://www.geonames.org .
 

Main Activity|Detail Activity | Detail list
-------------|----------------- | -------------
![alt text](screenshot/main.png "Main Activity")  | ![alt text](screenshot/detail.png "Detail Fragment") | ![alt text](screenshot/list.png "Hourly list")


Search City|Settings Activity | Widget
-------------|----------------- | -------------
![alt text](screenshot/add_city.png "Add new city")  | ![alt text](screenshot/settings.png "Settings Fragment") | ![alt text](screenshot/widget.png "Widget")



#### Used libraries:
* com.android.support:appcompat-v7:25.1.0
* com.android.support:support-v4:25.1.0
* com.android.support:recyclerview-v7:25.1.0
* com.android.support:design:25.1.0
* com.android.support:cardview-v7:25.1.1
* org.greenrobot:greendao:3.2.0
* com.squareup.retrofit2:retrofit:2.1.0
* com.squareup.retrofit2:converter-gson:2.1.0
* com.squareup.okhttp3:logging-interceptor:3.3.1
* com.google.android.gms:play-services-location:10.2.0
* com.firebase:firebase-jobdispatcher:0.5.2
* com.facebook.fresco:fresco:1.1.0

#### Prerequisites

Create an api.gradle file in Weather directory after generating API Key from Apixu and UserName from GeoNames. The contents would somewhat look like this :

    ext {
    BASE_WEATHER_URL = "http://api.apixu.com/v1/"; 
    BASE_GEONAME_URL= "http://api.geonames.org/"; 
    GEONAME_API_KEY= "YOUR_USER_NAME"; 
    WEATHER_API_KEY = "YOUR_API_KEY"; }

Developed By
-------
Igor Havrylyuk (Graviton57)

[1]: https://github.com/graviton57/weather.git
