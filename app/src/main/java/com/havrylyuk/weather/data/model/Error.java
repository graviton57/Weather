package com.havrylyuk.weather.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Igor Havrylyuk on 14.02.2017.
 */

public class Error {

    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;

    public Error() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
