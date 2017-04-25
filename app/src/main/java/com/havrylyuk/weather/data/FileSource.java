package com.havrylyuk.weather.data;

/**
 * Created by Igor Havrylyuk on 12.03.2017.
 */

public interface FileSource {

    String getCondition(int code, String lang);

}
