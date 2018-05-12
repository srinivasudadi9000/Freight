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

import com.waysideutilities.waysidetruckfreights.Adapter.OrderAdapter;
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
public class MyOrder extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView listMyOrders;
    private ProgressDialog progressDialog;
    private String language;
    ArrayList<Cargo> listOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_my_order);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.MyOrder));
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
        new GetMyOrders().execute();
        listMyOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cargo cargo = listOrders.get(position);
                Bundle bundle = new Bundle();
                Intent intent = new Intent(MyOrder.this, OrderDetails.class);
                bundle.putString("LOADID", cargo.getId());
                bundle.putString("DATE", cargo.getDate());
                bundle.putString("FROM", cargo.getFrom_street_name().concat(",").concat(cargo.getFrom_landmark()).concat(",").concat(cargo.getFrom_city()).concat(",").concat(cargo.getFrom_state()).concat(",").concat(cargo.getFrom_pincode()));
                bundle.putString("TO", cargo.getTo_street_name().concat(",").concat(cargo.getTo_landmark()).concat(",").concat(cargo.getTo_city()).concat(",").concat(cargo.getTo_state()).concat(",").concat(cargo.getTo_pincode()));
                bundle.putString("CONTACT_NO", cargo.getContactNumber());
                bundle.putString("FTL_LTL", cargo.getFtl_ltl());
                bundle.putString("WEIGHT", cargo.getWeight());
                bundle.putString("CATEGORY", cargo.getLoad_Category());
               // bundle.putString("TRUCK_TYPE", cargo.getType_of_truck());
                bundle.putString("DESCRIPTION", cargo.getLoad_description());
               // bundle.putString("BOOK_STATUS", cargo.getBook_status());
                bundle.putString("TRIP_STATUS", cargo.getTrip_status());
                bundle.putString("BOOK_BY_ID", cargo.getBooked_by_id());
                intent.putExtra("BUNDLE", bundle);
                startActivity(intent);

            }
        });

    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listMyOrders = (ListView) findViewById(R.id.listMyOrders);
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

    private class GetMyOrders extends AsyncTask<Void, Void, InputStream> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MyOrder.this);
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
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/myorders.php", "GET", newString);
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
                Log.e("GetMyOrders : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        listOrders = getValueFromJsonArray(jsonArray);
                        listMyOrders.setAdapter(new OrderAdapter(MyOrder.this, listOrders));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MyOrder.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ArrayList<Cargo> getValueFromJsonArray(JSONArray jsonArray) {
        Cargo oredr = null;
        ArrayList<Cargo> listOrder = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            oredr = new Cargo();
            try {
                if (!(jsonArray.getJSONObject(i).isNull("id")))
                    oredr.setId(jsonArray.getJSONObject(i).getString("id"));

                if (!(jsonArray.getJSONObject(i).isNull("to_street_name")))
                    oredr.setTo_street_name(jsonArray.getJSONObject(i).getString("to_street_name"));
                if (!(jsonArray.getJSONObject(i).isNull("to_landmark")))
                    oredr.setTo_landmark(jsonArray.getJSONObject(i).getString("to_landmark"));
                if (!(jsonArray.getJSONObject(i).isNull("to_city")))
                    oredr.setTo_city(jsonArray.getJSONObject(i).getString("to_city"));
                if (!(jsonArray.getJSONObject(i).isNull("to_state")))
                    oredr.setTo_state(jsonArray.getJSONObject(i).getString("to_state"));
                if (!(jsonArray.getJSONObject(i).isNull("to_pincode")))
                    oredr.setTo_pincode(jsonArray.getJSONObject(i).getString("to_pincode"));

                if (!(jsonArray.getJSONObject(i).isNull("from_street_name")))
                    oredr.setFrom_street_name(jsonArray.getJSONObject(i).getString("from_street_name"));
                if (!(jsonArray.getJSONObject(i).isNull("from_landmark")))
                    oredr.setFrom_landmark(jsonArray.getJSONObject(i).getString("from_landmark"));
                if (!(jsonArray.getJSONObject(i).isNull("from_city")))
                    oredr.setFrom_city(jsonArray.getJSONObject(i).getString("from_city"));
                if (!(jsonArray.getJSONObject(i).isNull("from_state")))
                    oredr.setFrom_state(jsonArray.getJSONObject(i).getString("from_state"));
                if (!(jsonArray.getJSONObject(i).isNull("from_pincode")))
                    oredr.setFrom_pincode(jsonArray.getJSONObject(i).getString("from_pincode"));

                if (!(jsonArray.getJSONObject(i).isNull("trip_status")))
                    oredr.setTrip_status(jsonArray.getJSONObject(i).getString("trip_status"));
                if (!(jsonArray.getJSONObject(i).isNull("load_booked_by_id")))
                    oredr.setBooked_by_id(jsonArray.getJSONObject(i).getString("load_booked_by_id"));

                if (!(jsonArray.getJSONObject(i).isNull("total_charges")))
                    oredr.setTotal_charges(jsonArray.getJSONObject(i).getString("total_charges"));
                if (!(jsonArray.getJSONObject(i).isNull("paid_charges")))
                    oredr.setPaid_amount(jsonArray.getJSONObject(i).getString("paid_charges"));
                if (!(jsonArray.getJSONObject(i).isNull("remaining_charges")))
                    oredr.setRemaining_amount(jsonArray.getJSONObject(i).getString("remaining_charges"));

                if (!(jsonArray.getJSONObject(i).isNull("date")))
                    oredr.setDate(jsonArray.getJSONObject(i).getString("date"));
                if (!(jsonArray.getJSONObject(i).isNull("trip_status")))
                    oredr.setTrip_status(jsonArray.getJSONObject(i).getString("trip_status"));

                if (!(jsonArray.getJSONObject(i).isNull("weight")))
                    oredr.setWeight(jsonArray.getJSONObject(i).getString("weight"));
                if (!(jsonArray.getJSONObject(i).isNull("load_category")))
                    oredr.setLoad_Category(jsonArray.getJSONObject(i).getString("load_category"));
                if (!(jsonArray.getJSONObject(i).isNull("load_type")))
                    oredr.setFtl_ltl(jsonArray.getJSONObject(i).getString("load_type"));
                if (!(jsonArray.getJSONObject(i).isNull("mobile_no")))
                    oredr.setContactNumber(jsonArray.getJSONObject(i).getString("mobile_no"));
                if (!(jsonArray.getJSONObject(i).isNull("load_description")))
                    oredr.setLoad_description(jsonArray.getJSONObject(i).getString("load_description"));

                listOrder.add(oredr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listOrder;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
