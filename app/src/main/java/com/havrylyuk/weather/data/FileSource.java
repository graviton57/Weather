package com.havrylyuk.weather.data;

import com.havrylyuk.weather.data.model.Condition;

/**
 * Created by Igor Havrylyuk on 12.03.2017.
 */

public interface FileSource {

    String getCondition(int code, String lang);

}
