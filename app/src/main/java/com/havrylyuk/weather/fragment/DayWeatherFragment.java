package com.havrylyuk.weather.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.havrylyuk.weather.R;
import com.havrylyuk.weather.adapter.HoursRecyclerViewAdapter;
import com.havrylyuk.weather.dao.OrmWeather;
import com.havrylyuk.weather.data.local.ILocalDataSource;
import com.havrylyuk.weather.data.local.LocalDataSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by Igor Havrylyuk on 17.02.2017.
 */
public class DayWeatherFragment extends Fragment  {

    public final static String CITY_ID = "city_id";
    public final static String DATE = "date";
    private HoursRecyclerViewAdapter mAdapter;

    public DayWeatherFragment() {

    }

    public static DayWeatherFragment getInstance(int cityId, long time) {
        Bundle args = new Bundle();
        args.putInt(CITY_ID, cityId);
        args.putLong(DATE, time);
        DayWeatherFragment dayWeatherFragment = new DayWeatherFragment();
        dayWeatherFragment.setArguments(args);
        return dayWeatherFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabs, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.hour_list);
        setupRecyclerView(recyclerView);
        ILocalDataSource localDataSource = LocalDataSource.getInstance(getContext());
        addWeathersToList(localDataSource.getForecast(getArguments().getInt(CITY_ID),
                new Date(getArguments().getLong(DATE))));

        return view;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new HoursRecyclerViewAdapter(getActivity(), new ArrayList<OrmWeather>());
        mAdapter.setListener(new HoursRecyclerViewAdapter.OnIconClickListener() {
            @Override
            public void onIconClick(OrmWeather weather, View view) {
                Toast.makeText(getActivity(),weather.getCondition_text(),Toast.LENGTH_LONG).show();
            }
        });
        recyclerView.setAdapter(mAdapter);
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }


    public void addWeathersToList(List<OrmWeather> weatherList) {
        mAdapter.addElements(weatherList);
    }

    public void addWeatherToList(OrmWeather weather) {
        mAdapter.addElement(weather);
    }


}