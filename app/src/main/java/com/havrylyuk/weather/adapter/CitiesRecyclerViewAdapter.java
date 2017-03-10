package com.havrylyuk.weather.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.havrylyuk.weather.R;
import com.havrylyuk.weather.dao.OrmCity;
import com.havrylyuk.weather.data.model.CityWithWeather;
import com.havrylyuk.weather.util.PreferencesHelper;
import com.havrylyuk.weather.util.Utility;

import java.util.List;

/**
 *
 * Created by Igor Havrylyuk on 11.02.2017.
 */

public class CitiesRecyclerViewAdapter extends RecyclerView.Adapter<CitiesRecyclerViewAdapter.ViewHolder> {



    public interface CitiesRecyclerViewItemListener {
        void onItemClick(CityWithWeather city, View view);
    }

    private Context context;
    private List<CityWithWeather> mCities;
    private CitiesRecyclerViewItemListener mListener;
    private int mCurrentPosition = -1;
    private  boolean isMetric;

    public CitiesRecyclerViewAdapter(Context context, List<CityWithWeather> cities) {
        mCities = cities;
        this.context = context;
        PreferencesHelper pref =  PreferencesHelper.getInstance();
        isMetric = context.getString(R.string.pref_unit_default_value)
                .equals(pref.getUnits(context));
    }

    public void setListener(CitiesRecyclerViewItemListener listener) {
        this.mListener = listener;
    }

    public void addCity(CityWithWeather city) {
        if (!haveCityYet(city)) {
            mCities.add(mCities.size(), city);
            notifyItemChanged(mCities.size());
        }
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void setCurrentPosition(int value) {
        mCurrentPosition = value;
        notifyDataSetChanged();
    }

    private boolean haveCityYet(CityWithWeather cityWithWeather) {
        for (CityWithWeather city : mCities) {
            if (city.getCity().get_id().equals(cityWithWeather.getCity().get_id())) {
                return true;
            }
        }
        return false;
    }

    public void setCities(List<CityWithWeather> cities) {
        mCities.clear();
        mCities.addAll(mCities.size(), cities);
        notifyDataSetChanged();
    }

    public void addCities(List<CityWithWeather> cities) {
        mCities.addAll(mCities.size(), cities);
        notifyDataSetChanged();
    }

    public void removeCity(int location) {
        mCities.remove(location);
        notifyItemRemoved(location);
    }

    public OrmCity getCity(int location) {
        return mCities.get(location).getCity();
    }

    public void clear() {
        mCities.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.city = mCities.get(position);
        holder.contentView.setText(holder.city.getCity().getCity_name());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            holder.imageView.setTransitionName(holder.contentView.getClass().getName() + position);
        }
        if (holder.city.getWeather() != null) {
            Resources res = holder.view.getResources();
            String temperatureText = holder.city.getWeather().getTemp() > 0 ?
                    res.getString(R.string.temperature_plus, holder.city.getWeather().getTemp(),isMetric ? "°C" : "°F") :
                    res.getString(R.string.temperature_minus, holder.city.getWeather().getTemp(),isMetric ? "°C" : "°F");
            holder.temperatureView.setText(temperatureText);
            if (holder.windView != null) {
                    String windText = res.getString(R.string.format_wind,
                            holder.city.getWeather().getWind_speed(),
                            isMetric?"m/s":"mph",
                            holder.city.getWeather().getWind_dir());
                    holder.windView.setText(windText);

            }
            if (holder.humidity != null) {
                String holderText = res.getString(R.string.format_humidity,
                        holder.city.getWeather().getHumidity());
                holder.humidity.setText(holderText);
            }
            if (holder.city.getWeather().getIcon() != null) {
                String fileName = Utility.getImageWithForecast(holder.city.getWeather().getCondition_code(),
                        holder.city.getWeather().getIs_day());
                holder.imageView.setImageURI(Uri.parse("asset:///"+ fileName));
            }
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(mCurrentPosition);
                mCurrentPosition = holder.getAdapterPosition();
                if (mListener != null) {
                    mListener.onItemClick(holder.city,holder.imageView);
                }
            }
        });
        holder.view.setSelected(mCurrentPosition == position);
    }

    @Override
    public int getItemCount() {
        return mCities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView contentView;
        public TextView temperatureView;
        public TextView windView;
        public TextView humidity;
        public SimpleDraweeView imageView;
        public CityWithWeather city;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.contentView = (TextView) view.findViewById(R.id.city_name);
            this.temperatureView = (TextView) view.findViewById(R.id.temperature);
            this.windView = (TextView) view.findViewById(R.id.wind);
            this.humidity = (TextView) view.findViewById(R.id.humidity);
            this.imageView = (SimpleDraweeView) view.findViewById(R.id.state_image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + contentView.getText() + "'";
        }
    }
}
