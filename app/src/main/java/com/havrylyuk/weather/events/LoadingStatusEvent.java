package com.havrylyuk.weather.events;

/**
 * Created by Igor Havrylyuk on 13.03.2017.
 */

public class LoadingStatusEvent {

    private int status;

    public LoadingStatusEvent(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
