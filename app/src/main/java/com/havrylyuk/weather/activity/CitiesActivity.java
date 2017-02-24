package com.havrylyuk.weather.activity;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.havrylyuk.weather.BuildConfig;
import com.havrylyuk.weather.R;
import com.havrylyuk.weather.adapter.CitiesRecyclerViewAdapter;
import com.havrylyuk.weather.dao.OrmCity;
import com.havrylyuk.weather.data.local.ILocalDataSource;
import com.havrylyuk.weather.data.local.LocalDataSource;
import com.havrylyuk.weather.data.model.CityWithWeather;

import com.havrylyuk.weather.dialog.AboutDialog;
import com.havrylyuk.weather.fragment.CityDetailFragment;
import com.havrylyuk.weather.service.SyncService;
import com.havrylyuk.weather.util.Utility;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Igor Havrylyuk on 16.02.2017.
 */

public class CitiesActivity extends AppCompatActivity {


    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mTwoPane;
    private CitiesRecyclerViewAdapter mAdapter;
    private final static String FRAGMENT_TAG = "fragment_tag";
    private SyncContentReceiver syncContentReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);
        IntentFilter filter = new IntentFilter(SyncContentReceiver.SYNC_RESPONSE_STATUS);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        syncContentReceiver = new SyncContentReceiver();
        registerReceiver(syncContentReceiver, filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(getTitle());
        }

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

        if (findViewById(R.id.city_detail_container) != null) {
            mTwoPane = true;
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.city_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               updateData();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        updateData();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        int spanCount;
        int orientation = getResources().getConfiguration().orientation;
        if (!mTwoPane && Configuration.ORIENTATION_LANDSCAPE == orientation) {
            spanCount = 2;
        } else {
            spanCount = 1;
        }
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
                AboutDialog exitDialog = (AboutDialog) getFragmentManager().findFragmentByTag(AboutDialog.ABOUT_DIALOG_TAG);
                if (exitDialog == null) exitDialog = AboutDialog.newInstance();
                if (!exitDialog.isAdded()) exitDialog.show(getFragmentManager().beginTransaction(), AboutDialog.ABOUT_DIALOG_TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateData() {
        if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(true);
           Intent intent = new Intent(this, SyncService.class);
           startService(intent);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(syncContentReceiver);
        super.onDestroy();
    }

    public class SyncContentReceiver extends BroadcastReceiver {
        public static final String SYNC_RESPONSE_STATUS = "com.havrylyuk.weather.intent.action.SYNC_RESPONSE_STATUS";
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean sync = intent.getIntExtra(SyncService.EXTRA_KEY_SYNC, 0) == 1;
            if (!sync) {
                if (BuildConfig.DEBUG) Toast.makeText(CitiesActivity.this,"Sync complete",Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                ILocalDataSource localDataSource = LocalDataSource.getInstance(context);
                List<CityWithWeather> cityWithWeatherList = new ArrayList<>();
                for (OrmCity ormCity : localDataSource.getCityList()) {
                    CityWithWeather cityWithWeather = new CityWithWeather();
                    cityWithWeather.setCity(ormCity);
                    cityWithWeather.setWeather(localDataSource.getSingleForecast(ormCity.get_id()));
                    cityWithWeatherList.add(cityWithWeather);
                }
                mAdapter.setCities(cityWithWeatherList);

            }
        }
    }

}
