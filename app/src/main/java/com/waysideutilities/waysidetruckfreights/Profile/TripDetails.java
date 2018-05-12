package com.waysideutilities.waysidetruckfreights.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.MapsTruckActivity;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;
import com.waysideutilities.waysidetruckfreights.helper.HTTPhelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;

/**
 * Created by Archana on 8/29/2016.
 */
public class TripDetails extends AppCompatActivity {

    private String language, load_booked_by_id, stringFrom, stringTo, truck_id, load_id, post_truckId;
    private Toolbar toolbar;
    private Bundle bundle;
    private ProgressDialog progressDialog;
    private Button btnStartTrip;
    private TextView txtLoadId, txtDate, txtFrom, txtLoaderNumber, txtDriverName, txtTo, txtTruckRegNumber, txtLoadCategory, txtTruckType, txtWeight, txtLoadFlt, txtDescription,txtTruckDriverNo, txtLoadDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_trip_details);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.MyTrips_Details));
        bundle = getIntent().getBundleExtra("BUNDLE");
        if (!(bundle.getString("BOOK_BY_ID").equals("0"))) {
            load_booked_by_id = bundle.getString("BOOK_BY_ID");
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
            new GetMyTripDetails(load_booked_by_id).execute();
        } else {
            finish();
        }
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtLoadId = (TextView) findViewById(R.id.txtLoadId);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtDriverName = (TextView) findViewById(R.id.txtDriverName);
        txtLoaderNumber = (TextView) findViewById(R.id.txtLoaderNumber);
        txtFrom = (TextView) findViewById(R.id.txtFrom);
        txtTo = (TextView) findViewById(R.id.txtTo);
        txtTruckRegNumber = (TextView) findViewById(R.id.txtTruckRegNumber);
        txtLoadCategory = (TextView) findViewById(R.id.txtLoadCategory);
        txtTruckType = (TextView) findViewById(R.id.txtTruckType);
        txtWeight = (TextView) findViewById(R.id.txtWeight);
        txtLoadFlt = (TextView) findViewById(R.id.txtLoadFlt);
        txtTruckDriverNo = (TextView)findViewById(R.id.txtTruckDriverNo);
        txtDescription = (TextView)findViewById(R.id.txtDescription);
        //txtTotalCash = (TextView) findViewById(R.id.txtTotalCash);
        btnStartTrip = (Button) findViewById(R.id.btnStartTrip);

        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(btnStartTrip.getText().toString()).equals("Completed")) {
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                    new UpdateTripStatus("1").execute();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetMyTripDetails extends AsyncTask<Void, Void, InputStream> {
        private String load_id;

        public GetMyTripDetails(String load_booked_by_id) {
            this.load_id = load_booked_by_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(TripDetails.this);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            try {
                String newString = String.format("truck_booked_by_id=%s", this.load_id);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/trip_details.php", "GET", newString);
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
                Log.e("Order Details : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        getSetValueFromBundle(bundle, jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(TripDetails.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getSetValueFromBundle(Bundle bundle, JSONObject jsonObject) {
        if (bundle.getString("BOOK_STATUS") != null) {
            if (bundle.getString("BOOK_STATUS").equals("1")) {
                if (bundle.getString("TRIP_STATUS").equals("2")) {
                    btnStartTrip.setText(R.string.completed);
                } else if (bundle.getString("TRIP_STATUS").equals("0")) {
                    btnStartTrip.setText(R.string.start_trip);
                } else {
                    btnStartTrip.setText(R.string.running);
                }
            }
        }
        try {
            load_id = jsonObject.getString("loadid");
            txtLoaderNumber.setText("Cargo provider number : " + jsonObject.getString("mobile_no"));
            stringFrom = jsonObject.getString("from_street_name").concat(",").concat(jsonObject.getString("from_landmark")).concat(",").concat(jsonObject.getString("from_city")).concat(",").concat(jsonObject.getString("from_state")).concat(",").concat(jsonObject.getString("from_pincode"));
            stringTo = jsonObject.getString("to_street_name").concat(",").concat(jsonObject.getString("to_landmark")).concat(",").concat(jsonObject.getString("to_city")).concat(",").concat(jsonObject.getString("to_state")).concat(",").concat(jsonObject.getString("to_pincode"));
            txtFrom.setText("From : " + stringFrom);
            txtTo.setText("To :" + stringTo);
            txtTruckRegNumber.setText("Truck registration number : "+bundle.getString("TRUCK_REG_NUMBER"));
            //if(bundle.getString("TRUCK_NO") != null)
            //txtTruckNo.setText("Truck Number : "+bundle.getString("TRUCK_NO"));
            if(bundle.getString("DESCRIPTION")!=null )
            txtDescription.setText("Description : "+bundle.getString("DESCRIPTION"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (bundle.getString("TRUCKID") != null) {
            truck_id = bundle.getString("TRUCKID");

        }
        if (bundle.getString("POSTTRUCKID") != null) {
            post_truckId = bundle.getString("POSTTRUCKID");
            txtLoadId.setText("Id : " + bundle.getString("POSTTRUCKID"));
        }
        if (bundle.getString("DATE") != null)
            txtDate.setText("Date : " + FrightUtils.getFormattedDate("yyyy-MM-dd", "dd/MM/yyyy", bundle.getString("DATE"), null));

        txtTruckDriverNo.setText("Driver Number : " + bundle.getString("DRIVER_NO"));
        txtDriverName.setText("Driver Name : " + bundle.getString("DRIVER_NAME"));

        if (bundle.getString("CATEGORY") != null)
            txtLoadCategory.setText("Load category : " + bundle.getString("CATEGORY"));
        if (bundle.getString("TRUCK_TYPE") != null)
            txtTruckType.setText("Type of truck : " + bundle.getString("TRUCK_TYPE"));
        if (bundle.getString("WEIGHT") != null)
            txtWeight.setText("Capacity(in tons) : " + bundle.getString("WEIGHT"));
        if (bundle.getString("FTL_LTL") != null)
            txtLoadFlt.setText("FTL/LTL : " + bundle.getString("FTL_LTL"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
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
            progressDialog = new ProgressDialog(TripDetails.this);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            try {
                String newString = String.format("load_id=%s&posttruck_id=%s&trip_status=%S", load_id, post_truckId, status);
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
                        if (!(btnStartTrip.getText().toString()).equals("Completed")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("ORIGIN", stringFrom);
                            bundle.putString("DEST", stringTo);
                            bundle.putString("TRUCK_ID", post_truckId);
                            bundle.putString("LOAD_ID", load_id);
                            Intent intent = new Intent(TripDetails.this, MapsTruckActivity.class);
                            intent.putExtra("BUNDLE", bundle);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(TripDetails.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
