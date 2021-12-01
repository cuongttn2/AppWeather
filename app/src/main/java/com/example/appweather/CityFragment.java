package com.example.appweather;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appweather.Common.Common;
import com.example.appweather.Model.WeatherResult;
import com.example.appweather.Retrofit.IOpenWeatherMap;
import com.example.appweather.Retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class CityFragment extends Fragment {

    private List<String> listCity;
    private MaterialSearchBar searchBar;


    ImageView img_weather;
    TextView txt_city_name, txt_humidity, txt_sunset, txt_geocoord, txt_date_time, txt_temp_max, txt_temp_min,
            txt_sunrise, txt_temp, txt_description, txt_wind, txt_pressure, txt_feel_like;
    LinearLayout city_panel;
    ProgressBar loading_city;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    static CityFragment instance;

    public static CityFragment getInstance() {
        if (instance == null) {
            instance = new CityFragment();
        }
        return instance;
    }

    public CityFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemview = inflater.inflate(R.layout.fragment_city, container, false);

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

        city_panel = itemview.findViewById(R.id.city_panel);
        loading_city = itemview.findViewById(R.id.loading_city);

        searchBar = itemview.findViewById(R.id.search_bar);
        searchBar.setEnabled(false);


        new LoadCities().execute();

        return itemview;

    }



    private class LoadCities extends SimpleAsyncTask<List<String>> {
        @Override
        protected List<String> doInBackgroundSimple() {
            listCity = new ArrayList<>();

            try {
                StringBuilder builder = new StringBuilder();
                InputStream is = getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream = new GZIPInputStream(is);

                InputStreamReader reader = new InputStreamReader(gzipInputStream);
                BufferedReader in = new BufferedReader(reader);

                String readed;

                while ((readed = in.readLine()) != null)
                    builder.append(readed);
                listCity = new Gson().fromJson(builder.toString(),new TypeToken<List<String>>(){}.getType());



            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            return listCity;
        }
//

        @Override
        protected void onSuccess(List<String> listCity) {
            super.onSuccess(listCity);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<String> suggest = new ArrayList<>();
                    for(String search : listCity){
                        if(search.toLowerCase().contains(searchBar.getText().toLowerCase())){
                            suggest.add(search);
                        }
                    }
                    searchBar.setLastSuggestions(suggest);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInformation(text.toString());

                    searchBar.setLastSuggestions(listCity);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });

            searchBar.setLastSuggestions(listCity);

            loading_city.setVisibility(View.GONE);


        }

    }

    private void getWeatherInformation(String cityName) {
        compositeDisposable.add(mService.getWeatherByCityName(cityName,
                Common.API_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
//                Load image
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/wn/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                                .append(".png").toString()).into(img_weather);

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
                        city_panel.setVisibility(View.VISIBLE);
                        loading_city.setVisibility(View.GONE);
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