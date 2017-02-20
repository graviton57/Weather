package com.havrylyuk.weather.util;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 *
 * Created by Igor Havrylyuk on 11.02.2017.
 */

public class ImageHelper {

    public static void load(@NonNull String url, ImageView imageView) {
        Picasso.with(imageView.getContext())
                .load(url)
                //.placeholder(R.drawable.weather)
                .noFade()
                .into(imageView);
    }
}
