package com.example.appweather.Model;

import java.text.DecimalFormat;

public class Coord {
    private float lon;
    private float lat;

    public Coord() {
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
//        DecimalFormat df = new DecimalFormat("0.00");
//        lon = Float.valueOf(df.format(lon));

        this.lon =  lon ;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
//        DecimalFormat df = new DecimalFormat("0.00");
//        lat = Float.valueOf(df.format(lat));

        this.lat = lat;
    }

    @Override
    public String toString() {
        return new StringBuffer("[").append(this.lat).append(",").append(this.lon).append("]").toString();
    }
}
