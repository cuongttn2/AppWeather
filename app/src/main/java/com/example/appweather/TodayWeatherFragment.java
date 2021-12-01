package com.example.appweather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appweather.Common.Common;
import com.example.appweather.Model.WeatherResult;
import com.example.appweather.Retrofit.IOpenWeatherMap;
import com.example.appweather.Retrofit.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class TodayWeatherFragment extends Fragment {
    Button btn_gotomap;

    ImageView img_weather;
    TextView txt_city_name, txt_humidity, txt_sunset, txt_geocoord, txt_date_time, txt_temp_max, txt_temp_min,
            txt_sunrise, txt_temp, txt_description, txt_wind, txt_pressure, txt_feel_like;
    LinearLayout weather_panel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    static TodayWeatherFragment instance;


    public static TodayWeatherFragment getInstance() {
        if (instance == null) {
            instance = new TodayWeatherFragment();
        }
        return instance;
    }

    public TodayWeatherFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemview = inflater.inflate(R.layout.fragment_today_weather, container, false);
        img_weather = itemview.findViewById(R.id.img_weather);
        txt_city_name = itemview.findViewById(R.id.txt_city_name);
        txt_humidity = itemview.findViewById(R.id.txt_humidity);
        txt_description = itemview.findViewById(R.id.txt_description);
        txt_temp = itemview.findViewById(R.id.txt_temp);
        txt_wind = itemview.findViewById(R.id.txt_wind);
        txt_sunrise = itemview.findViewById(R.id.txt_sunrise);
        txt_sunset = itemview.findViewById(R.id.txt_sunset);
        txt_geocoord = itemview.findViewById(R.id.txt_geocoord);
        txt_date_time = itemview.findViewById(R.id.txt_date_time);
        txt_pressure = itemview.findViewById(R.id.txt_pressure);
        txt_temp_max = itemview.findViewById(R.id.txt_temp_max);
        txt_temp_min = itemview.findViewById(R.id.txt_temp_min);
        txt_feel_like = itemview.findViewById(R.id.txt_feel_like);

        weather_panel = itemview.findViewById(R.id.weather_panel);
        loading = itemview.findViewById(R.id.loading);

        // change to map actyvity
        btn_gotomap = (Button) itemview.findViewById(R.id.btn_gotomap);
        btn_gotomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToMapActivity();
            }
        });

        getWeatherInformation();


        return itemview;
    }


    // change to MapActivity
    private void changeToMapActivity() {
        Intent intent = new Intent(getActivity(),MapsActivity.class);
        intent.putExtra("key_map","mapdata");
        startActivity(intent);
    }

    private void getWeatherInformation() {
        compositeDisposable.add(mService.getWeatherResultByLatLon(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.API_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
//                Load image
                        Picasso.get().load(
                                new StringBuilder("https://openweathermap.org/img/wn/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                                .append(".png").toString()
                        ).into(img_weather);

//              City information
                        txt_wind.setText(new StringBuilder("Speed: ")
                                .append(weatherResult.getWind().getSpeed())
                                .append(" Deg: ")
                                .append(weatherResult.getWind().getDeg()).toString());
                        txt_city_name.setText(weatherResult.getName());
                        txt_humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain()
                                .getPressure())).append(" %").toString());
                        txt_description.setText(new StringBuilder("Weather in ")
                                .append(weatherResult.getName()).toString());
                        txt_temp.setText(new StringBuilder(
                                String.valueOf(weatherResult.getMain()
                                        .getTemp())).append("ºC").toString());
                        txt_temp_max.setText(new StringBuilder(
                                String.valueOf(weatherResult.getMain()
                                        .getTemp_max())).append("ºC").toString());
                        txt_temp_min.setText(new StringBuilder(
                                String.valueOf(weatherResult.getMain()
                                        .getTemp_min())).append("ºC").toString());
                        txt_feel_like.setText(new StringBuilder(
                                String.valueOf(weatherResult.getMain()
                                        .getFeels_like())).append("ºC").toString());
                        txt_sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        txt_sunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
                        txt_geocoord.setText(new StringBuilder(weatherResult.getCoord().toString()).toString());
                        txt_date_time.setText(Common.convertUnixToDate(weatherResult.getDt()));
                        txt_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain()
                                .getPressure())).append(" hpa").toString());
//              Panel
                        weather_panel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }));

    }

    @Override
    public void onDestroyView() {
        compositeDisposable.clear();
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

}