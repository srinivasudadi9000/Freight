package com.waysideutilities.waysidetruckfreights.Cargo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.Adapter.SelectTruckAdapter;
import com.waysideutilities.waysidetruckfreights.BaseActivity;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Request;
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
 * Created by Archana on 2/3/2017.
 */
public class SelectTruck extends BaseActivity {
    private String language, sender_number,load_id,  cargo_owner_id, number;
    private Toolbar toolbar;
    private ListView listMyUnBookedTrucks;
    private ProgressDialog progressDialog;
    ArrayList<Truck> listUnBookedTrucks;
    private AlertDialog dialog;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        sender_number = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("NUMBER", null);

        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_select_truck);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.my_trucks);
        bundle = getIntent().getBundleExtra("BUNDLE");
        if (bundle.getString("LOAD_ID") != null) {
            number = bundle.getString("NUMBER");
            load_id = bundle.getString("LOAD_ID");
            cargo_owner_id = bundle.getString("CARGO_OWNER_ID");
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
            new GetMyUnBookedTrucks().execute(load_id);
        }
        listMyUnBookedTrucks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Truck truck = listUnBookedTrucks.get(position);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SelectTruck.this);
                alertBuilder.setMessage(getResources().getString(R.string.request_message));
                alertBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Request request = new Request();
                        request.setReceiver_id(cargo_owner_id);
                        request.setLoad_id(load_id);
                        request.setSent_message("The post truck id "+truck.getPost_truck_id()+" has sent request to load id "+ load_id + " for date "+ truck.getDate() + " and route "+truck.getFrom() +" To "+truck.getTo());
                        request.setReceived_message("The post truck id "+truck.getPost_truck_id() +" has requested for your load with load id "+ load_id+ " for date "+ truck.getDate()+" and route " + truck.getFrom()+" To "+truck.getTo());
                        request.setPosttruck_id(truck.getPost_truck_id());
                        request.setRtruck_id(truck.getId());
                        request.setReceiver_number(number);
                        request.setSender_number(truck.getContactNumber());
                        request.setRequest_status("0");
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new Request_To_Cargo_Owner(SelectTruck.this, request).execute();
                    }
                });
                dialog = alertBuilder.create();
                dialog.show();
            }
        });
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listMyUnBookedTrucks = (ListView) findViewById(R.id.listMyUnBookedTrucks);
    }

    private class GetMyUnBookedTrucks extends AsyncTask<String, String, InputStream> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SelectTruck.this);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(String... load_id) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            try {
                String newString = String.format("userId=%s&loadid=%s", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null), load_id[0]);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/my_unbooked_post_truck.php", "GET", newString);
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
                Log.e("GetMyUnBookedTrucks : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        listUnBookedTrucks = getValueFromJsonArrayTrucks(jsonArray);
                        listMyUnBookedTrucks.setAdapter(new SelectTruckAdapter(SelectTruck.this, listUnBookedTrucks));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(SelectTruck.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected ArrayList<Truck> getValueFromJsonArrayTrucks(JSONArray jsonArray) {
        Truck truck = null;
        ArrayList<Truck> listTrucks = new ArrayList<Truck>();
        for (int i = 0; i < jsonArray.length(); i++) {
            truck = new Truck();
            try {
                if (!(jsonArray.getJSONObject(i).isNull("posttruck_id")))
                    truck.setPost_truck_id(jsonArray.getJSONObject(i).getString("posttruck_id"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_id")))
                    truck.setId(jsonArray.getJSONObject(i).getString("truck_id"));
                if (!(jsonArray.getJSONObject(i).isNull("userid")))
                    truck.setTruck_owner_id(jsonArray.getJSONObject(i).getString("userid"));
                if (!(jsonArray.getJSONObject(i).isNull("driver_name")))
                    truck.setDriverName(jsonArray.getJSONObject(i).getString("driver_name"));
                if (!(jsonArray.getJSONObject(i).isNull("driver_mobile_no")))
                    truck.setDriverNumber(jsonArray.getJSONObject(i).getString("driver_mobile_no"));
                if (!(jsonArray.getJSONObject(i).isNull("owner_mb")))//owner_mb
                    truck.setContactNumber(jsonArray.getJSONObject(i).getString("owner_mb"));
                /*if (!(jsonArray.getJSONObject(i).isNull("truck_no")))
                    truck.setTruckNumber(jsonArray.getJSONObject(i).getString("truck_no"));*/
                if (!(jsonArray.getJSONObject(i).isNull("truck_reg_no")))
                    truck.setTruckRegNumber(jsonArray.getJSONObject(i).getString("truck_reg_no"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_city")))
                    truck.setTruckCity(jsonArray.getJSONObject(i).getString("truck_city"));
                if (!(jsonArray.getJSONObject(i).isNull("load_passing")))
                    truck.setTruckLoadPassing(jsonArray.getJSONObject(i).getString("load_passing"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_image")))
                    truck.setTruckImage(jsonArray.getJSONObject(i).getString("truck_image"));
                if (!(jsonArray.getJSONObject(i).isNull("license_copy")))
                    truck.setDriverLicenceImage(jsonArray.getJSONObject(i).getString("license_copy"));
                if (!(jsonArray.getJSONObject(i).isNull("insurance_copy")))
                    truck.setTruckInsProviderImage(jsonArray.getJSONObject(i).getString("insurance_copy"));
                if (!(jsonArray.getJSONObject(i).isNull("vehicle_reg_copy")))
                    truck.setTruckRegistrationImage(jsonArray.getJSONObject(i).getString("vehicle_reg_copy"));

                if (!(jsonArray.getJSONObject(i).isNull("load_category")))
                    truck.setLoad_Category(jsonArray.getJSONObject(i).getString("load_category"));
                if (!(jsonArray.getJSONObject(i).isNull("load_capacity")))
                    truck.setLoad_Capacity(jsonArray.getJSONObject(i).getString("load_capacity"));
                if (!(jsonArray.getJSONObject(i).isNull("load_type")))
                    truck.setFtl_ltl(jsonArray.getJSONObject(i).getString("load_type"));
                if (!(jsonArray.getJSONObject(i).isNull("date")))
                    truck.setDate(jsonArray.getJSONObject(i).getString("date"));
                if (!(jsonArray.getJSONObject(i).isNull("to")))
                    truck.setTo(jsonArray.getJSONObject(i).getString("to"));
                if (!(jsonArray.getJSONObject(i).isNull("from")))
                    truck.setFrom(jsonArray.getJSONObject(i).getString("from"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_type")))
                    truck.setType_of_truck(jsonArray.getJSONObject(i).getString("truck_type"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_load")))
                    truck.setTruckLoadPassing(jsonArray.getJSONObject(i).getString("truck_load"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_discription")))
                    truck.setLoad_description(jsonArray.getJSONObject(i).getString("truck_discription"));
                if (!(jsonArray.getJSONObject(i).isNull("charges")))
                    truck.setCharges(jsonArray.getJSONObject(i).getString("charges"));
                if (!(jsonArray.getJSONObject(i).isNull("booked_status")))
                    truck.setBook_status(jsonArray.getJSONObject(i).getString("booked_status"));
                if (!(jsonArray.getJSONObject(i).isNull("trip_status")))
                    truck.setTrip_status(jsonArray.getJSONObject(i).getString("trip_status"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_booked_by_id")))
                    truck.setBooked_by_id(jsonArray.getJSONObject(i).getString("truck_booked_by_id"));

                listTrucks.add(truck);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listTrucks;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
