package com.havrylyuk.weather.fragment;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.havrylyuk.weather.R;
import com.havrylyuk.weather.WeatherApp;
import com.havrylyuk.weather.adapter.DaysViewPagerAdapter;
import com.havrylyuk.weather.dao.OrmWeather;
import com.havrylyuk.weather.data.local.ILocalDataSource;
import com.havrylyuk.weather.data.model.DayPager;
import com.havrylyuk.weather.util.PreferencesHelper;
import com.havrylyuk.weather.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * Created by Igor Havrylyuk on 17.02.2017.
 */
public class CityDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_NAME = "item_name";
    private DaysViewPagerAdapter viewPagerAdapter;
    private FrameLayout progressFrame;
    private TabLayout tabLayout;
    private boolean waitAnimations;
    private ILocalDataSource localDataSource;
    private boolean isMetric;
    private PreferencesHelper pref;
    private long cityId;

    public CityDetailFragment() {

    }

    public static CityDetailFragment getInstance(long itemId, String itemName) {
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, itemId);
        args.putString(ARG_ITEM_NAME, itemName);
        CityDetailFragment cityDetailFragment = new CityDetailFragment();
        cityDetailFragment.setArguments(args);
        return cityDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferencesHelper.getInstance();
        localDataSource = ((WeatherApp) getActivity().getApplicationContext()).getLocalDataSource();
        if (savedInstanceState != null) {
            cityId = getArguments().getLong(ARG_ITEM_ID);
            openCity((int) cityId);
            localDataSource.getForecast((int) cityId);
        }
    }

    private void openCity(int cityId) {
        makeView(localDataSource.getForecast(cityId));
    }

    private void makeView(List<OrmWeather> forecasts) {
        Utility.sortWeatherHour(forecasts);
        if (forecasts.size() > 0) {
            OrmWeather current = forecasts.get(0);//current weather
            setTemp(current.getTemp());
            setHumidity(current.getHumidity());
            setWindSpeed(current.getWind_speed(), current.getWind_dir());
            if (current.getPressure()!=null && current.getPressure() > 0){
                setPressure(current.getPressure());
            }
            setCondition(current.getCondition_text());
            setDate(current.getDt());
            setImage(Utility.getImageWithForecast(current.getCondition_code(),current.getIs_day()));
            addDaysToViewPager(forecasts);
        } else {
            showError();
        }
    }

    private void addDaysToViewPager(List<OrmWeather> hours) {
        List<DayPager> days = new ArrayList<>();
        Calendar firstDate = Calendar.getInstance();
        firstDate.setTime(hours.get(0).getDt());
        DayPager day = new DayPager();
        day.setHours(new ArrayList<OrmWeather>());
        days.add(day);
        for (OrmWeather hourWeather : hours) {
            Calendar nextDate = Calendar.getInstance();
            nextDate.setTime(hourWeather.getDt());
            if (firstDate.get(Calendar.YEAR) == nextDate.get(Calendar.YEAR)
                    && firstDate.get(Calendar.DAY_OF_YEAR) - nextDate.get(Calendar.DAY_OF_YEAR) == 0) {
                day.getHours().add(hourWeather);
            } else {
                day = new DayPager();
                day.setHours(new ArrayList<OrmWeather>());
                day.getHours().add(hourWeather);
                days.add(day);
            }
            firstDate = nextDate;
        }
        addDays(days);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.city_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isMetric = getString(R.string.pref_unit_default_value)
                .equals(pref.getUnits(getActivity()));
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        FragmentManager fragmentManager = getChildFragmentManager();
        viewPagerAdapter = new DaysViewPagerAdapter(fragmentManager, new ArrayList<DayPager>());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressFrame = (FrameLayout) getActivity().findViewById(R.id.progressFrame);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPagerAdapter.clear();
        tabLayout.setVisibility(View.VISIBLE);
        if (!waitAnimations) {
            loadContent();
        }
    }

    public void waitAnimations() {
        waitAnimations = true;
    }

    public void loadContent() {
        long cityId = getArguments().getLong(ARG_ITEM_ID);
        openCity((int) cityId);
        waitAnimations = false;
    }

    public void clearContent() {
        tabLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showProgressBar(boolean show) {
        if (progressFrame == null) {
            return;
        }
        if (show) {
            progressFrame.setVisibility(View.VISIBLE);
        } else {
            progressFrame.setVisibility(View.GONE);
        }
    }

    public void setTemp(double temperature) {
        TextView view = (TextView) getActivity().findViewById(R.id.header_temp);
        if (view != null) {
            Resources res = getResources();
            String temperatureText = temperature > 0 ?
                    res.getString(R.string.format_temp_plus, temperature, isMetric ? "°C" : "°F") :
                    res.getString(R.string.format_temp_minus, temperature, isMetric ? "°C" : "°F");
            view.setText(temperatureText);
        }
    }

    public void setHumidity(int humidity) {
        TextView view = (TextView) getActivity().findViewById(R.id.header_humidity);
        if (view != null) {
            Resources res = getResources();
            String humidityText = res.getString(R.string.format_humidity, humidity);
            view.setText(humidityText);
        }
    }

    public void setWindSpeed(double windSpeed, String windDir) {
        TextView view = (TextView) getActivity().findViewById(R.id.header_wind);
        if (view != null) {
            Resources res = getResources();
            String windText = res.getString(R.string.format_wind,
                     windSpeed, isMetric?"m/s":"mph", windDir);
            view.setText(windText);
        }
    }

    public void setPressure(double pressure) {
        TextView view = (TextView) getActivity().findViewById(R.id.header_pressure);
        if (view != null) {
            Resources res = getResources();
            String pressureText = res.getString(R.string.format_pressure, pressure, isMetric ? "mmHg." : "psi");
            view.setText(pressureText);
        }
    }

    public void setCondition(String condition) {
        TextView view = (TextView) getActivity().findViewById(R.id.header_condition);
        if (view != null) {
            view.setText(condition);
        }
    }

    public void setDate(@NonNull Date date) {
        TextView view = (TextView) getActivity().findViewById(R.id.header_date);
        if (view != null) {
            SimpleDateFormat format =
                    new SimpleDateFormat("EE, dd MMM, HH:mm", Locale.getDefault());
            String value = format.format(date);
            view.setText(value);
        }
    }

    public void setImage(@NonNull String fileName) {
        SimpleDraweeView imageView =
                (SimpleDraweeView) getActivity().findViewById(R.id.backdrop);
        if (imageView != null) {
            imageView.setImageURI(Uri.parse("asset:///"+ fileName));
        }
    }

    public void addDays(List<DayPager> days) {
        if (viewPagerAdapter != null) {
            viewPagerAdapter.addDays(days);
        }
    }

    public void showError() {
        View view = getActivity().findViewById(R.id.viewpager);
        if (view != null) {
            String text = getResources().getString(R.string.error) + ": ";
            text += getResources().getString(R.string.failed_to_load_weather);
            Snackbar.make(view, text, Snackbar.LENGTH_LONG).show();
        }
    }
}
