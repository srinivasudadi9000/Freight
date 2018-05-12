package com.waysideutilities.waysidetruckfreights.Profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.waysideutilities.waysidetruckfreights.Adapter.SentRequestsAdapter;
import com.waysideutilities.waysidetruckfreights.BaseActivity;
import com.waysideutilities.waysidetruckfreights.Cargo.Cargo_Provider;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Cargo;
import com.waysideutilities.waysidetruckfreights.PojoClasses.DirectionsJSONParser;
import com.waysideutilities.waysidetruckfreights.PojoClasses.DistanceTime;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Truck;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.Constants;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;
import com.waysideutilities.waysidetruckfreights.helper.HTTPhelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;

public class FinalPriceActivity extends BaseActivity {
    private Toolbar toolbar;
    private Button btnPayment;
    private TextView txt_Order_Id, txt_cost, txt_Total_distance, txt_final_cost, txt_Total_Amount, txt_Service_charges, txt_Total_trip_cost, txt_25, txt_50, txt_full;
    private double _cost, calculated_cost;
    private String _distance = null;
    private Bundle bundle;
    private ProgressDialog progressDialog;
    private Cargo _cargo_details;
    private Truck _truck_details;
    private DecimalFormat df, df_cost;
    private String payAmount, reciver_number, sender_number, loadId, post_truckId, truckId, reciverId, request_status;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_price);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.final_price));
        btnPayment = (Button) findViewById(R.id.btnPayment);
        df = new DecimalFormat("#,###,##0.00");
        df_cost = new DecimalFormat("######0.00");

        bundle = getIntent().getBundleExtra("BUNDLE");
        if (bundle != null) {
            reciver_number = bundle.getString("RECIVER_NUMBER");
            sender_number = bundle.getString("SENDER_NUMBER");
            loadId = bundle.getString("LOAD_ID");
            post_truckId = bundle.getString("POST_TRUCK_ID");
            truckId = bundle.getString("TRUCK_ID");
            reciverId = bundle.getString("RECIVER_ID");
            request_status = bundle.getString("REQUEST_STATUS");
        }
        if (loadId != null) {
            new GetLoadDetails(loadId).execute();
        }

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_final_cost.getText().toString().length() != 0) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(FinalPriceActivity.this);
                    alertBuilder.setMessage(getResources().getString(R.string.message));
                    alertBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                            new getChecksum(payAmount).execute();
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
                } else {
                    Toast.makeText(FinalPriceActivity.this, R.string.please_select_option, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txt_Order_Id = (TextView) findViewById(R.id.txt_Order_Id);
        //txt_cost = (TextView) findViewById(R.id.txt_cost);
        txt_Total_distance = (TextView) findViewById(R.id.txt_Total_distance);
        txt_Total_trip_cost = (TextView) findViewById(R.id.txt_Total_trip_cost);
        //txt_Service_charges = (TextView) findViewById(R.id.txt_Service_charges);
        txt_Total_Amount = (TextView) findViewById(R.id.txt_Total_Amount);

        txt_final_cost = (TextView) findViewById(R.id.txt_final_cost);
        txt_25 = (TextView) findViewById(R.id.txt_25);
        txt_25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_distance != null) {
                    double final_cost = calculated_cost - (calculated_cost * 0.75);
                    txt_final_cost.setText("Amount : " + df.format(final_cost));
                    payAmount = df_cost.format(final_cost);
                    // Toast.makeText(FinalPriceActivity.this, R.string.message, Toast.LENGTH_LONG).show();
                }
            }
        });
        txt_50 = (TextView) findViewById(R.id.txt_50);
        txt_50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_distance != null) {
                    double final_cost = calculated_cost - (calculated_cost * 0.50);
                    txt_final_cost.setText("Amount : " + df.format(final_cost));
                    payAmount = df_cost.format(final_cost);
                    //Toast.makeText(FinalPriceActivity.this,R.string.message, Toast.LENGTH_LONG).show();
                }
            }
        });
        txt_full = (TextView) findViewById(R.id.txt_full);
        txt_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_final_cost.setText("Amount : " + df.format(calculated_cost));
                payAmount = df_cost.format(calculated_cost);
            }
        });
    }

    private class DownloadTaskDistance extends AsyncTask<String, Void, String> {

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
                    Toast.makeText(FinalPriceActivity.this, R.string.add_correct_address, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    progressDialog.dismiss();
                    new ParserDistanceTask().execute(result);
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
    private class ParserDistanceTask extends AsyncTask<String, Integer, DistanceTime> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(FinalPriceActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.data_calculation));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        // Parsing the data in non-ui thread
        @Override
        protected DistanceTime doInBackground(String... jsonData) {
            JSONObject jObject;
            DistanceTime DistanceTimeObj = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                // Starts parsing data
                DistanceTimeObj = parser.parseDistance(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return DistanceTimeObj;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(DistanceTime distanceTime) {
            progressDialog.dismiss();
            if (distanceTime != null) {
                if (distanceTime.getDistance() != null) {
                    String charge = null;
                    double distance = Double.parseDouble(distanceTime.getDistance()) / 1000;
                    // double time = Double.parseDouble(distanceTime.getDuration()) / 60;
                    String charges[] = _truck_details.getCharges().split("#");
                    if (charges[1].equals("per km")) {
                        charge = charges[0];
                        _distance = String.valueOf(distance);
                        /*_cost = ((distance * Double.valueOf(charge)) + (Double.valueOf(charge) * 0.05));
                        calculated_cost = _cost;*/
                        _cost = ((distance * Double.valueOf(charge)));
                        calculated_cost = _cost + (_cost * 0.05);
                        //txt_Total_distance.setText("Total distance  : " + distance);
                        txt_Total_trip_cost.setText("Total distance  : " + distance);
                        //txt_Total_trip_cost.setText("Total cost of trip : " + df.format(distance * Double.valueOf(charge)));
                    }
                    txt_Order_Id.setText("Order id : " + _cargo_details.getId());
                    //txt_cost.setText("Cost " + charges[1] + " : " + charge);
                    //txt_Service_charges.setText("Our service charge : " + df.format(Double.valueOf(charge) * 0.05));
                    txt_Total_Amount.setText("Total Amount " + df.format(calculated_cost));
                }
            } else {
                Toast.makeText(FinalPriceActivity.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
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

    private class GetLoadDetails extends AsyncTask<Void, Void, InputStream> {
        private String _load_id;

        public GetLoadDetails(String load_id) {
            this._load_id = load_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(FinalPriceActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... voids) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            String newString = null;
            try {
                if (this._load_id != null) {
                    newString = String.format("loadid=%s", this._load_id);
                }
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/load_details.php", "GET", newString);
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
                Log.e("Load Details ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        _cargo_details = getTripValueFromJsonArray(jsonArray);

                        new GetTruckDetails(post_truckId).execute();
                    } else {
                        Toast.makeText(FinalPriceActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(FinalPriceActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private Cargo getTripValueFromJsonArray(JSONArray jsonArray) {
        Cargo cargo = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            cargo = new Cargo();
            try {
                if (!(jsonArray.getJSONObject(i).isNull("loadid")))
                    cargo.setId(jsonArray.getJSONObject(i).getString("loadid"));

                if (!(jsonArray.getJSONObject(i).isNull("date")))
                    cargo.setDate(jsonArray.getJSONObject(i).getString("date"));

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

                if (!(jsonArray.getJSONObject(i).isNull("load_category")))
                    cargo.setLoad_Category(jsonArray.getJSONObject(i).getString("load_category"));

                if (!(jsonArray.getJSONObject(i).isNull("load_type")))
                    cargo.setFtl_ltl(jsonArray.getJSONObject(i).getString("load_type"));

                if (!(jsonArray.getJSONObject(i).isNull("weight")))
                    cargo.setWeight(jsonArray.getJSONObject(i).getString("weight"));

                if (!(jsonArray.getJSONObject(i).isNull("mobile_no")))
                    cargo.setContactNumber(jsonArray.getJSONObject(i).getString("mobile_no"));

                if (!(jsonArray.getJSONObject(i).isNull("type_of_truck")))
                    cargo.setType_of_truck(jsonArray.getJSONObject(i).getString("type_of_truck"));

                if (!(jsonArray.getJSONObject(i).isNull("load_description")))
                    cargo.setLoad_description(jsonArray.getJSONObject(i).getString("load_description"));

                if (!(jsonArray.getJSONObject(i).isNull("booked_status")))
                    cargo.setBook_status(jsonArray.getJSONObject(i).getString("booked_status"));

                if (!(jsonArray.getJSONObject(i).isNull("trip_status")))
                    cargo.setTrip_status(jsonArray.getJSONObject(i).getString("trip_status"));

                if (!(jsonArray.getJSONObject(i).isNull("load_booked_by_id")))
                    cargo.setBooked_by_id(jsonArray.getJSONObject(i).getString("load_booked_by_id"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return cargo;
    }

    private class GetTruckDetails extends AsyncTask<Void, Void, InputStream> {
        private String _truck_id;

        public GetTruckDetails(String load_id) {
            this._truck_id = load_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(FinalPriceActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... voids) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            String newString = null;
            try {
                if (this._truck_id != null) {
                    newString = String.format("posttruck_id=%s", this._truck_id);
                }
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/post_truck_details.php", "GET", newString);
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
                Log.e("Truck Details ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        _truck_details = getTruckDetailsValueFromJsonArray(jsonArray);
                        String charge = null;
                        String charges[] = _truck_details.getCharges().split("#");
                        if (charges[1].equals("per km")) {
                            String from = _cargo_details.getFrom_street_name()
                                    .concat(",").concat(_cargo_details.getFrom_landmark())
                                    .concat(",").concat(_cargo_details.getFrom_city())
                                    .concat(",").concat(_cargo_details.getFrom_state())
                                    .concat(",").concat(_cargo_details.getFrom_pincode());

                            String to = _cargo_details.getTo_street_name()
                                    .concat(",").concat(_cargo_details.getTo_landmark())
                                    .concat(",").concat(_cargo_details.getTo_city())
                                    .concat(",").concat(_cargo_details.getTo_state())
                                    .concat(",").concat(_cargo_details.getTo_pincode());

                            txt_Total_distance.setText("From : " + from + "\n \nTo : " + to);
                            String url = FrightUtils.getDistanceUrl(from, to, FinalPriceActivity.this);
                            new DownloadTaskDistance().execute(url);
                        } else if (charges[1].equals("per ton")) {
                            progressDialog.dismiss();
                            charge = charges[0];
                            _distance = charges[0];
                           /* _cost = ((Double.valueOf(_cargo_details.getWeight()) * Double.valueOf(charge)) + (Double.valueOf(charge) * 0.05));
                            calculated_cost = _cost;*/
                            _cost = ((Double.valueOf(_cargo_details.getWeight()) * Double.valueOf(charge)));
                            calculated_cost = _cost + (_cost * 0.05);
                            txt_Total_distance.setText("From : " + _cargo_details.getFrom_street_name()
                                    .concat(",").concat(_cargo_details.getFrom_landmark())
                                    .concat(",").concat(_cargo_details.getFrom_city())
                                    .concat(",").concat(_cargo_details.getFrom_state())
                                    .concat(",").concat(_cargo_details.getFrom_pincode()) +
                                    "\n \nTo : " + _cargo_details.getTo_street_name()
                                    .concat(",").concat(_cargo_details.getTo_landmark())
                                    .concat(",").concat(_cargo_details.getTo_city())
                                    .concat(",").concat(_cargo_details.getFrom_state())
                                    .concat(",").concat(_cargo_details.getTo_state())
                                    .concat(",").concat(_cargo_details.getTo_pincode()));
                            //txt_Total_distance.setText("Total tons : " + _cargo_details.getWeight());
                            txt_Total_trip_cost.setText("Total tons : " + _cargo_details.getWeight());
                            //txt_Total_trip_cost.setText("Total cost of trip : " + df.format(Double.valueOf(_cargo_details.getWeight()) * Double.valueOf(charge)));
                            txt_Order_Id.setText("Order id : " + _cargo_details.getId());
                            //txt_cost.setText("Cost " + charges[1] + " : " + charge);
                            //txt_Service_charges.setText("Our service charge : " + df.format(Double.valueOf(charge) * 0.05));
                            txt_Total_Amount.setText("Total Amount " + df.format(calculated_cost));
                        } else {
                            progressDialog.dismiss();
                            charge = charges[0];
                            _distance = charges[0];
                           /* _cost = Double.valueOf(charge) + ((Double.valueOf(charge) * 0.05));
                            calculated_cost = _cost;*/
                            _cost = Double.valueOf(charge);
                            calculated_cost = _cost + (_cost * 0.05);
                            txt_Total_distance.setText("From : " + _cargo_details.getFrom_street_name()
                                    .concat(",").concat(_cargo_details.getFrom_landmark())
                                    .concat(",").concat(_cargo_details.getFrom_city())
                                    .concat(",").concat(_cargo_details.getFrom_state())
                                    .concat(",").concat(_cargo_details.getFrom_pincode()) +
                                    "\n \nTo : " + _cargo_details.getTo_street_name()
                                    .concat(",").concat(_cargo_details.getTo_landmark())
                                    .concat(",").concat(_cargo_details.getTo_city())
                                    .concat(",").concat(_cargo_details.getFrom_state())
                                    .concat(",").concat(_cargo_details.getTo_state())
                                    .concat(",").concat(_cargo_details.getTo_pincode())
                            );
                            //txt_Total_trip_cost.setText("Total cost of trip : " + charge);
                            txt_Total_trip_cost.setVisibility(View.GONE);
                            txt_Order_Id.setText("Order id : " + _cargo_details.getId());
                            // txt_cost.setText("Cost " + charges[1] + " : " + charge);
                            //txt_Service_charges.setText("Our service charge : " + df.format(Double.valueOf(charge) * 0.05));
                            txt_Total_Amount.setText("Total Amount " + df.format(calculated_cost));
                        }

                    } else {
                        Toast.makeText(FinalPriceActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                finish();
                Toast.makeText(FinalPriceActivity.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Truck getTruckDetailsValueFromJsonArray(JSONArray jsonArray) {
        Truck truck = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            truck = new Truck();
            try {
                if (!(jsonArray.getJSONObject(i).isNull("truck_id")))
                    truck.setId(jsonArray.getJSONObject(i).getString("truck_id"));

                if (!(jsonArray.getJSONObject(i).isNull("posttruck_id")))
                    truck.setPost_truck_id(jsonArray.getJSONObject(i).getString("posttruck_id"));

                if (!(jsonArray.getJSONObject(i).isNull("load_capacity")))
                    truck.setLoad_Capacity(jsonArray.getJSONObject(i).getString("load_capacity"));

                if (!(jsonArray.getJSONObject(i).isNull("charges")))
                    truck.setCharges(jsonArray.getJSONObject(i).getString("charges"));

                if (!(jsonArray.getJSONObject(i).isNull("driver_name")))
                    truck.setDriver_Name(jsonArray.getJSONObject(i).getString("driver_name"));

                if (!(jsonArray.getJSONObject(i).isNull("driver_mobile_no")))
                    truck.setDriver_contact_number(jsonArray.getJSONObject(i).getString("driver_mobile_no"));

                if (!(jsonArray.getJSONObject(i).isNull("truck_city")))
                    truck.setTruckCity(jsonArray.getJSONObject(i).getString("truck_city"));

                if (!(jsonArray.getJSONObject(i).isNull("load_passing")))
                    truck.setTruckLoadPassing(jsonArray.getJSONObject(i).getString("load_passing"));

                if (!(jsonArray.getJSONObject(i).isNull("load_category")))
                    truck.setLoad_Category(jsonArray.getJSONObject(i).getString("load_category"));

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

                if (!(jsonArray.getJSONObject(i).isNull("truck_discription")))
                    truck.setLoad_description(jsonArray.getJSONObject(i).getString("truck_discription"));

                if (!(jsonArray.getJSONObject(i).isNull("booked_status")))
                    truck.setBook_status(jsonArray.getJSONObject(i).getString("booked_status"));

                if (!(jsonArray.getJSONObject(i).isNull("trip_status")))
                    truck.setTrip_status(jsonArray.getJSONObject(i).getString("trip_status"));

                if (!(jsonArray.getJSONObject(i).isNull("truck_reg_no")))
                    truck.setTruckRegNumber(jsonArray.getJSONObject(i).getString("truck_reg_no"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return truck;
    }

    private class getChecksum extends AsyncTask<Void, Void, String> {
        private String final_amount;
        private String orderId;

        public getChecksum(String amount) {
            this.final_amount = amount;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
            Date now = new Date();
            this.orderId = "WAYSIDEORDERID" + formatter.format(now) + getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USER_NUMBER", null) + getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(FinalPriceActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            String inputStream = null;
            inputStream = helper.generateCheckSumPostRequest(final_amount,
                    getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USER_NUMBER", null), this.orderId,
                    getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("EMAIL", null));
            return inputStream;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            progressDialog.dismiss();
            JSONObject jsonObject = null;
            try {
                if (response != null) {
                    jsonObject = new JSONObject(response);
                    String checkSum = jsonObject.getString("CHECKSUMHASH");
                    String orderId = jsonObject.getString("ORDER_ID");
                    if (checkSum != null) {
                        onStartTransaction(checkSum, orderId, final_amount);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void onStartTransaction(final String checkSum, final String orderid, String amount) {
        PaytmPGService Service = null;
        //for testing environment
        //Service = PaytmPGService.getStagingService();
        //for production environment
        Service = PaytmPGService.getProductionService();
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID", Constants.MID_VALUE);
        paramMap.put("ORDER_ID", orderid);
        paramMap.put("CUST_ID", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USER_NUMBER", Constants.CUST_ID_VALUE));
        paramMap.put("INDUSTRY_TYPE_ID", Constants.INDUSTRY_TYPE_ID_VALUE);
        paramMap.put("CHANNEL_ID", Constants.CHANNEL_ID_VALUE);
        paramMap.put("TXN_AMOUNT", amount);
        paramMap.put("WEBSITE", Constants.WEBSITE_VALUE);
        paramMap.put("CALLBACK_URL", "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");
        paramMap.put("EMAIL", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("EMAIL", null));
        paramMap.put("MOBILE_NO", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USER_NUMBER", null));
        paramMap.put("CHECKSUMHASH", checkSum);

        PaytmOrder order = new PaytmOrder(paramMap);
        Service.initialize(order, null);
        Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {

            @Override
            public void onTransactionResponse(Bundle bundle) {
                if (bundle.getString("STATUS").equals("TXN_SUCCESS")) {
                    String orderId = bundle.getString("ORDERID");
                    String paid_amount = bundle.getString("TXNAMOUNT");
                    new CheckTransactionStatus(orderId, paid_amount).execute();
                } else {
                    finishAffinity();
                    startActivity(new Intent(FinalPriceActivity.this, Profile.class));
                    Toast.makeText(getBaseContext(), bundle.getString("RESPMSG"), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void networkNotAvailable() {
                finishAffinity();
                startActivity(new Intent(FinalPriceActivity.this, Profile.class));
                Toast.makeText(getBaseContext(), "networkNotAvailable", Toast.LENGTH_LONG).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                finishAffinity();
                startActivity(new Intent(FinalPriceActivity.this, Cargo_Provider.class));
            }

            @Override
            public void someUIErrorOccurred(String s) {
                Log.e("someUIErrorOccurred", s);
                Toast.makeText(getBaseContext(), "someUIErrorOccurred", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {

            }

            @Override
            public void onBackPressedCancelTransaction() {
                startActivity(new Intent(FinalPriceActivity.this, Cargo_Provider.class));
                finishAffinity();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                finishAffinity();
                startActivity(new Intent(FinalPriceActivity.this, Cargo_Provider.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (dialog != null)
                dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CheckTransactionStatus extends AsyncTask<Void, Void, InputStream> {
        private String orderId, paid_amounttoPaytm, newString;

        public CheckTransactionStatus(String orderid, String paid_amount) {
            this.orderId = orderid;
            this.paid_amounttoPaytm = paid_amount;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(FinalPriceActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            try {
                newString = String.format("ORDER_ID=%s", this.orderId);
                inputStream = helper.makeHttpRequest(Constants.TRANSACTION_STATUS_URL, "GET", newString);
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
                Log.e("Response : ", stringResult);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(stringResult);
                    String status = jsonObject.getString("STATUS");
                    if (status != null && status != "") {
                        if (status.equals("TXN_SUCCESS")) {
                            Intent intent = new Intent(FinalPriceActivity.this, PaymentSuccessActivity.class);
                            intent.putExtra("ORDERID", orderId);
                            intent.putExtra("AMOUNT", this.paid_amounttoPaytm);
                            intent.putExtra("TOTAL_AMOUNT", df_cost.format(calculated_cost));
                            intent.putExtra("RECEIVER", reciver_number);
                            intent.putExtra("DRIVER_NAME", _truck_details.getDriver_Name());
                            intent.putExtra("TRUCK_REG_NO", _truck_details.getTruckRegNumber());
                            intent.putExtra("SENDER", sender_number);
                            intent.putExtra("LOAD_ID", loadId);
                            intent.putExtra("POST_TRUCK_ID", post_truckId);
                            intent.putExtra("TRUCK_ID", truckId);
                            intent.putExtra("RECIVER_ID", reciverId);
                            intent.putExtra("REQUEST_STATUS", request_status);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(FinalPriceActivity.this, "Transaction failed !Please try again", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(FinalPriceActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
