package com.havrylyuk.weather.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.havrylyuk.weather.BuildConfig;
import com.havrylyuk.weather.R;
import com.havrylyuk.weather.adapter.CitiesRecyclerViewAdapter;
import com.havrylyuk.weather.dao.OrmCity;
import com.havrylyuk.weather.data.model.CityWithWeather;
import com.havrylyuk.weather.dialog.AboutDialog;
import com.havrylyuk.weather.events.ChangeEvent;
import com.havrylyuk.weather.events.LoadingStatusEvent;
import com.havrylyuk.weather.fragment.CityDetailFragment;
import com.havrylyuk.weather.service.WeatherJobService;
import com.havrylyuk.weather.service.WeatherService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Igor Havrylyuk on 16.02.2017.
 */

public class CitiesActivity extends PermissionActivity {

    private static final String LOG_TAG = CitiesActivity.class.getSimpleName();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mTwoPane;
    private CitiesRecyclerViewAdapter mAdapter;
    private final static String FRAGMENT_TAG = "com.havrylyuk.weather.fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            WeatherJobService.scheduleJob(this);
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setupToolbar();
        setupFabButton();
        if (findViewById(R.id.city_detail_container) != null) {
            mTwoPane = true;
        }
        setupRecyclerView();
        setupSwipeRefreshLayout();
        loadDataFromDb();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_cities;
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(getTitle());
        }
    }

    private void setupFabButton(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CitiesActivity.this, AddCityActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void setupSwipeRefreshLayout(){
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateDataFromNetwork();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.city_list);
        int orientation = getResources().getConfiguration().orientation;
        int spanCount = !mTwoPane && Configuration.ORIENTATION_LANDSCAPE == orientation ? 2 : 1;
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        mAdapter = new CitiesRecyclerViewAdapter(this, new ArrayList<CityWithWeather>());
        mAdapter.setListener(mListener);
        recyclerView.setAdapter(mAdapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        int location = viewHolder.getAdapterPosition();
                        localDataSource.deleteForecast(mAdapter.getCity(location).get_id());
                        localDataSource.deleteCity(mAdapter.getCity(location));
                        mAdapter.removeCity(location);
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                        if (mAdapter.getCurrentPosition() == location && mTwoPane && fragment != null) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .remove(fragment)
                                    .commit();
                            mAdapter.setCurrentPosition(-1);
                        } else if (mAdapter.getCurrentPosition() > location) {
                            mAdapter.setCurrentPosition(mAdapter.getCurrentPosition() - 1);
                        }
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private CitiesRecyclerViewAdapter.CitiesRecyclerViewItemListener mListener =
            new CitiesRecyclerViewAdapter.CitiesRecyclerViewItemListener() {

                @Override
                public void onItemClick(CityWithWeather city, View view) {
                    if (mTwoPane) {
                        long cityId = city.getCity().get_id();
                        String cityName = city.getCity().getCity_name();
                        CityDetailFragment fragment = CityDetailFragment.getInstance(cityId, cityName);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.city_detail_container, fragment, FRAGMENT_TAG)
                                .commit();
                    } else {
                        Intent intent = new Intent(CitiesActivity.this, CityDetailActivity.class);
                        Bundle args = new Bundle();
                        args.putLong(CityDetailFragment.ARG_ITEM_ID, city.getCity().get_id());
                        args.putString(CityDetailFragment.ARG_ITEM_NAME, city.getCity().getCity_name());
                        args.putString(CityDetailActivity.IMAGE_NAME, city.getWeather().getIcon());

                        if (getResources().getConfiguration().orientation == OrientationHelper.VERTICAL &&
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            args.putString(CityDetailActivity.TRANSITION_NAME, view.getTransitionName());
                            intent.putExtras(args);
                            ActivityOptions transitionActivityOptions =
                                    ActivityOptions.makeSceneTransitionAnimation(CitiesActivity.this, view, view.getTransitionName());
                            ActivityCompat.startActivity(CitiesActivity.this, intent, transitionActivityOptions.toBundle());
                        } else {
                            intent.putExtras(args);
                            startActivity(intent);
                        }
                    }
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                AboutDialog exitDialog =
                        (AboutDialog) getFragmentManager().findFragmentByTag(AboutDialog.ABOUT_DIALOG_TAG);
                if (exitDialog == null) exitDialog = AboutDialog.newInstance();
                if (!exitDialog.isAdded()) {
                    exitDialog.show(getFragmentManager().beginTransaction(), AboutDialog.ABOUT_DIALOG_TAG);
                }
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadDataFromDb(){
        List<CityWithWeather> cityWithWeatherList = new ArrayList<>();
        for (OrmCity ormCity : localDataSource.getCityList()) {
            CityWithWeather cityWithWeather = new CityWithWeather();
            cityWithWeather.setCity(ormCity);
            cityWithWeather.setWeather(localDataSource.getSingleForecast(ormCity.get_id()));
            cityWithWeatherList.add(cityWithWeather);
        }
        if (mAdapter!=null) {
            mAdapter.setCities(cityWithWeatherList);
        }
    }

    private void updateDataFromNetwork() {
        if (BuildConfig.DEBUG) Log.d("CitiesActivity","updateDataFromNetwork()");
        if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(true);
         Intent intent = new Intent(this, WeatherService.class);
         startService(intent);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoadingStatusEvent event) {
        switch (event.getStatus()) {
            case WeatherService.SYNC_START:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(CitiesActivity.this,"Sync start",Toast.LENGTH_SHORT).show();
                }
                break;
            case WeatherService.SYNC_END:
                if (BuildConfig.DEBUG) {
                    Toast.makeText(CitiesActivity.this,"Sync complete",Toast.LENGTH_SHORT).show();
                }
                if (mSwipeRefreshLayout!=null) mSwipeRefreshLayout.setRefreshing(false);
                loadDataFromDb();
                break;
            case WeatherService.SYNC_NO_INTERNET:
                    Toast.makeText(CitiesActivity.this,getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
                break;
            case WeatherService.SYNC_ERROR:
                    Toast.makeText(CitiesActivity.this, getString(R.string.error_load_data), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Subscribe
    public void onEvent(ChangeEvent event) {
        switch (event.getEventId()) {
            case ChangeEvent.CHANGE_CONTENT:
                if (BuildConfig.DEBUG)  Log.d(LOG_TAG, "Content Change Event - reload weather data from network");
                updateDataFromNetwork();
                break;
            case ChangeEvent.CHANGE_LANGUAGE:
                recreate();
                break;
        }
    }

}
