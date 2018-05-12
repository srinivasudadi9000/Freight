package com.waysideutilities.waysidetruckfreights.PojoClasses;

/**
 * Created by Archana on 1/20/2017.
 */
public class GeoPoint {
    private double lat;
    private double log;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLog() {
        return log;
    }

    public void setLog(double log) {
        this.log = log;
    }

    public GeoPoint() {
    }

    public GeoPoint(double lat, double log) {
        this.lat = lat;
        this.log = log;
    }
}
