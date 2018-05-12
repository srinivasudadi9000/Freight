package com.waysideutilities.waysidetruckfreights;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.waysideutilities.waysidetruckfreights.Adapter.TripAdapter;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Direction;
import com.waysideutilities.waysidetruckfreights.PojoClasses.DirectionsJSONParser;
import com.waysideutilities.waysidetruckfreights.PojoClasses.GPSTracker;
import com.waysideutilities.waysidetruckfreights.Profile.FinalPriceActivity;
import com.waysideutilities.waysidetruckfreights.Profile.MyTrips;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;
import static com.waysideutilities.waysidetruckfreights.helper.Constants.REQUEST_PERMISSIONS;

public class MapsTruckActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int coreLocationPermission, fineLocationPermission;
    private GPSTracker gps;
    private double latitude, longitude;
    private Bundle bundle;
    private String origin, destination, truckId, loadId;
    Marker myMarker;
    private ProgressDialog progressDialog;
    private TextView txt_start_trip, txt_stop_trip;
    private String trip_Status;
    private LocationManager locationManager;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_truck);
        txt_start_trip = (TextView) findViewById(R.id.txt_start_trip);
        txt_stop_trip = (TextView) findViewById(R.id.txt_stop_trip);

        txt_start_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + destination));
                startActivity(intent);
                txt_stop_trip.setVisibility(View.VISIBLE);
                txt_start_trip.setVisibility(View.GONE);
            }
        });

        txt_stop_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MapsTruckActivity.this);
                alertBuilder.setMessage(getResources().getString(R.string.stop_trip_message));
                alertBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        trip_Status = "2";
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new UpdateTripStatus(trip_Status).execute();
                    }
                });
                alertBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog = alertBuilder.create();
                dialog.show();
            }
        });
        gps = new GPSTracker(MapsTruckActivity.this);// check if GPS enabled
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            accessPermission();
        } else {
            showMap();
        }
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //Toast.makeText(MapsTruckActivity.this,"onLocationChanged",Toast.LENGTH_SHORT).show();
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                    new Truck_Tracking(location.getLatitude(), location.getLongitude(), truckId).execute();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                    gps = new GPSTracker(MapsTruckActivity.this);
                    if (gps.canGetLocation()) {
                        latitude = 28.614148;
                        longitude = 77.209004;
                        if (myMarker != null) {
                            myMarker.remove();
                        }
                        myMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).draggable(false).visible(false));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(5).bearing(0).tilt(30).build()));

                    }
                }

                @Override
                public void onProviderDisabled(String provider) {
                    //Toast.makeText(MapsTruckActivity.this,"onProviderDisabled",Toast.LENGTH_SHORT).show();
                    gps = new GPSTracker(MapsTruckActivity.this);
                    gps.showSettingsAlert();
                }
            });
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (coreLocationPermission == 0 && fineLocationPermission == 0) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;
            showCurrentLocation();
            // mMap.setTrafficEnabled(true);
            mMap.setMyLocationEnabled(true);

            bundle = getIntent().getBundleExtra("BUNDLE");
            if (bundle != null) {
                origin = bundle.get("ORIGIN").toString();
                destination = bundle.get("DEST").toString();
                truckId = bundle.getString("TRUCK_ID");
                loadId = bundle.getString("LOAD_ID");
                // Getting URL to the Google Directions API
                String url = FrightUtils.getDirectionsUrl(origin, destination, MapsTruckActivity.this);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                new Truck_Tracking(latitude, longitude, truckId).execute();
            }
        } else {
            checkForPermission();
        }
    }

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
                    Toast.makeText(MapsTruckActivity.this, "CORE LOCATION PERMISSION_GRANTED", Toast.LENGTH_SHORT);
                    coreLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                    fineLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                    if (coreLocationPermission == 0 && fineLocationPermission == 0) {
                        showMap();
                    }
                } else if ((grantResults[0] == PackageManager.PERMISSION_DENIED) && (grantResults[1] == PackageManager.PERMISSION_DENIED)) {
                    Toast.makeText(MapsTruckActivity.this, "FINE LOCATION permission allows us to access phone state. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    coreLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                    fineLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                    if (coreLocationPermission == 0 && fineLocationPermission == 0) {
                        showMap();
                    }
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(MapsTruckActivity.this, "CORE LOCATION permission allows us to access phone state. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void showMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.truckMap);
        mapFragment.getMapAsync(this);
    }

    private void setMarkerOption(double Latitude, double Longitude) {
        if (myMarker != null) {
            myMarker.remove();
        }
        myMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Latitude, Longitude)).draggable(false).visible(false));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Latitude, Longitude), 15));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(Latitude, Longitude)).zoom(12).bearing(0).tilt(30).build()));
    }

    private void showCurrentLocation() {
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            setMarkerOption(latitude, longitude);
        }
    }


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
                    Toast.makeText(MapsTruckActivity.this, R.string.add_correct_address, Toast.LENGTH_SHORT).show();
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
            Direction direction = null;
            List<List<HashMap<String, String>>> routes = null;
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
            MarkerOptions markerOptions = new MarkerOptions();
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
                mMap.addMarker(new MarkerOptions().title(result.getStartAddress()).anchor(0.5f, 0.5f).position(new LatLng(result.getStrtLatLng().latitude, result.getStrtLatLng().longitude)).draggable(false).visible(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions().title(result.getEndAddress()).anchor(0.5f, 0.5f).position(new LatLng(result.getEndLatLng().latitude, result.getEndLatLng().longitude)).draggable(false).visible(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        }
    }

    private class Truck_Tracking extends AsyncTask<Void, Void, InputStream> {
        private Location location;
        private double lat;
        private double log;
        private String posttruckId;

        public Truck_Tracking(double latitude, double longitude, String truckId) {
            this.lat = latitude;
            this.log = longitude;
            this.posttruckId = truckId;
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("lattitude", this.lat);
                jsonObject.accumulate("longitude", this.log);
                jsonObject.accumulate("posttruck_id", this.posttruckId);
                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/truck_tracking.php", "GET", newString);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
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
                Log.e("Map add lat log", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        // Toast.makeText(MapsTruckActivity.this, "success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MapsTruckActivity.this, "Updation fails!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MapsTruckActivity.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateTripStatus extends AsyncTask<Void, Void, InputStream> {
        private String status;

        public UpdateTripStatus(String trip_status) {
            this.status = trip_status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MapsTruckActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            try {
                String newString = String.format("load_id=%s&posttruck_id=%s&trip_status=%S", loadId, truckId, status);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/upload_trip_status.php", "GET", newString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            progressDialog.dismiss();
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
                Log.e("Update Trip Status : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        txt_stop_trip.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MapsTruckActivity.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null)
            dialog.dismiss();
    }
}
