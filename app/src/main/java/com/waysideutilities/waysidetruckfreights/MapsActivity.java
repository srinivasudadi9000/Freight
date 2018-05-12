package com.waysideutilities.waysidetruckfreights;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Direction;
import com.waysideutilities.waysidetruckfreights.PojoClasses.DirectionsJSONParser;
import com.waysideutilities.waysidetruckfreights.PojoClasses.GPSTracker;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;
import com.waysideutilities.waysidetruckfreights.helper.HTTPhelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.REQUEST_PERMISSIONS;
/**
 * Created by Archana on 8/29/2016.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Bundle bundle = null;
    private String origin, destination, truckId, loadId;
    private int coreLocationPermission, fineLocationPermission;
    private GPSTracker gps;
    Marker marker;
    private Marker myMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            accessPermission();
        } else {
            showMap();
        }
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 00000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {}

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {
                    //Toast.makeText(MapsTruckActivity.this,"onProviderDisabled",Toast.LENGTH_SHORT).show();
                    gps = new GPSTracker(MapsActivity.this);
                    gps.showSettingsAlert();
                }
            });
        }
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("status").equals("NOT_FOUND")) {
                    Toast.makeText(MapsActivity.this, R.string.add_correct_address, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new ParserTask().execute(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception while url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, Direction> {
        // Parsing the data in non-ui thread
        @Override
        protected Direction doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            Direction direction = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                // Starts parsing data
                direction = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return direction;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(Direction result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
           // MarkerOptions markerOptions = new MarkerOptions();
            if (result != null) {
                // Traversing through all the routes
                for (int i = 0; i < result.getRoutes().size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();
                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.getRoutes().get(i);
                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }
                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(15);
                    lineOptions.color(Color.BLUE);
                }
                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);
                mMap.addMarker(new MarkerOptions().title(result.getStartAddress()).position(new LatLng(result.getStrtLatLng().latitude, result.getStrtLatLng().longitude)).draggable(false).visible(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions().title(result.getEndAddress()).position(new LatLng(result.getEndLatLng().latitude, result.getEndLatLng().longitude)).draggable(false).visible(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
               //setMarkerOption(result.getStrtLatLng().latitude,result.getStrtLatLng().longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(result.getStrtLatLng().latitude, result.getStrtLatLng().longitude), 7));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(result.getStrtLatLng().latitude, result.getStrtLatLng().longitude)).zoom(12).bearing(0).tilt(30).build()));

            }
        }
    }

   /* private void setMarkerOption(double Latitude, double Longitude) {
        if (myMarker != null) {
            myMarker.remove();
        }
        myMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Latitude, Longitude)).draggable(true).visible(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Latitude, Longitude), 15));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(Latitude, Longitude)).zoom(12).bearing(0).tilt(30).build()));
    }*/

    private void accessPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            coreLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            fineLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (coreLocationPermission == 0 && fineLocationPermission == 0) {
            showMap();
        } else {
            checkForPermission();
        }
    }

    private void checkForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int count = 0;
            String[] permissions = new String[]{"", ""};
            if ((coreLocationPermission != PackageManager.PERMISSION_GRANTED) || (fineLocationPermission != PackageManager.PERMISSION_GRANTED)) {
                if (coreLocationPermission != PackageManager.PERMISSION_GRANTED) {
                    permissions[count++] = Manifest.permission.ACCESS_COARSE_LOCATION;
                }
                if (fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
                    permissions[count] = Manifest.permission.ACCESS_FINE_LOCATION;
                }
                requestPermissions(permissions, REQUEST_PERMISSIONS);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if ((grantResults.length == 2) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(MapsActivity.this, "CORE LOCATION PERMISSION_GRANTED", Toast.LENGTH_SHORT);
                    coreLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                    fineLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                    if (coreLocationPermission == 0 && fineLocationPermission == 0) {
                        showMap();
                    }
                } else if ((grantResults[0] == PackageManager.PERMISSION_DENIED) && (grantResults[1] == PackageManager.PERMISSION_DENIED)) {
                    Toast.makeText(MapsActivity.this, "FINE LOCATION permission allows us to access phone state. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    coreLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                    fineLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                    if (coreLocationPermission == 0 && fineLocationPermission == 0) {
                        showMap();
                    }
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(MapsActivity.this, "CORE LOCATION permission allows us to access phone state. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void showMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Get Lat log from server
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setTrafficEnabled(true);
        if (coreLocationPermission == 0 && fineLocationPermission == 0) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;
            bundle = getIntent().getBundleExtra("BUNDLE");
            if (bundle != null) {
                origin = bundle.getString("ORIGIN");
                destination = bundle.getString("DEST");
                truckId = bundle.getString("TRUCK_ID");
                loadId = bundle.getString("LOAD_ID");
                // Getting URL to the Google Directions API
                String url = FrightUtils.getDirectionsUrl(origin, destination, MapsActivity.this);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
                callAsynchronousTask();
            }
        } else {
            checkForPermission();
        }
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new Get_Lat_Long().execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 30000); //execute in every 30000 ms
    }

    private class Get_Lat_Long extends AsyncTask<Void, Void, InputStream> {
        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            String newString = null;
            try {
                newString = String.format("posttruck_id=%s", truckId);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/get_tracking_details.php", "GET", newString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            if (inputStream != null) {
                StringBuilder builder = new StringBuilder();
                InputStreamReader streamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(streamReader);
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String stringResult = builder.toString();
                Log.e("get_tracking_details ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Double lat = Double.parseDouble(jsonObject.getString("lattitude"));
                        Double log = Double.parseDouble(jsonObject.getString("longitude"));
                        if (marker != null) {
                            marker.remove();
                        }
                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, log)).draggable(true).visible(true).icon(getMarkerIconFromDrawable(getResources().getDrawable(R.drawable.truck_red))));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, log), 15));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(lat, log)).zoom(15).bearing(0).tilt(30).build()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MapsActivity.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }
    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}





































/*    private static List<Address> getAddrByWeb(JSONObject jsonObject) {
        List<Address> res = new ArrayList<Address>();
        try {
            JSONArray array = (JSONArray) jsonObject.get("results");
            for (int i = 0; i < array.length(); i++) {
                Double lon = new Double(0);
                Double lat = new Double(0);
                String name = "";
                try {
                    lon = array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    lat = array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    name = array.getJSONObject(i).getString("formatted_address");
                    Address addr = new Address(Locale.getDefault());
                    addr.setLatitude(lat);
                    addr.setLongitude(lon);
                    addr.setAddressLine(0, name != null ? name : "");
                    res.add(addr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }


    public GeoPoint getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;
        List<Address> add = getAddrByWeb(getLocationInfo(strAddress));
        try {
            address = coder.getFromLocationName(strAddress, 5);

            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
                    (double) (location.getLongitude() * 1E6));

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
return p1;
        } */

/* public static JSONObject getLocationInfo(String address) {
        StringBuilder stringBuilder = new StringBuilder();
        try {

            address = address.replaceAll(" ", "%20");

            HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }*/