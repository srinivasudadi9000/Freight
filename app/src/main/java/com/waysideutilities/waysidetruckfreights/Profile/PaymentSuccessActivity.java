package com.waysideutilities.waysidetruckfreights.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.BaseActivity;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Request;
import com.waysideutilities.waysidetruckfreights.PojoClasses.User;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.HTTPhelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;

/**
 * Created by Archana on 8/29/2016.
 */
public class PaymentSuccessActivity extends BaseActivity {

    private TextView txt_teansactionId;
    private Toolbar toolbar;
    private String paid_Amount, driver_name, truck_reg_number, user_name, total_Amount, sender, reciver, postTruckId, truckId, loadId, reciverId, remaining_Amount, request_status;
    private ProgressDialog progressDialog;
    private DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user_name = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USER_NAME", null);

        df = new DecimalFormat("#,###,##0.00");
        Intent intent = getIntent();
        if (intent != null) {
            String orderid = intent.getStringExtra("ORDERID");
            paid_Amount = intent.getStringExtra("AMOUNT");
            sender = intent.getStringExtra("SENDER");
            reciver = intent.getStringExtra("RECEIVER");
            postTruckId = intent.getStringExtra("POST_TRUCK_ID");
            truckId = intent.getStringExtra("TRUCK_ID");
            loadId = intent.getStringExtra("LOAD_ID");
            reciverId = intent.getStringExtra("RECIVER_ID");
            total_Amount = intent.getStringExtra("TOTAL_AMOUNT");
            request_status = intent.getStringExtra("REQUEST_STATUS");
            driver_name = intent.getStringExtra("DRIVER_NAME");
            truck_reg_number = intent.getStringExtra("TRUCK_REG_NO");

            remaining_Amount = String.valueOf(df.format(Double.valueOf(total_Amount) - Double.valueOf(paid_Amount)));

            txt_teansactionId = (TextView) findViewById(R.id.txt_teansactionId);
            txt_teansactionId.setText("Transaction Id : " + orderid);


            Request sRequest = new Request();
            sRequest.setRequest_status(request_status);
            sRequest.setReceiver_number(reciver);
            sRequest.setReceived_message("Please find the cargo provider details -" + user_name +"/"+sender + "\n" +
                    ". For any further assistance with your trip call us on 18004252552. THANK YOU FOR CHOOSING WAYSIDE TRUCK FREIGHT");

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
            new Update_status_request(sRequest).execute();

            String message_to_cargo = "Please find the truck driver details -" + driver_name + "/" + reciver + "\n" + "/" + truck_reg_number +
                    ". For any further assistance with the order call us on 18004252552. THANK YOU FOR CHOOSING WAYSIDE TRUCK FREIGHT";
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
            new RequestForTruckMessage(PaymentSuccessActivity.this, sender, message_to_cargo).execute();

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
            new Book_Truck().execute();
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

    private class Update_status_request extends AsyncTask<Void, Void, InputStream> {
        private Request request;

        public Update_status_request(Request sRequest) {
            this.request = sRequest;
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("sender_id", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                jsonObject.accumulate("receiver_id", reciverId);
                jsonObject.accumulate("load_id", loadId);
                jsonObject.accumulate("posttruck_id", postTruckId);
                jsonObject.accumulate("truck_id", truckId);
                jsonObject.accumulate("request_status", this.request.getRequest_status());

                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/update_request_status.php", "GET", newString);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
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
                Log.e("Update_status sent", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        String receiver_mb = this.request.getReceiver_number();
                        String received_message = this.request.getReceived_message();
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new RequestForTruckMessage(PaymentSuccessActivity.this, receiver_mb, received_message).execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                finish();
                Toast.makeText(PaymentSuccessActivity.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Book_Truck extends AsyncTask<Void, Void, InputStream> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PaymentSuccessActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;

            try {
                String newString = String.format("load_id=%s&posttruck_id=%s&total_charges=%s&paid_amount=%s&unpaid_amount=%s", loadId, postTruckId, total_Amount, paid_Amount, remaining_Amount);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/book_truck.php", "GET", newString);
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
                progressDialog.dismiss();
                String stringResult = builder.toString();
                Log.e("Update_status sent", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                finish();
                Toast.makeText(PaymentSuccessActivity.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
