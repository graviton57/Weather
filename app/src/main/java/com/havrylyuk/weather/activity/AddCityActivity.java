package com.havrylyuk.weather.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.havrylyuk.weather.R;
import com.havrylyuk.weather.adapter.AddCityRecyclerViewAdapter;
import com.havrylyuk.weather.dao.OrmCity;
import com.havrylyuk.weather.data.local.ILocalDataSource;
import com.havrylyuk.weather.data.local.LocalDataSource;
import com.havrylyuk.weather.data.model.SearchResult;
import com.havrylyuk.weather.data.remote.ApiClient;
import com.havrylyuk.weather.data.remote.OpenWeatherService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * Created by Igor Havrylyuk on 16.02.2017.
 */
public class AddCityActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddCityActivity.class.getSimpleName();
    private AddCityRecyclerViewAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView mSearchState;
    private ImageView mImageView;
    private View mCityListView;

    private MenuItem searchItem;
    private SearchView searchView;
    private String searchQuery;

    private ILocalDataSource localDataSource;
    private OpenWeatherService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        localDataSource = LocalDataSource.getInstance(getApplicationContext());
        service = ApiClient.getClient().create(OpenWeatherService.class);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.city_search_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);
        mSearchState = (TextView) findViewById(R.id.textView);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        assert mProgressBar != null;
        mProgressBar.setVisibility(View.GONE);
        mCityListView = findViewById(R.id.city_search_list_container);
        if (mCityListView != null) {
            mCityListView.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        mAdapter = new AddCityRecyclerViewAdapter(new ArrayList<SearchResult>(),
                        new AddCityRecyclerViewAdapter.AddCityRecyclerViewItemListener() {
            @Override
            public void onItemClick(SearchResult newCity) {
                OrmCity city = new OrmCity(newCity.getId(),newCity.getName(),newCity.getRegion(),
                        newCity.getCountry(),newCity.getLat(),newCity.getLon());
                localDataSource.saveCity(city);
                Intent intent = new Intent(AddCityActivity.this, CitiesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchItem = menu.findItem(R.id.action_search);
        setupSearchView(searchItem);
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
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            searchView.clearFocus();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
            });
        }
    }

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
            Call<List<SearchResult>> responseCall = service.findCity(ApiClient.API_KEY, searchQuery);
            responseCall.enqueue(new Callback<List<SearchResult>>() {
                @Override
                public void onResponse(Call<List<SearchResult>> call, Response<List<SearchResult>> response) {
                    Log.d(LOG_TAG,"Success onResponse list size="+response.body().size());
                    if (!response.body().isEmpty()) {
                        addCities(response.body());
                        setCityListVisible(true);
                        setProgressBarVisible(false);
                    } else {
                        showCouldNotFindCity();
                    }
                }

                @Override
                public void onFailure(Call<List<SearchResult>> call, Throwable t) {
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

    private void addCities(List<SearchResult> list) {
        if (mAdapter != null) {
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
    }

    private void showStartTyping() {
        mSearchState.setText(getResources().getString(R.string.start_typing));
    }


}
