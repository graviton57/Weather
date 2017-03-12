package com.havrylyuk.weather.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.havrylyuk.weather.BuildConfig;
import com.havrylyuk.weather.R;
import com.havrylyuk.weather.WeatherApp;
import com.havrylyuk.weather.adapter.AddCityRecyclerViewAdapter;
import com.havrylyuk.weather.dao.OrmCity;
import com.havrylyuk.weather.data.local.ILocalDataSource;
import com.havrylyuk.weather.data.model.GeoCities;
import com.havrylyuk.weather.data.model.GeoCity;
import com.havrylyuk.weather.data.remote.GeoNameApiClient;
import com.havrylyuk.weather.data.remote.GeoNamesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * Created by Igor Havrylyuk on 16.02.2017.
 */
public class AddCityActivity extends BaseActivity {

    private static final String LOG_TAG = AddCityActivity.class.getSimpleName();
    private static final int MAX_CITIES_LIST_SIZE = 16;
    private static final String CITIES_STYLE = "cities1000";//"cities5000" or "cities15000"

    private AddCityRecyclerViewAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView mSearchState;
    private SimpleDraweeView mImageView;
    private View mCityListView;

    private SearchView searchView;
    private String searchQuery;

    private ILocalDataSource localDataSource;
    private GeoNamesService service;

    @Override
    protected int getLayout() {
        return R.layout.activity_add_city;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        localDataSource = ((WeatherApp) getApplicationContext()).getLocalDataSource();
        service = GeoNameApiClient.getClient().create(GeoNamesService.class);
        setupRecyclerView();
        mSearchState = (TextView) findViewById(R.id.textView);
        mImageView = (SimpleDraweeView) findViewById(R.id.imageView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        assert mProgressBar != null;
        mProgressBar.setVisibility(View.GONE);
        mCityListView = findViewById(R.id.city_search_list_container);
        if (mCityListView != null) {
            mCityListView.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.city_search_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        mAdapter = new AddCityRecyclerViewAdapter(new ArrayList<GeoCity>(),
                        new AddCityRecyclerViewAdapter.AddCityRecyclerViewItemListener() {
            @Override
            public void onItemClick(GeoCity newCity) {
                OrmCity city = new OrmCity(newCity.getGeoNameId(),newCity.getName(),newCity.getAdminName1(),
                        newCity.getCountryName(), Double.parseDouble(newCity.getLat()),
                        Double.parseDouble(newCity.getLng()));
                localDataSource.saveCity(city);
                Intent intent = new Intent(AddCityActivity.this, CitiesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            setupSearchView(searchItem);
        }
        return true;
    }

    private void setupSearchView(MenuItem searchItem) {
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchQuery = null;
                updateData();
                return true;
            }
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });
        SearchManager searchManager = (SearchManager) (getSystemService(Context.SEARCH_SERVICE));
        searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            searchView.clearFocus();
            searchView.setOnQueryTextListener(onQueryTextListener);
        }
    }

    private  SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchQuery = query;
            searchView.clearFocus();
            updateData();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            searchQuery = query;
            updateData();
            return true;
        }
    };

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        hideKeyboard();
        onBackPressed();
        return true;
    }

    private void updateData() {
        if (!TextUtils.isEmpty(searchQuery)) {
            setProgressBarVisible(true);
            Call<GeoCities> responseCall = service.findCity(
                    searchQuery, MAX_CITIES_LIST_SIZE, Locale.getDefault().getLanguage(),
                    CITIES_STYLE, BuildConfig.GEONAME_API_KEY);
            responseCall.enqueue(new Callback<GeoCities>() {
                @Override
                public void onResponse(Call<GeoCities> call, Response<GeoCities> response) {
                    if (!response.body().getCities().isEmpty()) {
                        addCities(response.body().getCities());
                        setCityListVisible(true);
                        setProgressBarVisible(false);
                    } else {
                        showCouldNotFindCity();
                    }
                }
                @Override
                public void onFailure(Call<GeoCities> call, Throwable t) {
                    Log.e(LOG_TAG,"error:"+t.getMessage());
                    showCouldNotFindCity();
                    setProgressBarVisible(false);
                }
            });
        } else {
            clear();
            setImageViewVisible(true);
            setSearchStateVisible(true);
            setCityListVisible(false);
            showStartTyping();
        }
    }

    private void addCities(List<GeoCity> list) {
        if (mAdapter != null) {
              mAdapter.clear();
              mAdapter.addCities(list);
        }
    }

    private void clear() {
        if (mAdapter != null) {
            mAdapter.clear();
        }
    }

    private void setCityListVisible(boolean visible) {
        if (mCityListView == null) {
            return;
        }
        if (visible) {
            mCityListView.setVisibility(View.VISIBLE);
        } else {
            mCityListView.setVisibility(View.GONE);
        }
    }


    private void setProgressBarVisible(boolean visible) {
        if (mProgressBar == null) {
            return;
        }
        if (visible) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void setImageViewVisible(boolean visible) {
        if (mImageView != null) {
            if (visible) {
                mImageView.setVisibility(View.VISIBLE);
            } else {
                mImageView.setVisibility(View.GONE);
            }
        }
    }

    private void setSearchStateVisible(boolean visible) {
        if (mSearchState != null) {
            if (visible) {
                mSearchState.setVisibility(View.VISIBLE);
            } else {
                mSearchState.setVisibility(View.GONE);
            }
        }
    }

    private void showCouldNotFindCity() {
        mSearchState.setText(getResources().getString(R.string.could_not_find_a_city));
        clear();
    }

    private void showStartTyping() {
        mSearchState.setText(getResources().getString(R.string.start_typing));
    }
}
