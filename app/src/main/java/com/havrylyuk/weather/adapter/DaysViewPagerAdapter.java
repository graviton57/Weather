package com.havrylyuk.weather.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.havrylyuk.weather.data.model.DayPager;
import com.havrylyuk.weather.fragment.DayWeatherFragment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 *
 * Created by Igor Havrylyuk on 17.02.2017.
 */
public class DaysViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<DayPager> mDays;

    public DaysViewPagerAdapter(FragmentManager fm, List<DayPager> days) {
        super(fm);
        mDays = days;
    }

    public void addDay(DayPager day) {
        mDays.add(day);
        notifyDataSetChanged();
    }

    public void addDays(List<DayPager> days) {
        mDays.addAll(days);
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        SimpleDateFormat format = new SimpleDateFormat("dd E",Locale.getDefault());
        return format.format(mDays.get(position).getHours().get(0).getDt());
    }

    @Override
    public Fragment getItem(int position) {
        int cityId = mDays.get(position).getHours().get(0).getCity_id().intValue();
        long time = mDays.get(position).getHours().get(0).getDt().getTime();
        return DayWeatherFragment.getInstance(cityId, time);
    }

    @Override
    public int getCount() {
        return mDays != null ? mDays.size() : 0;
    }

    public void clear() {
        mDays.clear();
        notifyDataSetChanged();
    }
}
