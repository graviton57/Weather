package com.havrylyuk.weather.activity;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;

import com.facebook.drawee.view.SimpleDraweeView;
import com.havrylyuk.weather.R;
import com.havrylyuk.weather.fragment.CityDetailFragment;

/**
 *
 * Created by Igor Havrylyuk on 17.02.2017.
 */
public class CityDetailActivity extends BaseActivity {

    public static final String TRANSITION_NAME = "com.havrylyuk.weather.transition_name";
    public static final String IMAGE_NAME = "com.havrylyuk.weather.image_name";

    @Override
    protected int getLayout() {
        return R.layout.activity_city_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        String cityName = getIntent().getStringExtra(CityDetailFragment.ARG_ITEM_NAME);
        if (cityName != null) {
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(cityName);
            } else if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(cityName);
            }
        }
        String imageName = getIntent().getStringExtra(IMAGE_NAME);
        SimpleDraweeView imageView = (SimpleDraweeView) findViewById(R.id.backdrop);
        if (imageView != null && !TextUtils.isEmpty(imageName)) {
            imageView.setImageURI(Uri.parse("asset:///"+ imageName + ".jpg"));
        }
        boolean waitAnimations = false;
        if (getResources().getConfiguration().orientation == OrientationHelper.VERTICAL &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String transitionName = getIntent().getStringExtra(TRANSITION_NAME);
            if (imageView != null && !TextUtils.isEmpty(transitionName)) {
                waitAnimations = true;
                initEnterAnimation();
                imageView.setTransitionName(transitionName);
            }
        }
        if (savedInstanceState == null) {
            long cityId = getIntent().getLongExtra(CityDetailFragment.ARG_ITEM_ID, 0);
            CityDetailFragment fragment = CityDetailFragment.getInstance(cityId, cityName);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.city_detail_container, fragment)
                    .commit();
            if (waitAnimations) {
                fragment.waitAnimations();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getResources().getConfiguration().orientation == OrientationHelper.VERTICAL &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportFinishAfterTransition();
            initExitAnimation();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @TargetApi(21)
    private void initEnterAnimation() {
        final Transition sharedElementEnterTransition = getWindow().getSharedElementEnterTransition();
        sharedElementEnterTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                CityDetailFragment fragment = (CityDetailFragment) getSupportFragmentManager().findFragmentById(R.id.city_detail_container);
                fragment.loadContent();
                sharedElementEnterTransition.removeListener(this);
            }
        });
    }

    @TargetApi(21)
    private void initExitAnimation() {
        final Transition elementExitTransition = getWindow().getSharedElementReturnTransition();
        elementExitTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                CityDetailFragment fragment = (CityDetailFragment) getSupportFragmentManager().findFragmentById(R.id.city_detail_container);
                fragment.clearContent();
                elementExitTransition.removeListener(this);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }
}
