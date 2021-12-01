package com.example.appweather;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.example.appweather.Adapter.NextDayAdapter;
import com.example.appweather.Common.Common;
import com.example.appweather.Model.WeatherNextDayResult;
import com.example.appweather.Retrofit.IOpenWeatherMap;
import com.example.appweather.Retrofit.RetrofitClient;

import org.w3c.dom.Text;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class NextDayFragment extends Fragment {

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    TextView txt_cityName_nd, txt_geocoord_nd;
    RecyclerView recyclerView;

    static NextDayFragment instance;

    public static NextDayFragment getInstance() {
        if (instance == null)
            instance = new NextDayFragment();
        return instance;
    }

    public NextDayFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_next_day, container, false);

        txt_cityName_nd = v.findViewById(R.id.txt_city_name_nd);
        txt_geocoord_nd = v.findViewById(R.id.txt_geocoord_nd);

        recyclerView = v.findViewById(R.id.rv_nextdays);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL,false));
        getNextDaysInfomation();

        return v;
    }

    private void getNextDaysInfomation() {
        compositeDisposable.add(mService.getWeatherNextDayByLatLon(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.API_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherNextDayResult>() {
                    @Override
                    public void accept(WeatherNextDayResult weatherNextDayResult) throws Exception {
                        displayNextDaysWeather(weatherNextDayResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("ERROR", "" + throwable.getMessage());
                    }
                })
        );
    }

    private void displayNextDaysWeather(WeatherNextDayResult weatherNextDayResult) {
        txt_cityName_nd.setText(new StringBuilder(weatherNextDayResult.city.name));
        txt_geocoord_nd.setText(new StringBuilder(weatherNextDayResult.city.coord.toString()));

        NextDayAdapter adapter = new NextDayAdapter(getContext(),weatherNextDayResult);
        recyclerView.setAdapter(adapter);
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