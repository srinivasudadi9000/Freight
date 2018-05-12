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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.Adapter.TripAdapter;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Truck;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;
import com.waysideutilities.waysidetruckfreights.helper.HTTPhelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;
/**
 * Created by Archana on 8/29/2016.
 */
public class MyTrips extends AppCompatActivity {

    private String language;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private ListView listMyTrips;
    private ArrayList<Truck> listTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_my_trips);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.MyTrips));
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
        new GetMyTrips().execute();
        listMyTrips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Truck truck = listTrip.get(position);
                Bundle bundle = new Bundle();
                Intent intent = new Intent(MyTrips.this, TripDetails.class);
                bundle.putString("TRUCKID", truck.getId());
                bundle.putString("POSTTRUCKID", truck.getPost_truck_id());
                bundle.putString("DATE", truck.getDate());
               // bundle.putString("FROM", truck.getFrom());
                //bundle.putString("TO", truck.getTo());
                bundle.putString("DRIVER_NO", truck.getDriver_contact_number());
                bundle.putString("TRUCK_REG_NUMBER", truck.getTruckRegNumber());
               // bundle.putString("TRUCK_NO", truck.getTruckNumber());
                bundle.putString("DRIVER_NAME", truck.getDriverName());
                bundle.putString("FTL_LTL", truck.getFtl_ltl());
                bundle.putString("CATEGORY", truck.getLoad_Category());
                bundle.putString("TRUCK_TYPE", truck.getType_of_truck());
                bundle.putString("DESCRIPTION", truck.getLoad_description());
                bundle.putString("BOOK_STATUS", truck.getBook_status());
                bundle.putString("TRIP_STATUS", truck.getTrip_status());
                bundle.putString("BOOK_BY_ID", truck.getBooked_by_id());
                bundle.putString("WEIGHT",truck.getTruckLoadPassing());
                intent.putExtra("BUNDLE", bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listMyTrips = (ListView) findViewById(R.id.listMyTrips);
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

    private class GetMyTrips extends AsyncTask<Void, Void, InputStream> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MyTrips.this);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            try {
                String newString = String.format("userId=%s", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/mytrips.php", "GET", newString);
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
                Log.e("My Trips : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        listTrip = getTripValueFromJsonArray(jsonArray);
                        listMyTrips.setAdapter(new TripAdapter(MyTrips.this, listTrip));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MyTrips.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ArrayList<Truck> getTripValueFromJsonArray(JSONArray jsonArray) {
        Truck truck = null;
        ArrayList<Truck> listTrips = new ArrayList<Truck>();
        for (int i = 0; i < jsonArray.length(); i++) {
            truck = new Truck();
            try {
                if (!(jsonArray.getJSONObject(i).isNull("posttruck_id")))
                    truck.setPost_truck_id(jsonArray.getJSONObject(i).getString("posttruck_id"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_id")))
                    truck.setId(jsonArray.getJSONObject(i).getString("truck_id"));
                if (!(jsonArray.getJSONObject(i).isNull("load_category")))
                    truck.setLoad_Category(jsonArray.getJSONObject(i).getString("load_category"));
                if (!(jsonArray.getJSONObject(i).isNull("load_type")))
                    truck.setFtl_ltl(jsonArray.getJSONObject(i).getString("load_type"));
                if (!(jsonArray.getJSONObject(i).isNull("load_capacity")))
                    truck.setLoad_Capacity(jsonArray.getJSONObject(i).getString("load_capacity"));
                if (!(jsonArray.getJSONObject(i).isNull("date")))
                    truck.setDate(jsonArray.getJSONObject(i).getString("date"));
                if (!(jsonArray.getJSONObject(i).isNull("to")))
                    truck.setTo(jsonArray.getJSONObject(i).getString("to"));
                if (!(jsonArray.getJSONObject(i).isNull("from")))
                    truck.setFrom(jsonArray.getJSONObject(i).getString("from"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_type")))
                    truck.setType_of_truck(jsonArray.getJSONObject(i).getString("truck_type"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_discription")))
                    truck.setLoad_description(jsonArray.getJSONObject(i).getString("truck_discription"));
                if (!(jsonArray.getJSONObject(i).isNull("booked_status")))
                    truck.setBook_status(jsonArray.getJSONObject(i).getString("booked_status"));
                if (!(jsonArray.getJSONObject(i).isNull("trip_status")))
                    truck.setTrip_status(jsonArray.getJSONObject(i).getString("trip_status"));
                if (!(jsonArray.getJSONObject(i).isNull("charges")))
                    truck.setCharges(jsonArray.getJSONObject(i).getString("charges"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_booked_by_id")))
                    truck.setBooked_by_id(jsonArray.getJSONObject(i).getString("truck_booked_by_id"));
                if (!(jsonArray.getJSONObject(i).isNull("driver_name")))
                    truck.setDriverName(jsonArray.getJSONObject(i).getString("driver_name"));
                if (!(jsonArray.getJSONObject(i).isNull("driver_mobile_no")))
                    truck.setDriver_contact_number(jsonArray.getJSONObject(i).getString("driver_mobile_no"));
                /*if (!(jsonArray.getJSONObject(i).isNull("truck_no")))
                    truck.setTruckNumber(jsonArray.getJSONObject(i).getString("truck_no"));*/
                if (!(jsonArray.getJSONObject(i).isNull("truck_reg_no")))
                    truck.setTruckRegNumber(jsonArray.getJSONObject(i).getString("truck_reg_no"));
                if (!(jsonArray.getJSONObject(i).isNull("load_passing")))
                    truck.setTruckLoadPassing(jsonArray.getJSONObject(i).getString("load_passing"));


                if (!(jsonArray.getJSONObject(i).isNull("truck_city")))
                    truck.setTruckCity(jsonArray.getJSONObject(i).getString("truck_city"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_image")))
                    truck.setTruckImage(jsonArray.getJSONObject(i).getString("truck_image"));
                if (!(jsonArray.getJSONObject(i).isNull("license_copy")))
                    truck.setDriverLicenceImage(jsonArray.getJSONObject(i).getString("license_copy"));
                if (!(jsonArray.getJSONObject(i).isNull("insurance_copy")))
                    truck.setTruckInsProviderImage(jsonArray.getJSONObject(i).getString("insurance_copy"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_reg_copy")))
                    truck.setTruckRegistrationImage(jsonArray.getJSONObject(i).getString("truck_reg_copy"));
                listTrips.add(truck);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listTrips;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
