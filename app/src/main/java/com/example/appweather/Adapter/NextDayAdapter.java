package com.example.appweather.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appweather.Common.Common;
import com.example.appweather.Model.WeatherNextDayResult;
import com.example.appweather.R;
import com.squareup.picasso.Picasso;

public class NextDayAdapter extends RecyclerView.Adapter<NextDayAdapter.MyViewHolder> {
    Context context;
    WeatherNextDayResult weatherNextDayResult;

    public NextDayAdapter(Context context, WeatherNextDayResult weatherNextDayResult) {
        this.context = context;
        this.weatherNextDayResult = weatherNextDayResult;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.item_nextdays, parent, false);

        return new MyViewHolder(itemview);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//      load image
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/wn/")
                .append(weatherNextDayResult.list.get(position).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.img_weather_nd);

        holder.txt_date_time_nd.setText(new StringBuilder(Common.convertUnixToDate(weatherNextDayResult
                .list.get(position).dt)));

        holder.txt_description_nd.setText(new StringBuilder(weatherNextDayResult.list.get(position)
                .weather.get(0).getDescription()));

        holder.txt_temperature_nd.setText(new StringBuilder(String.valueOf(weatherNextDayResult.list.get(position)
                .main.getTemp())).append("ºC"));

    }

    @Override
    public int getItemCount() {
        return weatherNextDayResult.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_date_time_nd, txt_description_nd, txt_temperature_nd;
        ImageView img_weather_nd;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_weather_nd = itemView.findViewById(R.id.img_weather_nd);
            txt_date_time_nd = itemView.findViewById(R.id.txt_date);
            txt_description_nd = itemView.findViewById(R.id.txt_description_nd);
            txt_temperature_nd = itemView.findViewById(R.id.txt_temp_nd);
        }
    }
}
