package com.example.appweather.Retrofit;

import com.example.appweather.Model.WeatherNextDayResult;
import com.example.appweather.Model.WeatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {
    @GET("weather")
    Observable<WeatherResult> getWeatherResultByLatLon(@Query("lat") String lat,
                                                       @Query("lon") String lon,
                                                       @Query("appid") String appid,
                                                       @Query("units") String unit);

    @GET("weather")
    Observable<WeatherResult> getWeatherByCityName(@Query("q") String cityName,
                                                       @Query("appid") String appid,
                                                       @Query("units") String unit);


    @GET("forecast")
    Observable<WeatherNextDayResult> getWeatherNextDayByLatLon(@Query("lat") String lat,
                                                       @Query("lon") String lon,
                                                       @Query("appid") String appid,
                                                       @Query("units") String unit);

}
