package com.waysideutilities.waysidetruckfreights;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.Adapter.BoardAdapter;
import com.waysideutilities.waysidetruckfreights.Cargo.Cargo_Provider;
import com.waysideutilities.waysidetruckfreights.Cargo.EditLoad;
import com.waysideutilities.waysidetruckfreights.Cargo.PostLoad;
import com.waysideutilities.waysidetruckfreights.Owner.EditTruck;
import com.waysideutilities.waysidetruckfreights.Owner.PostTruck;
import com.waysideutilities.waysidetruckfreights.Owner.Truck_Owner;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Cargo;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Request;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Truck;
import com.waysideutilities.waysidetruckfreights.PojoClasses.User;
import com.waysideutilities.waysidetruckfreights.Profile.BankDetails;
import com.waysideutilities.waysidetruckfreights.Profile.MyLoads;
import com.waysideutilities.waysidetruckfreights.Profile.MyTrucks;
import com.waysideutilities.waysidetruckfreights.Profile.Profile;
import com.waysideutilities.waysidetruckfreights.helper.HTTPhelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.IMG_SHRINK_HEIGHT;
import static com.waysideutilities.waysidetruckfreights.helper.Constants.IMG_SHRINK_WIDTH;
import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;

/**
 * Created by Archana on 1/24/2017.
 */

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    public static ArrayList<Cargo> listOfCargo;

    public class SignUpAsyncTask extends AsyncTask<Void, Void, InputStream> {
        private User user;
        private Context context;

        public SignUpAsyncTask(User user, Context signUp) {
            this.user = user;
            this.context = signUp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(this.context);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("emailid", this.user.getEmail());
                jsonObject.accumulate("name", this.user.getUserName());
                jsonObject.accumulate("mobileno", this.user.getContact_number());
                jsonObject.accumulate("emergencymb", this.user.getEmg_contact_number());
                jsonObject.accumulate("city", this.user.getCity());
                jsonObject.accumulate("usertype", this.user.getWho_u_r());
                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/registernew.php", "GET", newString);
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
                Log.e("SignUp", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("USERID", jsonObject.getString("userid"));
                        editor.putString("USERTYPE", jsonObject.getString("usertype"));
                        editor.commit();
                        if (jsonObject.getString("usertype").equals("Cargo Provider")) {
                            startActivity(new Intent(this.context, Cargo_Provider.class));
                        } else {
                            startActivity(new Intent(this.context, Truck_Owner.class));
                        }
                        finish();
                    } else {
                        Toast.makeText(this.context, R.string.registration_fails, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class UserSignIn extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private User user;

        public UserSignIn(User user, Context context) {
            this.user = user;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(this.context);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... voids) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            String newString = null;
            try {
                if (this.user.getEmail() != null) {
                    newString = String.format("emailid=%s", this.user.getEmail());
                }
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/otp_email.php", "GET", newString);
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
                Log.e("SignIn ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    if (success.equals("1")) {
                        editor.putString("SUCCESS", jsonObject.getString("success"));
                        editor.putString("USERID", jsonObject.getString("Userid"));
                        editor.putString("USERTYPE", jsonObject.getString("Usertype"));
                        editor.putString("EMAIL", jsonObject.getString("emailid"));
                        editor.putString("OTP", jsonObject.getString("OTP"));
                        editor.putString("LANGUAGE", jsonObject.getString("language"));
                        editor.remove("RANDOM_OTP");
                        editor.remove("NUMBER");
                        editor.putString("MOB_NUM", jsonObject.getString("Mobileno"));
                        editor.commit();
                        startActivity(new Intent(this.context, Verify.class));
                        finish();
                    } else {
                        editor.putString("SUCCESS", jsonObject.getString("success"));
                        editor.putString("EMAIL", jsonObject.getString("emailid"));
                        editor.putString("OTP", jsonObject.getString("OTP"));
                        editor.remove("RANDOM_OTP");
                        editor.commit();
                        startActivity(new Intent(this.context, Verify.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class UserSignInWithMobile extends AsyncTask<Void, Void, String> {
        private Context context;
        private User user;

        public UserSignInWithMobile(User user, Context signIn) {
            this.user = user;
            this.context = signIn;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(this.context);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            HTTPhelper helper = new HTTPhelper();
            String inputStream = null;
            String newString = null;

            if (this.user.getContact_number() != null) {
                Random ran = new Random();
                int code = (100000 + ran.nextInt(900000));
                Log.e("RANDOM == ", String.valueOf(code));
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("RANDOM_OTP", String.valueOf(code));
                editor.putString("NUMBER", this.user.getContact_number());
                editor.remove("OTP");
                editor.commit();
                inputStream = helper.makeHttpPostRequest("http://www.waysideutilities.com/api/sms_gate.php", user.getContact_number(), code);
             }
            return inputStream;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            progressDialog.dismiss();
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("status");
                    Log.e("response message", response);
                    if (success.equals("success")) {
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new UserInfoFormMobile(context, this.user.getContact_number()).execute();
                    } else {
                        Toast.makeText(this.context, R.string.err_contact_no, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class Request_To_Cargo_Owner extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private Request request;

        public Request_To_Cargo_Owner(Context selectTruck, Request request) {
            this.context = selectTruck;
            this.request = request;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(this.context);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("sender_id", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                jsonObject.accumulate("receiver_id", this.request.getReceiver_id());
                jsonObject.accumulate("load_id", this.request.getLoad_id());
                jsonObject.accumulate("posttruck_id", this.request.getPosttruck_id());
                jsonObject.accumulate("send_message", this.request.getSent_message());
                jsonObject.accumulate("received_message", this.request.getReceived_message());
                jsonObject.accumulate("sender_mb", this.request.getSender_number());
                jsonObject.accumulate("receiver_mb", this.request.getReceiver_number());
                jsonObject.accumulate("truck_id", this.request.getRtruck_id());
                jsonObject.accumulate("request_status", this.request.getRequest_status());

                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/send_request.php", "GET", newString);

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
                Log.e("truck Request Result", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(this.context, getResources().getString(R.string.your_request) + " " + this.request.getLoad_id() + " " + getResources().getString(R.string.send_success), Toast.LENGTH_SHORT).show();
                        finish();
                        String receiver_mb = jsonObject.getString("receiver_mb");
                        String received_message = jsonObject.getString("received_message");
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new RequestForMessage(this.context, receiver_mb, received_message).execute();
                    } else {
                        Toast.makeText(this.context, R.string.your_request + this.request.getLoad_id() + R.string.send_fail, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class Request_To_Truck_Owner extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private Request request;

        public Request_To_Truck_Owner(Context selectLoad, Request request) {
            this.context = selectLoad;
            this.request = request;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(this.context);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("sender_id", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                jsonObject.accumulate("receiver_id", this.request.getReceiver_id());
                jsonObject.accumulate("load_id", this.request.getLoad_id());
                jsonObject.accumulate("posttruck_id", this.request.getPosttruck_id());
                jsonObject.accumulate("truck_id", this.request.getRtruck_id());
                jsonObject.accumulate("send_message", this.request.getSent_message());
                jsonObject.accumulate("sender_mb", this.request.getSender_number());
                jsonObject.accumulate("receiver_mb", this.request.getReceiver_number());
                jsonObject.accumulate("received_message", this.request.getReceived_message());
                jsonObject.accumulate("request_status", this.request.getRequest_status());

                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/send_request.php", "GET", newString);

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
                Log.e("Request stringResult", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {//sender_mb,receiver_mb,send_message,received_message
                        String receiver_mb = jsonObject.getString("receiver_mb");
                        String received_message = jsonObject.getString("received_message");
                        Toast.makeText(this.context, getResources().getString(R.string.your_request) + " " + this.request.getLoad_id() + " " + getResources().getString(R.string.send_success), Toast.LENGTH_SHORT).show();
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new RequestForMessage(this.context, receiver_mb, received_message).execute();
                    } else {
                        Toast.makeText(this.context, R.string.request_Fails, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class RequestForMessage extends AsyncTask<Void, Void, String> {
        private Context context;
        private String receiver_mb;
        private String received_message;

        public RequestForMessage(Context context, String receiver_mb, String received_message) {
            this.context = context;
            this.receiver_mb = receiver_mb;
            this.received_message = received_message;
        }


        @Override
        protected String doInBackground(Void... voids) {
            HTTPhelper helper = new HTTPhelper();
            String inputStream = null;

            if (this.receiver_mb != null) {
                inputStream = helper.makeHttpPostRequest("http://www.waysideutilities.com/api/sms_gate.php", this.receiver_mb, this.received_message);
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            progressDialog.dismiss();
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("status");
                    Log.e("request for truck", response);
                    if (success.equals("success")) {
                        //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                    } else {
                        Toast.makeText(this.context, R.string.err_contact_no, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class PostLoadAsyncTask extends AsyncTask<Void, Void, InputStream> {
        private Cargo cargo;
        private Context context;

        public PostLoadAsyncTask(Cargo cargo, PostLoad postLoad) {
            this.cargo = cargo;
            this.context = postLoad;
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
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("userId", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                jsonObject.accumulate("date", this.cargo.getDate());
                //jsonObject.accumulate("from", this.cargo.getFrom());
                //jsonObject.accumulate("to", this.cargo.getTo());

                jsonObject.accumulate("from_street_name", this.cargo.getFrom_street_name());
                jsonObject.accumulate("from_landmark", this.cargo.getFrom_landmark());
                jsonObject.accumulate("from_city", this.cargo.getFrom_city());
                jsonObject.accumulate("from_state", this.cargo.getFrom_state());
                jsonObject.accumulate("from_pincode", this.cargo.getFrom_pincode());

                jsonObject.accumulate("to_street_name", this.cargo.getTo_street_name());
                jsonObject.accumulate("to_landmark", this.cargo.getTo_landmark());
                jsonObject.accumulate("to_city", this.cargo.getTo_city());
                jsonObject.accumulate("to_state", this.cargo.getTo_state());
                jsonObject.accumulate("to_pincode", this.cargo.getTo_pincode());

                jsonObject.accumulate("mobile_no", this.cargo.getContactNumber());
                jsonObject.accumulate("load_category", this.cargo.getLoad_Category());
                jsonObject.accumulate("load_type", this.cargo.getFtl_ltl());
                jsonObject.accumulate("weight", this.cargo.getWeight());
                jsonObject.accumulate("type_of_truck", this.cargo.getType_of_truck());
                jsonObject.accumulate("load_description", this.cargo.getLoad_description());
                jsonObject.accumulate("booked_status", "0");
                jsonObject.accumulate("load_booked_by_id", "0");
                jsonObject.accumulate("trip_status", "0");

                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/postload.php", "GET", newString);

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
                Log.e("PostLoad stringResult", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(this.context, R.string.load_posted_successfully, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this.context, R.string.load_posting_fails, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class EditLoadAsyncTask extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private Cargo cargo;

        public EditLoadAsyncTask(Cargo cargo, EditLoad editLoad) {
            this.context = editLoad;
            this.cargo = cargo;
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
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("userId", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                jsonObject.accumulate("loadid", this.cargo.getId());
                jsonObject.accumulate("date", this.cargo.getDate());
                // jsonObject.accumulate("from", this.cargo.getFrom());
                //jsonObject.accumulate("to", this.cargo.getTo());
                jsonObject.accumulate("from_street_name", this.cargo.getFrom_street_name());
                jsonObject.accumulate("from_landmark", this.cargo.getFrom_landmark());
                jsonObject.accumulate("from_city", this.cargo.getFrom_city());
                jsonObject.accumulate("from_state", this.cargo.getFrom_state());
                jsonObject.accumulate("from_pincode", this.cargo.getFrom_pincode());

                jsonObject.accumulate("to_street_name", this.cargo.getTo_street_name());
                jsonObject.accumulate("to_landmark", this.cargo.getTo_landmark());
                jsonObject.accumulate("to_city", this.cargo.getTo_city());
                jsonObject.accumulate("to_state", this.cargo.getTo_state());
                jsonObject.accumulate("to_pincode", this.cargo.getTo_pincode());

                jsonObject.accumulate("mobile_no", this.cargo.getContactNumber());
                jsonObject.accumulate("load_category", this.cargo.getLoad_Category());
                jsonObject.accumulate("load_type", this.cargo.getFtl_ltl());
                jsonObject.accumulate("weight", this.cargo.getWeight());
                jsonObject.accumulate("type_of_truck", this.cargo.getType_of_truck());
                jsonObject.accumulate("load_description", this.cargo.getLoad_description());
                jsonObject.accumulate("booked_status", "0");
                jsonObject.accumulate("load_booked_by_id", "0");
                jsonObject.accumulate("trip_status", "0");

                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/edit_load.php", "GET", newString);

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
                Log.e("EditLoad stringResult", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(this.context, R.string.load_updated_successfully, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this.context, MyLoads.class));
                        finish();
                    } else {
                        Toast.makeText(this.context, R.string.load_updating_fails, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class GetLoadList extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private ArrayList<Cargo> listLoad;
        private ListView listView;

        public GetLoadList(Context loadBoard, ListView listTruckBoard) {
            this.context = loadBoard;
            this.listView = listTruckBoard;
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
            try {
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/load_board.php", "GET", "");

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
                Log.e("GetLoadList : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        listLoad = getValueFromJsonArray(jsonArray);
                        listView.setAdapter(new BoardAdapter(context, listLoad));
                    } else {
                        listView.setVisibility(View.GONE);
                        Toast.makeText(context, R.string.date_not_found, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class GetTruckList extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private ArrayList<Truck> listTruck;
        private ListView listView;

        public GetTruckList(Context loadBoard, ListView listLoadBoard) {
            this.context = loadBoard;
            this.listView = listLoadBoard;
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
            try {
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/truck_board.php", "GET", "");
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
                Log.e("GetTruckList : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        listTruck = getValueFromJsonArrayForTruck(jsonArray);
                        listView.setAdapter(new BoardAdapter(listTruck, context));
                    } else {
                        listView.setVisibility(View.GONE);
                        Toast.makeText(context, R.string.date_not_found, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected ArrayList<Cargo> getValueFromJsonArray(JSONArray jsonArray) {
        Cargo cargo = null;
        ArrayList<Cargo> listCargo = new ArrayList<Cargo>();
        for (int i = 0; i < jsonArray.length(); i++) {
            cargo = new Cargo();
            try {
                if (!(jsonArray.getJSONObject(i).isNull("loadid")))
                    cargo.setId(jsonArray.getJSONObject(i).getString("loadid"));
                if (!(jsonArray.getJSONObject(i).isNull("userid")))
                    cargo.setCargo_user_id(jsonArray.getJSONObject(i).getString("userid"));
                if (!(jsonArray.getJSONObject(i).isNull("date")))
                    cargo.setDate(jsonArray.getJSONObject(i).getString("date"));

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


                if (!(jsonArray.getJSONObject(i).isNull("mobile_no")))
                    cargo.setContactNumber(jsonArray.getJSONObject(i).getString("mobile_no"));
                if (!(jsonArray.getJSONObject(i).isNull("load_category")))
                    cargo.setLoad_Category(jsonArray.getJSONObject(i).getString("load_category"));
                if (!(jsonArray.getJSONObject(i).isNull("load_type")))
                    cargo.setFtl_ltl(jsonArray.getJSONObject(i).getString("load_type"));
                if (!(jsonArray.getJSONObject(i).isNull("weight")))
                    cargo.setWeight(jsonArray.getJSONObject(i).getString("weight"));
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
                listCargo.add(cargo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listCargo;
    }

    public class AddComment extends AsyncTask<Void, Void, InputStream> {
        private String Posttruck_id;
        private Context context;
        private String comment, rating;

        public AddComment(Context comments, String Posttruck_id, String s, String rating) {
            this.Posttruck_id = Posttruck_id;
            this.context = comments;
            this.comment = s;
            this.rating = rating;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(this.context);
            progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("reviews", this.comment);
                jsonObject.accumulate("posttruck_id", this.Posttruck_id);
                jsonObject.accumulate("rating", this.rating);
                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/add_reviews.php", "GET", newString);
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
                Log.e("Comments Details : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(this.context, R.string.review_added, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class SearchLoadList extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private Cargo cargo;
        private ArrayList<Cargo> listCargo;
        private ListView listView;

        public SearchLoadList(Context loadBoard, Cargo cargo, ListView listLoadBoard) {
            this.context = loadBoard;
            this.cargo = cargo;
            this.listView = listLoadBoard;
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
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("date", this.cargo.getDate());
                jsonObject.accumulate("from", this.cargo.getFrom_city());
                jsonObject.accumulate("to", this.cargo.getTo_city());
                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/search_load.php", "GET", newString);

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
                Log.e("GetLoadList : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        listCargo = getValueFromJsonArray(jsonArray);
                        listView.setAdapter(new BoardAdapter(context, listCargo));
                    } else {
                        listView.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, R.string.date_not_found, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class SearchTruckList extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private Truck truck;
        private ArrayList<Truck> listTruck;
        private ListView listView;

        public SearchTruckList(Context loadBoard, Truck truck, ListView listTruckBoard) {
            this.context = loadBoard;
            this.truck = truck;
            this.listView = listTruckBoard;
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
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("date", this.truck.getDate());
                jsonObject.accumulate("from", this.truck.getFrom());
                jsonObject.accumulate("to", this.truck.getTo());
                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/search_truck.php", "GET", newString);

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
                Log.e("GetTruckList : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        listTruck = getValueFromJsonArrayForTruck(jsonArray);
                        listView.setAdapter(new BoardAdapter(listTruck, context));
                    } else {
                        Toast.makeText(context, R.string.date_not_found, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected ArrayList<Truck> getValueFromJsonArrayForTruck(JSONArray jsonArray) {
        Truck truck = null;
        ArrayList<Truck> listTruck = new ArrayList<Truck>();
        for (int i = 0; i < jsonArray.length(); i++) {
            truck = new Truck();
            try {
                if (!(jsonArray.getJSONObject(i).isNull("truck_id")))
                    truck.setId(jsonArray.getJSONObject(i).getString("truck_id"));
                if (!(jsonArray.getJSONObject(i).isNull("userid")))
                    truck.setTruck_owner_id(jsonArray.getJSONObject(i).getString("userid"));
                if (!(jsonArray.getJSONObject(i).isNull("driver_name")))
                    truck.setDriverName(jsonArray.getJSONObject(i).getString("driver_name"));
                if (!(jsonArray.getJSONObject(i).isNull("driver_mobile_no")))
                    truck.setDriverNumber(jsonArray.getJSONObject(i).getString("driver_mobile_no"));
                if (!(jsonArray.getJSONObject(i).isNull("owner_mb")))
                    truck.setContactNumber(jsonArray.getJSONObject(i).getString("owner_mb"));
               /* if (!(jsonArray.getJSONObject(i).isNull("truck_no")))
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
                if (!(jsonArray.getJSONObject(i).isNull("posttruck_id")))
                    truck.setPost_truck_id(jsonArray.getJSONObject(i).getString("posttruck_id"));
                if (!(jsonArray.getJSONObject(i).isNull("load_category")))
                    truck.setLoad_Category(jsonArray.getJSONObject(i).getString("load_category"));
                if (!(jsonArray.getJSONObject(i).isNull("load_capacity")))
                    truck.setLoad_Capacity(jsonArray.getJSONObject(i).getString("load_capacity"));
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
                if (!(jsonArray.getJSONObject(i).isNull("truck_load")))
                    truck.setTruckLoadPassing(jsonArray.getJSONObject(i).getString("truck_load"));
                if (!(jsonArray.getJSONObject(i).isNull("truck_discription")))
                    truck.setLoad_description(jsonArray.getJSONObject(i).getString("truck_discription"));
                if (!(jsonArray.getJSONObject(i).isNull("charges")))
                    truck.setCharges(jsonArray.getJSONObject(i).getString("charges"));
                if (!(jsonArray.getJSONObject(i).isNull("booked_status")))
                    truck.setBook_status(jsonArray.getJSONObject(i).getString("booked_status"));
                if (!(jsonArray.getJSONObject(i).isNull("trip_status")))
                    truck.setTrip_status(jsonArray.getJSONObject(i).getString("trip_status"));
                if (!(jsonArray.getJSONObject(i).isNull("booked_by_id")))
                    truck.setBooked_by_id(jsonArray.getJSONObject(i).getString("booked_by_id"));

                listTruck.add(truck);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listTruck;
    }

/*    public class PostDriverDetails extends AsyncTask<Void, Void, String> {
        private Context context;
        private Truck truck;
        private String responseString;

        public PostDriverDetails(Context loadBoard, Truck truck) {
            this.context = loadBoard;
            this.truck = truck;
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
        protected String doInBackground(Void... params) {
            HttpPost httpPost = new HttpPost("http://www.waysideutilities.com/api/fill_truck_detail.php");
            HttpClient httpClient = new DefaultHttpClient();
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (this.truck.getTruckImage() != null)
                entityBuilder.addBinaryBody("truck_image", new File(this.truck.getTruckImage()));

            if (this.truck.getTruckInsProviderImage() != null)
                entityBuilder.addBinaryBody("insurance_copy", new File(this.truck.getTruckInsProviderImage()));

            if (this.truck.getDriverLicenceImage() != null)
                entityBuilder.addBinaryBody("license_copy", new File(this.truck.getDriverLicenceImage()));

            if (this.truck.getTruckRegistrationImage() != null)
                entityBuilder.addBinaryBody("vehical_reg_copy", new File(this.truck.getTruckRegistrationImage()));

            entityBuilder.addTextBody("userId", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
            entityBuilder.addTextBody("driver_name", this.truck.getDriverName());
            entityBuilder.addTextBody("driver_mobile_no", this.truck.getDriverNumber());
            //entityBuilder.addTextBody("truck_no", this.truck.getTruckNumber());
            entityBuilder.addTextBody("truck_reg_no", this.truck.getTruckRegNumber());
            entityBuilder.addTextBody("load_passing", this.truck.getTruckLoadPassing());
            entityBuilder.addTextBody("truck_city", this.truck.getTruckCity());

            // entityBuilder.addTextBody("document", );
            HttpEntity httpEntity = entityBuilder.build();
            httpPost.setEntity(httpEntity);
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity entity = httpResponse.getEntity();
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                responseString = new BasicResponseHandler().handleResponse(httpResponse);
                Log.e("Response == ", responseString);
                JSONObject jsonObject = new JSONObject(responseString);
                String success = jsonObject.getString("success");
                if (success.equals("1")) {
                    startActivity(new Intent(this.context, PostTruck.class));
                    finish();
                } else {
                    Toast.makeText(this.context, "Data Insertion fail", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);
            progressDialog.dismiss();
        }
    }*/

    public static Bitmap decodeSampledBitmapFromResource(Uri uri, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeFile(uri.getPath(), options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(uri.getPath(), options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public class EditTruckAsyncTask extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private Truck truck;

        public EditTruckAsyncTask(Truck truck, EditTruck editTruck) {
            this.context = editTruck;
            this.truck = truck;
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
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("truck_id", this.truck.getId());
                jsonObject.accumulate("posttruck_id", this.truck.getPost_truck_id());
                jsonObject.accumulate("userId", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                //jsonObject.accumulate("driver_name", this.truck.getDriverName());
                //jsonObject.accumulate("driver_mobile_no", this.truck.getDriverNumber());
                //jsonObject.accumulate("driver_licence_no", this.truck.getDriver_licence_number());
                //jsonObject.accumulate("driver_licence_location", this.truck.getDriverLicenceIssueCity());
                jsonObject.accumulate("date", this.truck.getDate());
                jsonObject.accumulate("from", this.truck.getFrom());
                jsonObject.accumulate("to", this.truck.getTo());

                jsonObject.accumulate("truck_type", this.truck.getType_of_truck());
                jsonObject.accumulate("load_category", this.truck.getLoad_Category());
                jsonObject.accumulate("load_capacity", this.truck.getLoad_Capacity());
                jsonObject.accumulate("charges", this.truck.getCharges());
                jsonObject.accumulate("load_type", this.truck.getFtl_ltl());
                jsonObject.accumulate("truck_description", this.truck.getLoad_description());

                jsonObject.accumulate("booked_status", this.truck.getBook_status());
                jsonObject.accumulate("trip_status", this.truck.getTrip_status());
                jsonObject.accumulate("truck_booked_by_id", this.truck.getBooked_by_id());

                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/edit_post_truck.php", "GET", newString);

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
                Log.e("Edit Truck : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(this.context, R.string.edit_success_msg, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this.context, MyTrucks.class));
                        finish();
                    } else {
                        Toast.makeText(this.context, R.string.edit_fail_msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class UpdateUserProfile extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private User user;
        private TextView txtName, txtCity, txtEmail, txtMobNumber;

        public UpdateUserProfile(Context profile, User user, TextView txtName, TextView txtCity, TextView txtEmail, TextView txtMobNumber) {
            this.context = profile;
            this.user = user;
            this.txtName = txtName;
            this.txtCity = txtCity;
            this.txtEmail = txtEmail;
            this.txtMobNumber = txtMobNumber;
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
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("name", this.user.getUserName());
                jsonObject.accumulate("city", this.user.getCity());
                jsonObject.accumulate("emailid", this.user.getEmail());
                jsonObject.accumulate("userId", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                jsonObject.accumulate("mobileno", this.user.getContact_number());
                jsonObject.accumulate("emergencymb", this.user.getEmg_contact_number());

                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/edit_profile.php", "GET", newString);
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
                Log.e("UpdateUserProfile : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    if (jsonObject.getString("success").equals("1")) {
                        this.txtName.setText(jsonObject.getString("name"));
                        this.txtCity.setText(jsonObject.getString("city"));
                        this.txtEmail.setText(jsonObject.getString("emailid"));
                        this.txtMobNumber.setText(jsonObject.getString("mobileno"));
                    } else if (jsonObject.getString("success").equals("2")) {
                        Toast.makeText(this.context, R.string.err_acc_exit, Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.getString("success").equals("3")) {
                        Toast.makeText(this.context, R.string.err_mob_no_exit, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this.context, R.string.err_update_fail, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class PostBankDetails extends AsyncTask<Void, Void, String> {
        private Context context;
        private User user;
        private String responseString;

        public PostBankDetails(BankDetails bankDetails, User user) {
            this.context = bankDetails;
            this.user = user;
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
        protected String doInBackground(Void... params) {
            HttpPost httpPost = new HttpPost("http://www.waysideutilities.com/api/bank_details.php");
            HttpClient httpClient = new DefaultHttpClient();
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (this.user.getTruckOwnerPanNo() != null)
                entityBuilder.addBinaryBody("owner_pan_adhar", new File(this.user.getTruckOwnerPanNo()));
            entityBuilder.addTextBody("userId", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
            entityBuilder.addTextBody("owner_name", this.user.getTruckOwnerName());
            entityBuilder.addTextBody("owner_mobile_no", this.user.getTruckOwnerNo());
            entityBuilder.addTextBody("owner_city", this.user.getTruckOwnerCity());
            entityBuilder.addTextBody("owner_addess", this.user.getTruckOwnerAddress());
            entityBuilder.addTextBody("account_name", this.user.getTruckOwnerAccName());
            entityBuilder.addTextBody("account_no", this.user.getTruckOwnerAccNo());
            entityBuilder.addTextBody("IFSC_code", this.user.getTruckOwnerBankIFSCNo());
            entityBuilder.addTextBody("bank_name", this.user.getTruckOwnerBankName());

            HttpEntity httpEntity = entityBuilder.build();
            httpPost.setEntity(httpEntity);
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity entity = httpResponse.getEntity();
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                responseString = new BasicResponseHandler().handleResponse(httpResponse);
                Log.e("Response == ", responseString);
                JSONObject jsonObject = new JSONObject(responseString);
                String success = jsonObject.getString("success");
                if (success.equals("1")) {
                    finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);
            progressDialog.dismiss();
        }
    }

    public class DeleteUserProfile extends AsyncTask<Void, Void, InputStream> {
        private Context context;

        public DeleteUserProfile(Profile profile) {
            this.context = profile;
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
            try {
                String newString = String.format("userId=%s", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/delete_account.php", "GET", newString);
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
                Log.e("DeleteUserProfile : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(context, R.string.account_successfully_deleted, Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.clear();
                        editor.commit();
                        startActivity(new Intent(this.context, SignIn.class));
                        finishAffinity();
                    } else {
                        Toast.makeText(context, R.string.account_deleted_Fails, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class DeleteMyLoads extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private String liadId;

        public DeleteMyLoads(Context myLoads, String id) {
            this.context = myLoads;
            this.liadId = id;
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
            try {
                String newString = String.format("loadid=%s", this.liadId);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/delete_load.php", "GET", newString);
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
                Log.e("DeleteMyLoad : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(context, R.string.load_successfully_deleted, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this.context, MyLoads.class));
                        finish();
                    } else {
                        Toast.makeText(context, R.string.load_deleted_Fails, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();

            }
        }
    }

    public class DeleteMyTruck extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private String liadId;

        public DeleteMyTruck(Context myLoads, String id) {
            this.context = myLoads;
            this.liadId = id;
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
            try {
                String newString = String.format("posttruck_id=%s", this.liadId);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/delete_truck.php", "GET", newString);
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
                Log.e("DeleteMyTruck : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(context, R.string.truck_successfully_deleted, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this.context, MyTrucks.class));
                        finish();
                    } else {
                        Toast.makeText(context, R.string.truck_deleted_Fails, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();

            }
        }
    }

    public class AddTruck extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private Truck truck;

        public AddTruck(Truck truck, Context addTruck) {
            this.context = addTruck;
            this.truck = truck;
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
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("userId", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                jsonObject.accumulate("truck_id", this.truck.getId());
                jsonObject.accumulate("date", this.truck.getDate());
                jsonObject.accumulate("from", this.truck.getFrom());
                jsonObject.accumulate("to", this.truck.getTo());
                jsonObject.accumulate("load_category", this.truck.getLoad_Category());
                jsonObject.accumulate("load_capacity", this.truck.getLoad_Capacity());
                jsonObject.accumulate("load_type", this.truck.getFtl_ltl());
                jsonObject.accumulate("truck_type", this.truck.getType_of_truck());
                jsonObject.accumulate("truck_description", this.truck.getLoad_description());
                jsonObject.accumulate("booked_status", "0");
                jsonObject.accumulate("charges", this.truck.getCharges());
                jsonObject.accumulate("truck_booked_by_id", "0");
                jsonObject.accumulate("trip_status", "0");

                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/post_truck.php", "GET", newString);

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
                Log.e("AddTruck stringResult", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(this.context, R.string.truck_posted_successfully, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, MyTrucks.class));
                        finish();
                    } else {
                        Toast.makeText(this.context, R.string.truck_posting_fails, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UserInfoFormMobile extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private String number;

        public UserInfoFormMobile(Context context, String contact_number) {
            this.context = context;
            this.number = contact_number;
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            try {
                String newString = String.format("mobileno=%s", this.number);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/user_info_from_mobile.php", "GET", newString);

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
                Log.e("UserInfofrom Mobile ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    if (success.equals("1")) {
                        editor.putString("SUCCESS", jsonObject.getString("success"));
                        editor.putString("USERID", jsonObject.getString("userid"));
                        editor.putString("USERTYPE", jsonObject.getString("usertype"));
                        editor.putString("EMAIL", jsonObject.getString("emailid"));
                        editor.remove("OTP");
                        editor.putString("NUMBER", jsonObject.getString("mobileno"));
                        //editor.putString("LANGUAGE", jsonObject.getString("language"));
                        editor.commit();
                        startActivity(new Intent(this.context, Verify.class));
                        finish();
                    } else {
                        editor.putString("SUCCESS", jsonObject.getString("success"));
                        editor.commit();
                        startActivity(new Intent(this.context, Verify.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
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


    public class UpdateDriverLicense extends AsyncTask<Void, Void, String> {
        private Context context;
        private Truck truck;
        private ImageView imageLicense;
        private String responseString = null;
        private ProgressBar progressBar;

        public UpdateDriverLicense(Context editTruck, Truck truck, ImageView imageLicense, ProgressBar progressBar) {
            this.context = editTruck;
            this.imageLicense = imageLicense;
            this.progressBar = progressBar;
            this.truck = truck;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpPost httpPost = new HttpPost("http://www.waysideutilities.com/api/edit_license_copy.php");
            HttpClient httpClient = new DefaultHttpClient();
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (this.truck.getDriverLicenceImage() != null)
                entityBuilder.addBinaryBody("license_copy", new File(this.truck.getDriverLicenceImage()));
            entityBuilder.addTextBody("userId", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
            entityBuilder.addTextBody("posttruck_id", this.truck.getId());
            HttpEntity httpEntity = entityBuilder.build();
            httpPost.setEntity(httpEntity);
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity entity = httpResponse.getEntity();
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                responseString = new BasicResponseHandler().handleResponse(httpResponse);
                Log.e("Response == ", responseString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);
            JSONObject jsonObject = null;
            this.progressBar.setVisibility(View.GONE);
            if (responseString != null) {
                try {
                    jsonObject = new JSONObject(responseString);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Bitmap newBitmap = decodeSampledBitmapFromResource(Uri.parse(this.truck.getDriverLicenceImage()), IMG_SHRINK_WIDTH, IMG_SHRINK_HEIGHT);
                        this.imageLicense.setImageBitmap(newBitmap);
                    } else {
                        Toast.makeText(this.context, "Data Insertion fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, "Upload image fails", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class RequestForTruckMessage extends AsyncTask<Void, Void, String> {
        private Context context;
        private String receiver_mb;
        private String received_message;

        public RequestForTruckMessage(Context context, String receiver_mb, String received_message) {
            this.context = context;
            this.receiver_mb = receiver_mb;
            this.received_message = received_message;
        }


        @Override
        protected String doInBackground(Void... voids) {
            HTTPhelper helper = new HTTPhelper();
            String inputStream = null;

            if (this.receiver_mb != null) {
                inputStream = helper.makeHttpPostRequest("http://www.waysideutilities.com/api/sms_gate.php", this.receiver_mb, this.received_message);
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("status");
                    Log.e("request for truck", response);
                    if (success.equals("success")) {
                    } else {
                        Toast.makeText(this.context, R.string.err_contact_no, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

/*public class EditTruckAsyncTask extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private Truck truck;

        public EditTruckAsyncTask(Truck truck, EditTruck editTruck) {
            this.context = editTruck;
            this.truck = truck;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(this.context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("userId", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                jsonObject.accumulate("posttruck_id", this.truck.getId());
                jsonObject.accumulate("date", this.truck.getDate());
                jsonObject.accumulate("from", this.truck.getFrom());
                jsonObject.accumulate("to", this.truck.getTo());
                jsonObject.accumulate("driver_mobile_no", this.truck.getDriverNumber());
                jsonObject.accumulate("load_category", this.truck.getLoad_Category());
                jsonObject.accumulate("load_capacity", this.truck.getLoad_Capacity());
                jsonObject.accumulate("load_type", this.truck.getFtl_ltl());
                jsonObject.accumulate("driver_name", this.truck.getDriverName());
                jsonObject.accumulate("truck_type", this.truck.getType_of_truck());
                jsonObject.accumulate("load_discription", this.truck.getLoad_description());
                jsonObject.accumulate("driver_licence_no", this.truck.getDriverLicenceN());
                jsonObject.accumulate("driver_licence_location", this.truck.getDriverLicenceIssueCity());
                jsonObject.accumulate("charges", this.truck.getCharges());

                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/edit_truck.php", "GET", newString);

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
                Log.e("EditLoad stringResult", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(this.context, "Record updated successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this.context, MyTrucks.class));
                        finish();
                    } else {
                        Toast.makeText(this.context, "Record updation fails", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class UserSignIn extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private User user;

        public UserSignIn(User user, SignIn signIn) {
            this.user = user;
            this.context = signIn;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(this.context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... voids) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("emailid", this.user.getEmail());
                jsonObject.accumulate("password", this.user.getPassword());
                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/login1.php", "GET", newString);
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
                Log.e("SignIn ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("USERID", jsonObject.getString("userid"));
                        editor.putString("USERTYPE", jsonObject.getString("usertype"));
                        editor.commit();
                        if (jsonObject.getString("usertype").equals("Cargo Provider")) {
                            startActivity(new Intent(this.context, Cargo_Provider.class));
                            finish();
                        } else {
                            startActivity(new Intent(this.context, Truck_Owner.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(this.context, "Login fail ! Please enter correct email id & password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }
    */
/*public class ForgotPassword extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private User user;

        public ForgotPassword(User user, SignIn signIn) {
            this.user = user;
            this.context = signIn;
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
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("emailid", this.user.getEmail());
                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/forgotpassword.php", "GET", newString);
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
                Log.e("ForgetPassword ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(this.context, "Mail has been sent to your registered email address", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this.context, "No user found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }*/





  /*  public class EditTruckAsyncTask extends AsyncTask<Void, Void, String> {
        private Context context;
        private Truck truck;
        private String responseString;

        public EditTruckAsyncTask(Truck truck, EditTruck editTruck) {
            this.context = editTruck;
            this.truck = truck;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(this.context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpPost httpPost = new HttpPost("http://www.waysideutilities.com/api/edit_truck.php");
            HttpClient httpClient = new DefaultHttpClient();
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (this.truck.getDriverImage() != null)
                entityBuilder.addBinaryBody("driver_image", new File(this.truck.getDriverImage()));
            entityBuilder.addTextBody("userId", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
            entityBuilder.addTextBody("driver_name", this.truck.getDriverName());
            entityBuilder.addTextBody("driver_mobile_no", this.truck.getDriverNumber());
            entityBuilder.addTextBody("driver_licence_no", this.truck.getDriverLicenceN());
            entityBuilder.addTextBody("driver_licence_location", this.truck.getDriverLicenceIssueCity());
            entityBuilder.addTextBody("date", this.truck.getDate());
            entityBuilder.addTextBody("from", this.truck.getFrom());
            entityBuilder.addTextBody("to", this.truck.getTo());
            entityBuilder.addTextBody("truck_type", this.truck.getType_of_truck());
            entityBuilder.addTextBody("load_category", this.truck.load_Category);
            entityBuilder.addTextBody("load_capacity", this.truck.load_Capacity);
            entityBuilder.addTextBody("charges", this.truck.getCharges());
            entityBuilder.addTextBody("load_type", this.truck.getFtl_ltl());
            entityBuilder.addTextBody("load_discription", this.truck.getLoad_description());

            HttpEntity httpEntity = entityBuilder.build();
            httpPost.setEntity(httpEntity);
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity entity = httpResponse.getEntity();
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                responseString = new BasicResponseHandler().handleResponse(httpResponse);
                Log.e("Response == ", responseString);
                JSONObject jsonObject = new JSONObject(responseString);
                String success = jsonObject.getString("success");
                if (success.equals("1")) {
                    startActivity(new Intent(this.context, MyTrucks.class));
                    finish();
                } else {
                    Toast.makeText(this.context, "Data Insertion fail", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);
            progressDialog.dismiss();
        }
    }*/

/*
* public class PostBankDetails extends AsyncTask<Void, Void, InputStream> {
        private Context context;
        private User user;



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
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("", );
                jsonObject.accumulate("", );
                jsonObject.accumulate("", );
                jsonObject.accumulate("", );
                jsonObject.accumulate("", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                jsonObject.accumulate("", );
                jsonObject.accumulate("",);
                jsonObject.accumulate("", );
                jsonObject.accumulate("", );
                jsonObject.accumulate("", );

                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("", "GET", newString);
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
                Log.e("PostBankDetails : ", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }
*/