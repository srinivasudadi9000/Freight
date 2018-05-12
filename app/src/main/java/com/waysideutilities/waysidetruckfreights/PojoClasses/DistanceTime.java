package com.waysideutilities.waysidetruckfreights.PojoClasses;

/**
 * Created by Archana on 12/4/2017.
 */

public class DistanceTime {
    private  String distance;
    private  String  duration;
    private  String destination_addresses;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDestination_addresses() {
        return destination_addresses;
    }

    public void setDestination_addresses(String destination_addresses) {
        this.destination_addresses = destination_addresses;
    }

    @Override
    public String toString() {
        return "DistanceTime{" +
                "distance='" + distance + '\'' +
                ", duration='" + duration + '\'' +
                ", destination_addresses='" + destination_addresses + '\'' +
                '}';
    }
}
