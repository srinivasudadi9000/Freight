package com.waysideutilities.waysidetruckfreights.Profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.waysideutilities.waysidetruckfreights.Adapter.TruckBoardAdapter;
import com.waysideutilities.waysidetruckfreights.BaseActivity;
import com.waysideutilities.waysidetruckfreights.Owner.EditTruck;
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
public class MyTrucks extends BaseActivity {

    private Toolbar toolbar;
    private ListView listMyTrucks;
    private ProgressDialog progressDialog;
    private ArrayList<Truck> listTrucks;
    private AlertDialog dialog;
    private String language;

    public class GetMyTruck extends AsyncTask<Void, Void, InputStream> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MyTrucks.this);
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
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/my_posted_truck.php", "GET", newString);
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
                Log.e("GetMyTrucks : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        listTrucks = new ArrayList<Truck>();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        listTrucks = getValueFromJsonArrayForTruck(jsonArray);
                        listMyTrucks.setAdapter(new TruckBoardAdapter(MyTrucks.this, listTrucks));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MyTrucks.this, R.string.networkError, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_my_trucks);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.MyTruck));
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
        new GetMyTruck().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listMyTrucks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    final Truck truck = listTrucks.get(position);
                                                    final Bundle bundle = new Bundle();
                                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyTrucks.this);
                                                    if (truck.getBook_status().equals("1")) {
                                                        if (truck.trip_status.equals("2")) {
                                                            bundle.putString("TRUCKID", truck.getId());
                                                            bundle.putString("POST_TRUCKID", truck.getPost_truck_id());
                                                            bundle.putString("BOOK_STATUS", truck.getBook_status());
                                                            bundle.putString("TRIP_STATUS", truck.getTrip_status());
                                                            bundle.putString("DATE", truck.getDate());
                                                            bundle.putString("FROM", truck.getFrom());
                                                            bundle.putString("TO", truck.getTo());
                                                            bundle.putString("CATEGORY", truck.getLoad_Category());
                                                            bundle.putString("TRUCK_TYPE", truck.getType_of_truck());
                                                            bundle.putString("FTL_LTL", truck.getFtl_ltl());
                                                            bundle.putString("LOAD_CAPACITY", truck.getLoad_Capacity());
                                                            bundle.putString("CHARGE", truck.getCharges());
                                                            bundle.putString("DESCRIPTION", truck.getLoad_description());
                                                            Intent intent = new Intent(MyTrucks.this, EditTruck.class);
                                                            intent.putExtra("BUNDLE", bundle);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            alertBuilder.setMessage(getResources().getString(R.string.edit_mag));
                                                            alertBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                        }
                                                    } else {
                                                        alertBuilder.setTitle(getResources().getString(R.string.select_option));
                                                        alertBuilder.setPositiveButton(getResources().getString(R.string.edit), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                                bundle.putString("TRUCKID", truck.getId());
                                                                bundle.putString("DATE", truck.getDate());
                                                                bundle.putString("FROM", truck.getFrom());
                                                                bundle.putString("POST_TRUCKID", truck.getPost_truck_id());
                                                                bundle.putString("BOOK_STATUS", truck.getBook_status());
                                                                bundle.putString("TRIP_STATUS", truck.getTrip_status());
                                                                bundle.putString("TO", truck.getTo());
                                                                bundle.putString("CATEGORY", truck.getLoad_Category());
                                                                bundle.putString("TRUCK_TYPE", truck.getType_of_truck());
                                                                bundle.putString("FTL_LTL", truck.getFtl_ltl());
                                                                bundle.putString("LOAD_CAPACITY", truck.getLoad_Capacity());
                                                                bundle.putString("CHARGE", truck.getCharges());
                                                                bundle.putString("DESCRIPTION", truck.getLoad_description());
                                                                Intent intent = new Intent(MyTrucks.this, EditTruck.class);
                                                                intent.putExtra("BUNDLE", bundle);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                                        alertBuilder.setNegativeButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                                                                new DeleteMyTruck(MyTrucks.this, truck.getPost_truck_id()).execute();
                                                                finish();
                                                            }
                                                        });
                                                    }
                                                    dialog = alertBuilder.create();
                                                    dialog.show();
                                                }
                                            }
        );
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listMyTrucks = (ListView) findViewById(R.id.listMyTrucks);
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
