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

import com.waysideutilities.waysidetruckfreights.Adapter.LoadBoardAdapter;
import com.waysideutilities.waysidetruckfreights.BaseActivity;
import com.waysideutilities.waysidetruckfreights.Cargo.EditLoad;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Cargo;
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
public class MyLoads extends BaseActivity {

    private Toolbar toolbar;
    private ListView listMyLoads;
    private ProgressDialog progressDialog;
    ArrayList<Cargo> listCargo;
    private AlertDialog dialog;
    private String language;

    public class GetMyLoads extends AsyncTask<Void, Void, InputStream> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MyLoads.this);
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
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/myload.php", "GET", newString);
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
                Log.e("GetMyLoads : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        listCargo = getValueFromJsonArray(jsonArray);
                        listMyLoads.setAdapter(new LoadBoardAdapter(MyLoads.this, listCargo));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MyLoads.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_my_loads);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.my_loads);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
        new GetMyLoads().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listMyLoads.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Cargo cargo = listCargo.get(position);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyLoads.this);
                if (cargo.getBook_status().equals("1")) {
                    alertBuilder.setMessage(getResources().getString(R.string.edit_mag_order));
                    alertBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    alertBuilder.setTitle(R.string.select_option);
                    alertBuilder.setPositiveButton(getResources().getString(R.string.edit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Bundle bundle = new Bundle();
                            bundle.putString("LOADID", cargo.getId());
                            bundle.putString("DATE", cargo.getDate());

                            bundle.putString("FROM", cargo.getFrom_street_name());
                            bundle.putString("FROM_LANDMARK", cargo.getFrom_landmark());
                            bundle.putString("FROM_CITY", cargo.getFrom_city());
                            bundle.putString("FROM_STATE", cargo.getFrom_state());
                            bundle.putString("FROM_PIN", cargo.getFrom_pincode());

                            bundle.putString("TO", cargo.getTo_street_name());
                            bundle.putString("TO_LANDMARK", cargo.getTo_landmark());
                            bundle.putString("TO_CITY", cargo.getTo_city());
                            bundle.putString("TO_STATE", cargo.getTo_state());
                            bundle.putString("TO_PIN", cargo.getTo_pincode());

                            bundle.putString("CONTACT_NO", cargo.getContactNumber());
                            bundle.putString("FTL_LTL", cargo.getFtl_ltl());
                            bundle.putString("WEIGHT", cargo.getWeight());
                            bundle.putString("CATEGORY", cargo.getLoad_Category());
                            bundle.putString("TRUCK_TYPE", cargo.getType_of_truck());
                            bundle.putString("DESCRIPTION", cargo.getLoad_description());
                            Intent intent = new Intent(MyLoads.this, EditLoad.class);
                            intent.putExtra("BUNDLE", bundle);
                            startActivity(intent);
                            finish();
                        }
                    });
                    alertBuilder.setNegativeButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                            new DeleteMyLoads(MyLoads.this, cargo.getId()).execute();
                            finish();
                        }
                    });
                }

                dialog = alertBuilder.create();
                dialog.show();
            }
        });
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listMyLoads = (ListView) findViewById(R.id.listMyLoads);
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
