package com.waysideutilities.waysidetruckfreights.Cargo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.Adapter.CommentAdapter;
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
 * Created by Archana on 1/4/2017.
 */
public class CommentList extends AppCompatActivity {

    private ListView listComments;
    private String language, truck_id;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private ArrayList<Truck> arrayListComments;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_comment_list);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.comment));
        bundle = getIntent().getBundleExtra("BUNDLE");
        if (bundle.getString("POSTTRUCK_ID") != null) {
            truck_id = bundle.getString("POSTTRUCK_ID");
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
            new GetTruckComments().execute();
        }
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listComments = (ListView) findViewById(R.id.listComments);
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

    private class GetTruckComments extends AsyncTask<Void, Void, InputStream> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CommentList.this);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            try {
                String newString = String.format("truck_id=%s", truck_id);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/reviews.php", "GET", newString);
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
                Log.e("GetTruckComments : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        arrayListComments = getCommentValueFromJson(jsonArray);
                        listComments.setAdapter(new CommentAdapter(arrayListComments, CommentList.this));
                    } else {
                        listComments.setVisibility(View.GONE);
                        Toast.makeText(CommentList.this, R.string.no_comments, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(CommentList.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ArrayList<Truck> getCommentValueFromJson(JSONArray jsonArray) {
        Truck truck = null;
        ArrayList<Truck> listCommentsTruck = new ArrayList<Truck>();
        for (int i = 0; i < jsonArray.length(); i++) {
            truck = new Truck();
            try {
                if (!(jsonArray.getJSONObject(i).isNull("name")))
                    truck.setUserName(jsonArray.getJSONObject(i).getString("name"));
                if (!(jsonArray.getJSONObject(i).isNull("reviews")))
                    truck.setComment(jsonArray.getJSONObject(i).getString("reviews"));
                if (!(jsonArray.getJSONObject(i).isNull("rating")))
                    truck.setRating(jsonArray.getJSONObject(i).getString("rating")+"/5");
                listCommentsTruck.add(truck);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listCommentsTruck;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
