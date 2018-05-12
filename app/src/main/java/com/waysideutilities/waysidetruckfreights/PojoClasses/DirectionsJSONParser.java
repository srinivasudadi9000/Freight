package com.waysideutilities.waysidetruckfreights.PojoClasses;

import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Archana on 8/3/2017.
 */

public class DirectionsJSONParser {

    private AlertDialog dialog;
    private String end_address,start_address;
    private String end_location_lat,end_location_lng,start_location_lat,start_location_lng;

    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */
    public Direction parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        Direction direction = new Direction();

        try {
            jRoutes = jObject.getJSONArray("routes");
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    end_address = ((JSONObject) jLegs.get(j)).getString("end_address");
                    start_address = ((JSONObject) jLegs.get(j)).getString("start_address");

                    end_location_lat = ((JSONObject) jLegs.get(j)).getJSONObject("end_location").getString("lat");
                    end_location_lng = ((JSONObject) jLegs.get(j)).getJSONObject("end_location").getString("lng");

                    start_location_lat = ((JSONObject) jLegs.get(j)).getJSONObject("start_location").getString("lat");
                    start_location_lng = ((JSONObject) jLegs.get(j)).getJSONObject("start_location").getString("lng");


                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        direction.setRoutes(routes);
        direction.setStartAddress(start_address);
        direction.setEndAddress(end_address);
        direction.setStrtLatLng(new LatLng(Double.parseDouble(start_location_lat),Double.parseDouble(start_location_lng)));
        direction.setEndLatLng(new LatLng(Double.parseDouble(end_location_lat),Double.parseDouble(end_location_lng)));
        return direction;
    }

    public DistanceTime parseDistance(JSONObject jObject){
        JSONArray jsonArrayObject;
        JSONArray jsonArray;
        String distance,duration;
        DistanceTime distanceTime = new DistanceTime();
        try {
            jsonArrayObject = jObject.getJSONArray("rows");
            distanceTime.setDestination_addresses(jObject.getString("destination_addresses"));
            for (int i = 0; i < jsonArrayObject.length(); i++) {
                jsonArray = ((JSONObject) jsonArrayObject.get(i)).getJSONArray("elements");
                for (int j = 0; j < jsonArray.length(); j++) {
                    distance = ((JSONObject)jsonArray.get(j)).getJSONObject("distance").getString("value");
                    duration = ((JSONObject)jsonArray.get(j)).getJSONObject("duration").getString("value");
                    distanceTime.setDistance(distance);
                    distanceTime.setDuration(duration);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return distanceTime;
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}