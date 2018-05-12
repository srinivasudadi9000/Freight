package com.waysideutilities.waysidetruckfreights.Owner;

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

import com.waysideutilities.waysidetruckfreights.Adapter.SelectLoadAdapter;
import com.waysideutilities.waysidetruckfreights.BaseActivity;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Cargo;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Request;
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
public class SelectLoad extends BaseActivity {
    private String language,sender_number,truck_id, post_truck_id,truck_owner_id,number;
    private Toolbar toolbar;
    private ListView listMyUnBookedLoads;
    private ProgressDialog progressDialog;
    ArrayList<Cargo> listUnBookedCargo;
    private AlertDialog dialog;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        sender_number = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("NUMBER", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_select_load);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.my_loads);
        bundle = getIntent().getBundleExtra("BUNDLE");
        if (bundle.getString("POST_TRUCK_ID") != null) {
            post_truck_id = bundle.getString("POST_TRUCK_ID");
            number = bundle.getString("NUMBER");
            truck_id = bundle.getString("TRUCK_ID");
            truck_owner_id = bundle.getString("TRUCK_OWNER_ID");
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
            new GetMyUnBookedLoads().execute(post_truck_id);
        }
        listMyUnBookedLoads.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Cargo cargo = listUnBookedCargo.get(position);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SelectLoad.this);
                alertBuilder.setMessage(getResources().getString(R.string.request_message_load));
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
                        request.setReceiver_id(truck_owner_id);
                        request.setSent_message("The load id " +cargo.getId()+" has sent request to post truck id "+ post_truck_id +" for date "+ cargo.getDate()+"  and route " + (cargo.getFrom_street_name().concat(",").concat(cargo.getFrom_landmark()).concat(",").concat(cargo.getFrom_city()).concat(",").concat(cargo.getTo_state()).concat(",").concat(cargo.getFrom_pincode())) +" to "+(cargo.getTo_street_name().concat(",").concat(cargo.getTo_landmark()).concat(",").concat(cargo.getTo_city()).concat(",").concat(cargo.getTo_state()).concat(",").concat(cargo.getTo_pincode())));
                        request.setReceived_message("The load id " + cargo.getId()+" has requested for your truck with post truck id "  + post_truck_id +" for date "+cargo.getDate()+" and route "+(cargo.getFrom_street_name().concat(",").concat(cargo.getFrom_landmark()).concat(",").concat(cargo.getFrom_city()).concat(",").concat(cargo.getTo_state()).concat(",").concat(cargo.getFrom_pincode()))+" to "+(cargo.getTo_street_name().concat(",").concat(cargo.getTo_landmark()).concat(",").concat(cargo.getTo_city()).concat(",").concat(cargo.getTo_state()).concat(",").concat(cargo.getTo_pincode())));
                        request.setLoad_id(cargo.getId());
                        request.setPosttruck_id(post_truck_id);
                        request.setRtruck_id(truck_id);
                        request.setReceiver_number(number);
                        request.setSender_number(cargo.getContactNumber());
                        request.setRequest_status("0");
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new Request_To_Truck_Owner(SelectLoad.this, request).execute();
                    }
                });
                dialog = alertBuilder.create();
                dialog.show();
            }
        });
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listMyUnBookedLoads = (ListView) findViewById(R.id.listMyUnBookedLoads);
    }

    private class GetMyUnBookedLoads extends AsyncTask<String, String, InputStream> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SelectLoad.this);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(String... postTruck_id) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            try {
                String newString = String.format("userId=%s&posttruck_id=%s", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null), postTruck_id[0]);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/my_unbooked_load.php", "GET", newString);
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
                Log.e("GetMyUnBookedLoads : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        listUnBookedCargo = getValueFromJsonArray(jsonArray);
                        listMyUnBookedLoads.setAdapter(new SelectLoadAdapter(SelectLoad.this, listUnBookedCargo));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(SelectLoad.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected ArrayList<Cargo> getValueFromJsonArray(JSONArray jsonArray) {
        Cargo cargo = null;
        ArrayList<Cargo> listCargo = new ArrayList<Cargo>();
        for (int i = 0; i < jsonArray.length(); i++) {
            cargo = new Cargo();
            try {
                if (!(jsonArray.getJSONObject(i).isNull("loadid"))) ;
                cargo.setId(jsonArray.getJSONObject(i).getString("loadid"));
                if (!(jsonArray.getJSONObject(i).isNull("date"))) ;
                cargo.setDate(jsonArray.getJSONObject(i).getString("date"));
                //if (!(jsonArray.getJSONObject(i).isNull("from"))) ;
                //cargo.setFrom(jsonArray.getJSONObject(i).getString("from"));
                //if (!(jsonArray.getJSONObject(i).isNull("to"))) ;
                //cargo.setTo(jsonArray.getJSONObject(i).getString("to"));
                //if (!(jsonArray.getJSONObject(i).isNull("mobile_no"))) ;


                if (!(jsonArray.getJSONObject(i).isNull("from_street_name")))
                    cargo.setFrom_street_name(jsonArray.getJSONObject(i).getString("from_street_name"));
                if (!(jsonArray.getJSONObject(i).isNull("from_landmark")))
                    cargo.setFrom_landmark(jsonArray.getJSONObject(i).getString("from_landmark"));
                if (!(jsonArray.getJSONObject(i).isNull("from_city")))
                    cargo.setFrom_city(jsonArray.getJSONObject(i).getString("from_city"));
                if (!(jsonArray.getJSONObject(i).isNull("from_state")))
                    cargo.setFrom_state(jsonArray.getJSONObject(i).getString("from_state"));
                if (!(jsonArray.getJSONObject(i).isNull("from_pincode")))
                    cargo.setFrom_pincode(jsonArray.getJSONObject(i).getString("from_pincode"));


                if (!(jsonArray.getJSONObject(i).isNull("to_street_name")))
                    cargo.setTo_street_name(jsonArray.getJSONObject(i).getString("to_street_name"));
                if (!(jsonArray.getJSONObject(i).isNull("to_landmark")))
                    cargo.setTo_landmark(jsonArray.getJSONObject(i).getString("to_landmark"));
                if (!(jsonArray.getJSONObject(i).isNull("to_city")))
                    cargo.setTo_city(jsonArray.getJSONObject(i).getString("to_city"));
                if (!(jsonArray.getJSONObject(i).isNull("to_state")))
                    cargo.setTo_state(jsonArray.getJSONObject(i).getString("to_state"));
                if (!(jsonArray.getJSONObject(i).isNull("to_pincode")))
                    cargo.setTo_pincode(jsonArray.getJSONObject(i).getString("to_pincode"));

                cargo.setContactNumber(jsonArray.getJSONObject(i).getString("mobile_no"));

                if (!(jsonArray.getJSONObject(i).isNull("load_category"))) ;
                cargo.setLoad_Category(jsonArray.getJSONObject(i).getString("load_category"));
                if (!(jsonArray.getJSONObject(i).isNull("load_type"))) ;
                cargo.setFtl_ltl(jsonArray.getJSONObject(i).getString("load_type"));
                if (!(jsonArray.getJSONObject(i).isNull("weight"))) ;
                cargo.setWeight(jsonArray.getJSONObject(i).getString("weight"));
                if (!(jsonArray.getJSONObject(i).isNull("type_of_truck"))) ;
                cargo.setType_of_truck(jsonArray.getJSONObject(i).getString("type_of_truck"));
                if (!(jsonArray.getJSONObject(i).isNull("load_description"))) ;
                cargo.setLoad_description(jsonArray.getJSONObject(i).getString("load_description"));
                if (!(jsonArray.getJSONObject(i).isNull("booked_status"))) ;
                cargo.setBook_status(jsonArray.getJSONObject(i).getString("booked_status"));
                if (!(jsonArray.getJSONObject(i).isNull("trip_status"))) ;
                cargo.setTrip_status(jsonArray.getJSONObject(i).getString("trip_status"));
                if (!(jsonArray.getJSONObject(i).isNull("load_booked_by_id"))) ;
                cargo.setBooked_by_id(jsonArray.getJSONObject(i).getString("load_booked_by_id"));
                listCargo.add(cargo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listCargo;
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
