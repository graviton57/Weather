package com.havrylyuk.weather.data;

import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 *
 * Created by Igor Havrylyuk on 12.03.2017.
 */

public class FileManager implements FileSource {

    private static final String LOG_TAG = FileManager.class.getSimpleName();
    private static FileManager INSTANCE;
    private JSONArray conditions;
    private final static String FILE_NAME = "conditions.json";

    private FileManager(AssetManager assetManager) {
        this.conditions = getConditionFromFile(assetManager);
    }

    public static FileManager getInstance(AssetManager assetManager) {
        if (INSTANCE == null) {
            INSTANCE = new FileManager(assetManager);
        }
        return INSTANCE;
    }

    @Override
    public String getCondition(int code, String lang) {
        String result = null;
        for (int i = 0; conditions != null && i < conditions.length(); i++) {
            try {
                JSONObject jCondition = conditions.getJSONObject(i);
                if (jCondition.getInt("code") == code) {
                    JSONArray jLanguages = jCondition.getJSONArray("languages");
                    for (int j = 0; jLanguages != null && j < jLanguages.length(); j++) {
                        JSONObject jLanguage = jLanguages.getJSONObject(j);
                        if (lang.equals(jLanguage.getString("lang_iso"))) {
                            result = jLanguage.getString("day_text");
                            break;
                        }
                    }
                    break;
                }
            } catch(JSONException e) {
                Log.e(LOG_TAG, "Unable to get condition from json, " + e);
            }
        }
        return result;
    }

    private JSONArray getConditionFromFile(AssetManager assetManager) {
        try {
            InputStream inputStream = assetManager.open(FILE_NAME);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }
            return new JSONArray(total.toString());
        } catch(IOException e) {
            Log.e(LOG_TAG, "Unable to read conditions json file, " + e);
        } catch(JSONException e) {
            Log.e(LOG_TAG, "Unable to parse conditions json, " + e);
        }
            return null;
    }


}
