package com.havrylyuk.weather.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Igor Havrylyuk on 03.03.2017.
 */
/*{
      "adminCode1": "03",+
      "lng": "25.94034",+
      "geonameId": 710719,+
      "toponymName": "Chernivtsi",+
      "countryId": "690791",+
      "fcl": "P",+
      "population": 236250,+
      "countryCode": "UA",+
      "name": "Chernivci",+
      "fclName": "city, village,...",+
      "countryName": "Украина",+
      "fcodeName": "центр административного деления первого порядка",+
      "adminName1": "Черновицкая область",+
      "lat": "48.29149",+
      "fcode": "PPLA"+
    }*/
public class GeoCity {

    @SerializedName("adminCode1")
    private String adminCode1;
    @SerializedName("adminName1")
    private String adminName1;
    @SerializedName("lng")
    private String lng;
    @SerializedName("geonameId")
    private long geonameId;
    @SerializedName("countryCode")
    private String countryCode;
    @SerializedName("countryId")
    private String countryId;
    @SerializedName("countryName")
    private String countryName;
    @SerializedName("name")
    private String name;
    @SerializedName("population")
    private long population;
    @SerializedName("toponymName")
    private String toponymName;
    @SerializedName("lat")
    private String lat;
    @SerializedName("fcl")
    private String fcl;
    @SerializedName("fcode")
    private String fcode;
    @SerializedName("fclName")
    private String fclName;
    @SerializedName("fcodeName")
    private String fcodeName;

    public GeoCity() {
    }

    public String getAdminCode1() {
        return adminCode1;
    }

    public void setAdminCode1(String adminCode1) {
        this.adminCode1 = adminCode1;
    }

    public String getAdminName1() {
        return adminName1;
    }

    public void setAdminName1(String adminName1) {
        this.adminName1 = adminName1;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public long getGeoNameId() {
        return geonameId;
    }

    public void setGeoNameId(long geonameId) {
        this.geonameId = geonameId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
    }

    public String getToponymName() {
        return toponymName;
    }

    public void setToponymName(String toponymName) {
        this.toponymName = toponymName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getFcl() {
        return fcl;
    }

    public void setFcl(String fcl) {
        this.fcl = fcl;
    }

    public String getFcode() {
        return fcode;
    }

    public void setFcode(String fcode) {
        this.fcode = fcode;
    }

    public String getFclName() {
        return fclName;
    }

    public void setFclName(String fclName) {
        this.fclName = fclName;
    }

    public String getFcodeName() {
        return fcodeName;
    }

    public void setFcodeName(String fcodeName) {
        this.fcodeName = fcodeName;
    }
}
