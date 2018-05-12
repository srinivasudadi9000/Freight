package com.waysideutilities.waysidetruckfreights.Owner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
public class CheckTruck extends AppCompatActivity {

    private Button btnAddNewTruck;
    private ListView truckList;
    private ProgressDialog progressDialog;
    private String language;

    public class GetPostedTruck extends AsyncTask<Void, Void, InputStream> {
        private Context context;

        public GetPostedTruck(CheckTruck context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(this.context);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            String newString = null;
            try {
                newString = String.format("userId=%s", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/mytruck.php", "GET", newString);
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
                Log.e("Check Trucks ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        final ArrayList<Truck> postedTruckList = getValueFromJsonArray(jsonArray);
                        truckList.setAdapter(new CheckTruckAdapter(CheckTruck.this, postedTruckList));
                        truckList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Truck truckDetails = postedTruckList.get(position);
                                Bundle bundle = new Bundle();
                                bundle.putString("ID", truckDetails.getId());
                               /* bundle.putString("DRIVER_NAME", truckDetails.getDriverName());
                                bundle.putString("DRIVER_MOB_NUMBER", truckDetails.getDriver_contact_number());
                                bundle.putString("TRUCK_NUMBER", truckDetails.getTruckNumber());
                                bundle.putString("TRUCK_REG_NUMBER", truckDetails.getTruckRegNumber());
                                bundle.putString("TRUCK_CITY", truckDetails.getTruckCity());
                                bundle.putString("LOAD_PASSING", truckDetails.getTruckLoadPassing());
                                bundle.putString("TRUCK_IMAGE", truckDetails.getTruckImage());
                                bundle.putString("INSURANCE_IAMGE", truckDetails.getTruckInsProviderImage());
                                bundle.putString("LICENCE_IMAGE", truckDetails.getDriverLicenceImage());
                                bundle.putString("REG_IMAGE", truckDetails.getTruckRegistrationImage());
                               */ Intent intent = new Intent(context, PostTruck.class);
                                intent.putExtra("BUNDLE", bundle);
                                startActivity(intent);
                            }
                        });
                    } else {
                        Toast.makeText(this.context, "No record found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected ArrayList<Truck> getValueFromJsonArray(JSONArray jsonArray) {
        Truck truck = null;
        ArrayList<Truck> listPostedTruck = new ArrayList<Truck>();
        for (int i = 0; i < jsonArray.length(); i++) {
            truck = new Truck();
            try {
                if (!(jsonArray.getJSONObject(i).isNull("truck_id")))
                    truck.setId(jsonArray.getJSONObject(i).getString("truck_id"));
                if (!(jsonArray.getJSONObject(i).isNull("driver_name")))
                    truck.setDriverName(jsonArray.getJSONObject(i).getString("driver_name"));
                if (!(jsonArray.getJSONObject(i).isNull("driver_mobile_no")))
                    truck.setDriver_contact_number(jsonArray.getJSONObject(i).getString("driver_mobile_no"));
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
                listPostedTruck.add(truck);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listPostedTruck;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_check_truck);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
        new GetPostedTruck(CheckTruck.this).execute();
    }

    private void init() {
        truckList = (ListView) findViewById(R.id.truckList);
        btnAddNewTruck = (Button) findViewById(R.id.btnAddNewTruck);
        btnAddNewTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CheckTruck.this, DriverDetails.class));
            }
        });
    }

    private class CheckTruckAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Truck> postedTruckList;

        public CheckTruckAdapter(CheckTruck checkTruck, ArrayList<Truck> postedTruckList) {
            this.context = checkTruck;
            this.postedTruckList = postedTruckList;
        }

        @Override
        public int getCount() {
            return postedTruckList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Activity activity = (Activity) context;
            final LayoutInflater inflater = activity.getLayoutInflater();
            ViewHolder holder = null;
            Truck truck = postedTruckList.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.text_layout, null);
                holder.txtTruckNumber = (TextView) convertView.findViewById(R.id.txtTruckNumber);
                holder.txtDriverName = (TextView) convertView.findViewById(R.id.txtDriverName);
                holder.txtTruckNumber.setTag(position);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
           // holder.txtTruckNumber.setText(truck.getTruckNumber());
            holder.txtTruckNumber.setText(truck.getTruckRegNumber());
            holder.txtDriverName.setText(truck.getDriverName());
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView txtTruckNumber,txtDriverName;
    }
}
