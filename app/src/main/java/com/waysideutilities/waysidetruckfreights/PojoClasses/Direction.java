package com.waysideutilities.waysidetruckfreights.PojoClasses;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Archana on 4/4/2017.
 */

public class Direction {
    private List<List<HashMap<String, String>>> routes;
    private String startAddress;
    private String endAddress;
    private LatLng strtLatLng;
    private LatLng endLatLng;


    public List<List<HashMap<String, String>>> getRoutes() {
        return routes;
    }

    public void setRoutes(List<List<HashMap<String, String>>> routes) {
        this.routes = routes;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public LatLng getStrtLatLng() {
        return strtLatLng;
    }

    public void setStrtLatLng(LatLng strtLatLng) {
        this.strtLatLng = strtLatLng;
    }

    public LatLng getEndLatLng() {
        return endLatLng;
    }

    public void setEndLatLng(LatLng endLatLng) {
        this.endLatLng = endLatLng;
    }
}
