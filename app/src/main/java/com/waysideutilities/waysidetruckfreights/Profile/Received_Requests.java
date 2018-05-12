package com.waysideutilities.waysidetruckfreights.Profile;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.Adapter.ReceivedRequestsAdapter;
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
public class Received_Requests extends AppCompatActivity {
    private String language;
    private Toolbar toolbar;
    private ListView listReceivedRequest;
    private ProgressDialog progressDialog;
    private ArrayList<Request> listRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_received__requests);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.received_request));
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
        new RecievedRequests().execute();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listReceivedRequest = (ListView) findViewById(R.id.listReceivedRequest);
    }

    private class RecievedRequests extends AsyncTask<Void, Void, InputStream> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Received_Requests.this);
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
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/received_request.php", "GET", newString);
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
                Log.e("Received Requests : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        listRequests = getRequestsValueFromJsonArray(jsonArray);
                        listReceivedRequest.setAdapter(new ReceivedRequestsAdapter(Received_Requests.this,listRequests));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                finish();
                Toast.makeText(Received_Requests.this, R.string.networkError, Toast.LENGTH_SHORT).show();
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
    protected ArrayList<Request> getRequestsValueFromJsonArray(JSONArray jsonArray) {
        Request request = null;
        ArrayList<Request> listRequests = new ArrayList<Request>();
        for (int i = 0; i < jsonArray.length(); i++) {
            request = new Request();
            try {
                if (!(jsonArray.getJSONObject(i).isNull("receiver_id")))
                    request.setReceiver_id(jsonArray.getJSONObject(i).getString("receiver_id"));
                if (!(jsonArray.getJSONObject(i).isNull("received_message")))
                    request.setReceived_message(jsonArray.getJSONObject(i).getString("received_message"));
                if (!(jsonArray.getJSONObject(i).isNull("send_message")))
                    request.setSent_message(jsonArray.getJSONObject(i).getString("send_message"));
                if (!(jsonArray.getJSONObject(i).isNull("sender_mb")))
                    request.setSender_number(jsonArray.getJSONObject(i).getString("sender_mb"));
                if (!(jsonArray.getJSONObject(i).isNull("receiver_mb")))
                    request.setReceiver_number(jsonArray.getJSONObject(i).getString("receiver_mb"));
                if (!(jsonArray.getJSONObject(i).isNull("sender_id")))
                    request.setSender_id(jsonArray.getJSONObject(i).getString("sender_id"));
                if (!(jsonArray.getJSONObject(i).isNull("posttruck_id")))
                    request.setPosttruck_id(jsonArray.getJSONObject(i).getString("posttruck_id"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_id")))
                    request.setRtruck_id(jsonArray.getJSONObject(i).getString("truck_id"));
                if (!(jsonArray.getJSONObject(i).isNull("load_id")))
                    request.setLoad_id(jsonArray.getJSONObject(i).getString("load_id"));
                if (!(jsonArray.getJSONObject(i).isNull("request_status")))
                    request.setRequest_status(jsonArray.getJSONObject(i).getString("request_status"));
                listRequests.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listRequests;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
