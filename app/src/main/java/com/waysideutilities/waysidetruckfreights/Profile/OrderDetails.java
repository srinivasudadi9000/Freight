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

import com.waysideutilities.waysidetruckfreights.MapsActivity;
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
public class OrderDetails extends AppCompatActivity {
    private String language, truckId, loadId;
    private Toolbar toolbar;
    private TextView txtLoadId,  txtDate, txtFrom, txtDriverNo, txtDriverName, txtTo, txtLoadCategory, txtTruckType, txtWeight, txtLoadFlt, txtRegTruckNo, txtLoadCapacityOfTruck, txtLoaderMobNo, txtRemainingCash, txtLoadDesc;
    private Button btnCancelOrder, btnTrackOrder, btnAddReview;
    private ProgressDialog progressDialog;
    private String load_booked_by_id;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_order_details);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.order_details);

        bundle = getIntent().getBundleExtra("BUNDLE");
        if (bundle.getString("BOOK_BY_ID") != null) {
            load_booked_by_id = bundle.getString("BOOK_BY_ID");
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
            new GetMyOrdersDetails(load_booked_by_id).execute();
        }
    }

    private void getSetValueFromBundle(Bundle bundle, JSONObject jsonObject) {
        try {
            if (bundle.getString("LOADID") != null)
                loadId = bundle.getString("LOADID");
            if (bundle.getString("DATE") != null)
                txtDate.setText("Trip Date : " + FrightUtils.getFormattedDate("yyyy-MM-dd", "dd/MM/yyyy", bundle.getString("DATE"), null));
            if (bundle.getString("FROM") != null)
                txtFrom.setText("From : " + bundle.getString("FROM"));
            if (bundle.getString("TO") != null)
                txtTo.setText("To : " + bundle.getString("TO"));

            truckId = jsonObject.getString("posttruck_id");
            txtLoadId.setText("Load Id : "+loadId);
           // txtLoadId.setText("Post Truck Id : " + truckId);
            txtRegTruckNo.setText("Truck Registration Number : " + jsonObject.getString("truck_reg_no"));
            txtDriverName.setText("Driver Name : " + jsonObject.getString("driver_name"));
            txtDriverNo.setText("Driver Mobile Number : " + jsonObject.getString("driver_mobile_no"));
            txtLoadCapacityOfTruck.setText("Load Capacity of Truck : " + jsonObject.getString("load_capacity"));
            txtTruckType.setText("Type of truck : " + jsonObject.getString("truck_type"));

            if (bundle.getString("WEIGHT") != null)
            txtWeight.setText("Cargo Weight(in tons) : " + bundle.getString("WEIGHT"));

            if (bundle.getString("CONTACT_NO") != null)
                txtLoaderMobNo.setText("Loader Mobile Number : " + bundle.getString("CONTACT_NO"));
            if (bundle.getString("CATEGORY") != null)
                txtLoadCategory.setText("Load category : " + bundle.getString("CATEGORY"));

            if (bundle.getString("FTL_LTL") != null)
                txtLoadFlt.setText("FTL/LTL : " + bundle.getString("FTL_LTL"));
            if (bundle.getString("DESCRIPTION") != null)
                txtLoadDesc.setText("Description : " + bundle.getString("DESCRIPTION"));
            if (bundle.getString("TRIP_STATUS").equals("2")) {
                btnCancelOrder.setVisibility(View.INVISIBLE);
                btnTrackOrder.setVisibility(View.INVISIBLE);
                btnAddReview.setVisibility(View.VISIBLE);
                btnAddReview.setText(R.string.add_review);
            } else {
                btnCancelOrder.setVisibility(View.VISIBLE);
                btnTrackOrder.setVisibility(View.VISIBLE);
                btnAddReview.setVisibility(View.INVISIBLE);
            }
           //String charges[] = jsonObject.getString("charges").split("#");
           // txtTotalCash.setText("Total Cash : Rs. " + charges[0] + " /- " + charges[1]);
        } catch (JSONException e) {
            e.printStackTrace();
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

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtLoadId = (TextView) findViewById(R.id.txtLoadId);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtDriverName = (TextView) findViewById(R.id.txtDriverName);
        txtDriverNo = (TextView) findViewById(R.id.txtDriverNo);
        txtFrom = (TextView) findViewById(R.id.txtFrom);
        txtTo = (TextView) findViewById(R.id.txtTo);
        txtLoadCategory = (TextView) findViewById(R.id.txtLoadCategory);
        txtTruckType = (TextView) findViewById(R.id.txtTruckType);
        txtWeight = (TextView) findViewById(R.id.txtWeight);
        txtLoadFlt = (TextView) findViewById(R.id.txtLoadFlt);
        txtLoadDesc = (TextView) findViewById(R.id.txtLoadDesc);
        txtLoaderMobNo = (TextView) findViewById(R.id.txtLoaderMobNo);
        txtLoadCapacityOfTruck = (TextView) findViewById(R.id.txtLoadCapacityOfTruck);
        txtRegTruckNo = (TextView) findViewById(R.id.txtRegTruckNo);
        //txtRemainingCash = (TextView) findViewById(R.id.txtRemainingCash);
        btnCancelOrder = (Button) findViewById(R.id.btnCancelOrder);
        btnTrackOrder = (Button) findViewById(R.id.btnTrackOrder);
        btnAddReview = (Button) findViewById(R.id.btnAddReview);
        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnTrackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("ORIGIN", txtFrom.getText().toString());
                bundle.putString("DEST", txtTo.getText().toString());
                bundle.putString("TRUCK_ID", truckId);
                bundle.putString("LOAD_ID", loadId);
                Intent intent = new Intent(OrderDetails.this, MapsActivity.class);
                intent.putExtra("BUNDLE", bundle);
                startActivity(intent);
            }
        });
        btnAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("TRUCK_ID", truckId);
                Intent intent = new Intent(OrderDetails.this, Comments.class);
                intent.putExtra("BUNDLE", bundle);
                startActivity(intent);
            }
        });
    }


    private class GetMyOrdersDetails extends AsyncTask<Void, Void, InputStream> {
        private String book_by_id;

        public GetMyOrdersDetails(String load_booked_by_id) {
            this.book_by_id = load_booked_by_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OrderDetails.this);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            try {
                String newString = String.format("load_booked_by_id=%s", this.book_by_id);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/order_details.php", "GET", newString);
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
                Toast.makeText(OrderDetails.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
