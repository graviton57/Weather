package com.havrylyuk.weather.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 *
 * Created by Igor Havrylyuk on 11.02.2017.
 */

public class ImageUtils  {
    private Context mContext;

    public ImageUtils(Context context) {
        mContext = context;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight &&
                    (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromAssets(String assetName, int reqWidth, int reqHeight) {
        try {
            InputStream ims = mContext.getAssets().open(assetName);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(ims, null, options);
            int scale = calculateInSampleSize(options, reqWidth, reqHeight);
            options = new BitmapFactory.Options();
            options.inSampleSize = scale;
            ims = mContext.getAssets().open(assetName);
            return BitmapFactory.decodeStream(ims, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
