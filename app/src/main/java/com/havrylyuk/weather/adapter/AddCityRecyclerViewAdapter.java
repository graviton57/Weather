package com.havrylyuk.weather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.havrylyuk.weather.R;
import com.havrylyuk.weather.data.model.SearchResult;

import java.util.List;

/**
 *
 * Created by Igor Havrylyuk on 17.02.2017.
 */

public class AddCityRecyclerViewAdapter extends RecyclerView.Adapter<AddCityRecyclerViewAdapter.ViewHolder>  {

    public interface AddCityRecyclerViewItemListener {
        void onItemClick(SearchResult city);
    }

    private final List<SearchResult> cities;
    private AddCityRecyclerViewItemListener listener;

    public AddCityRecyclerViewAdapter(List<SearchResult> cities , AddCityRecyclerViewItemListener listener) {
        this.cities = cities;
        this.listener = listener;
    }

    public void addCity(SearchResult city) {
        cities.add(cities.size(), city);
        notifyItemInserted(cities.size());
    }

    public void addCities(List<SearchResult> cities) {
        this.cities.addAll(this.cities.size(), cities);
        notifyDataSetChanged();
    }

    public void clear() {
        cities.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_add_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(cities.get(holder.getAdapterPosition()));
                }
            }
        });
        holder.cityName.setText(cities.get(position).getName());
        holder.imageView.setImageResource(R.drawable.city);
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView cityName;
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.cityName = (TextView) view.findViewById(R.id.city_name);
            this.imageView = (ImageView) view.findViewById(R.id.ic_city);
        }
    }


}
