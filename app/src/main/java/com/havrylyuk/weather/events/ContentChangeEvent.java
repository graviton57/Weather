package com.havrylyuk.weather.events;

/**
 * Created by Igor Havrylyuk on 07.03.2017.
 */

public class ContentChangeEvent {

    private final long contentId;

    public ContentChangeEvent(long contentId) {
        this.contentId = contentId;
    }
}
