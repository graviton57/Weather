package com.havrylyuk.weather.util;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 *
 * Created by Igor Havrylyuk on 11.02.2017.
 */

public class ImageHelper {

    public static void load(@NonNull String url, SimpleDraweeView draweeView) {
        draweeView.setImageURI(Uri.parse(url));
    }
}
