package com.havrylyuk.weather.data.model;

import android.support.annotation.Nullable;

/**
 * Created by Igor Havrylyuk on 03.03.2017.
 */

public class GeoApiResponse {
    @Nullable
    private Status status;

    public GeoApiResponse() {
    }

    @Nullable
    public Status getStatus() {
        return status;
    }

    // inner api status class

    // body: {
    // "status":{
    // "message":"ERROR: canceling statement due to statement timeout",
    // "value":13}
    // }
    /* value       message
    * 10 Authorization Exception
    * 11 record does not exist
    * 12 other error
    * 13 database timeout
    * 14 invalid parameter
    * 15 no result found
    * 16 duplicate exception
    * 17 postal code not found
    * 18 daily limit of credits exceeded
    * 19 hourly limit of credits exceeded
    * 20 weekly limit of credits exceeded
    * 21 invalid input
    * 22 server overloaded exception
    * 23 service not implemented
    * 25 parameter is too big  */

    public static class  Status {

        private String message;
        private int value;

        public Status() {
        }

        public String getMessage() {
            return message;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return  message + ' ' + ", ErrorCode:" + value ;
        }
    }
}
