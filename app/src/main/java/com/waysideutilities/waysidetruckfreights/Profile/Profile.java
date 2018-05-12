package com.waysideutilities.waysidetruckfreights.Profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.BaseActivity;
import com.waysideutilities.waysidetruckfreights.Cargo.Cargo_Provider;
import com.waysideutilities.waysidetruckfreights.Owner.Truck_Owner;
import com.waysideutilities.waysidetruckfreights.PojoClasses.User;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.SignIn;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;
import com.waysideutilities.waysidetruckfreights.helper.HTTPhelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;
/**
 * Created by Archana on 8/29/2016.
 */
public class Profile extends BaseActivity implements View.OnClickListener {

    private static final String GMAIL_PACKAGE_NAME = "com.google.android.gm" ;
    private Toolbar toolbar;
    private TextView txtName, txtMobNumber, txtEmergncyMobNumber, txtEmail, txtCity, txtSentRequest, txtReceivedRequest, txtMyPost, txtAbout, txtSupport, txtSelectLanguage, txtMyOrders, txtLogOut, txtBankOwnerDetails;
    private ProgressDialog progressDialog;
    private String userType, language, name;
    private Button btnDeleteAccount;
    private User user;
    private AlertDialog dialog;

    public class GetUserProfile extends AsyncTask<Void, Void, InputStream> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Profile.this);
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
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/myprofile.php", "GET", newString);

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
                Log.e("GetUserProfile : ", stringResult);
                try {
                    user = new User();
                    JSONObject jsonObject = new JSONObject(stringResult);
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

                    if (!jsonObject.isNull("name")) {
                        user.setUserName(jsonObject.getString("name"));
                        txtName.setText(jsonObject.getString("name"));
                        editor.putString("USER_NAME", jsonObject.getString("name"));
                        editor.commit();
                        txtBankOwnerDetails.setOnClickListener(Profile.this);
                    }
                    if (!jsonObject.isNull("mobile")) {
                        user.setContact_number(jsonObject.getString("mobile"));
                        txtMobNumber.setText(jsonObject.getString("mobile"));
                        editor.putString("USER_NUMBER", jsonObject.getString("mobile"));
                        editor.commit();
                    }
                    if (!jsonObject.isNull("emergencymb")) {
                        user.setEmg_contact_number(jsonObject.getString("emergencymb"));
                        txtEmergncyMobNumber.setText(jsonObject.getString("emergencymb"));
                    }
                    if (!jsonObject.isNull("emailid")) {
                        user.setEmail(jsonObject.getString("emailid"));
                        txtEmail.setText(jsonObject.getString("emailid"));
                    }
                    if (!jsonObject.isNull("city")) {
                        user.setCity(jsonObject.getString("city"));
                        txtCity.setText(jsonObject.getString("city"));
                    }
                    if (!jsonObject.isNull("owner_address"))
                        user.setTruckOwnerAddress(jsonObject.getString("owner_address"));

                    if (!jsonObject.isNull("owner_name"))
                        user.setTruckOwnerName(jsonObject.getString("owner_name"));
                    if (!jsonObject.isNull("owner_mobile_no"))
                        user.setTruckOwnerNo(jsonObject.getString("owner_mobile_no"));
                    if (!jsonObject.isNull("owner_city"))
                        user.setTruckOwnerCity(jsonObject.getString("owner_city"));
                    if (!jsonObject.isNull("owner_pan_adhar"))
                        user.setTruckOwnerPanNo(jsonObject.getString("owner_pan_adhar"));
                    if (!jsonObject.isNull("account_name"))
                        user.setTruckOwnerAccName(jsonObject.getString("account_name"));
                    if (!jsonObject.isNull("account_no"))
                        user.setTruckOwnerAccNo(jsonObject.getString("account_no"));
                    if (!jsonObject.isNull("IFSC_code"))
                        user.setTruckOwnerBankIFSCNo(jsonObject.getString("IFSC_code"));
                    if (!jsonObject.isNull("bank_name"))
                        user.setTruckOwnerBankName(jsonObject.getString("bank_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(Profile.this, R.string.networkError, Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_profile);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.profile);
        userType = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERTYPE", null);
        if (userType.equals("Cargo Provider")) {
            txtMyPost.setText(getResources().getString(R.string.MyLoads));
            txtMyOrders.setText(getResources().getString(R.string.MyOrder));
        } else {
            txtMyPost.setText(getResources().getString(R.string.MyTruck));
            txtMyOrders.setText(getResources().getString(R.string.MyTrips));
        }
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
        new GetUserProfile().execute();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtName = (TextView) findViewById(R.id.txtName);
        txtMobNumber = (TextView) findViewById(R.id.txtMobNumber);
        txtEmergncyMobNumber = (TextView) findViewById(R.id.txtEmergncyMobNumber);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtCity = (TextView) findViewById(R.id.txtCity);
        txtSelectLanguage = (TextView) findViewById(R.id.txtSelectLanguage);
        txtSelectLanguage.setOnClickListener(Profile.this);
        txtMyPost = (TextView) findViewById(R.id.txtMyPost);
        txtMyPost.setOnClickListener(Profile.this);
        txtLogOut = (TextView) findViewById(R.id.txtLogOut);
        txtLogOut.setOnClickListener(Profile.this);
        txtBankOwnerDetails = (TextView) findViewById(R.id.txtBankOwnerDetails);
        btnDeleteAccount = (Button) findViewById(R.id.btnDeleteAccount);
        btnDeleteAccount.setOnClickListener(Profile.this);
        txtMyOrders = (TextView) findViewById(R.id.txtMyOrders);
        txtMyOrders.setOnClickListener(Profile.this);
        txtSupport = (TextView) findViewById(R.id.txtSupport);
        txtSupport.setOnClickListener(Profile.this);
        txtAbout = (TextView) findViewById(R.id.txtAbout);
        txtAbout.setOnClickListener(Profile.this);
        txtSentRequest = (TextView) findViewById(R.id.txtSentRequest);
        txtSentRequest.setOnClickListener(Profile.this);
        txtReceivedRequest = (TextView) findViewById(R.id.txtReceivedRequest);
        txtReceivedRequest.setOnClickListener(Profile.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (userType.equals("Cargo Provider")) {
            startActivity(new Intent(Profile.this, Cargo_Provider.class));
        } else {
            startActivity(new Intent(Profile.this, Truck_Owner.class));
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (userType.equals("Cargo Provider")) {
                    startActivity(new Intent(Profile.this, Cargo_Provider.class));
                } else {
                    startActivity(new Intent(Profile.this, Truck_Owner.class));
                }
                finish();
                return true;
            case R.id.action_edit:

                if (txtMobNumber.getText().toString().length() == 0) {
                    Toast.makeText(Profile.this, getResources().getString(R.string.err_contact_no), Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User();
                    user.setUserName(txtName.getText().toString());
                    user.setEmail(txtEmail.getText().toString());
                    user.setCity(txtCity.getText().toString());
                    user.setEmg_contact_number(txtEmergncyMobNumber.getText().toString()+ "( Alternate No)");
                    user.setContact_number(txtMobNumber.getText().toString()+"  ( Primary No )");
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                    new UpdateUserProfile(Profile.this, user, txtName, txtCity, txtEmail, txtMobNumber).execute();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txtReceivedRequest) {
            startActivity(new Intent(Profile.this, Received_Requests.class));
        }
        if (view.getId() == R.id.txtSentRequest) {
            startActivity(new Intent(Profile.this, SentRequests.class));
        }
        if (view.getId() == R.id.txtMyPost) {
            if (userType.equals("Cargo Provider")) {
                startActivity(new Intent(Profile.this, MyLoads.class));
            } else {
                startActivity(new Intent(Profile.this, MyTrucks.class));
            }
        }
        if (view.getId() == R.id.txtBankOwnerDetails) {
            Bundle bundle = new Bundle();
            bundle.putString("OWNER_NAME", user.getTruckOwnerName());
            bundle.putString("OWNER_CITY", user.getTruckOwnerCity());
            bundle.putString("OWNER_MOB_NO", user.getTruckOwnerNo());
            bundle.putString("OWNER_PAN_ADHAR_NO", user.getTruckOwnerPanNo());
            bundle.putString("OWNER_ADDRESS", user.getTruckOwnerAddress());
            bundle.putString("ACCOUNT_NAME", user.getTruckOwnerAccName());
            bundle.putString("ACCOUNT_NO", user.getTruckOwnerAccNo());
            bundle.putString("IFSC_CODE", user.getTruckOwnerBankIFSCNo());
            bundle.putString("BANK_NAME", user.getTruckOwnerBankName());
            Intent intent = new Intent(Profile.this, BankDetails.class);
            intent.putExtra("BUNDLE", bundle);
            startActivity(intent);
            //finish();
        }
        if (view.getId() == R.id.txtLogOut) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setCancelable(true);
            alertBuilder.setMessage(getResources().getString(R.string.logout_account));
            alertBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    try {
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.remove("EMAIL");
                        editor.remove("NUMBER");
                        editor.remove("USERTYPE");
                        editor.remove("VERIFIED");
                        editor.remove("USERID");
                        editor.remove("RANDOM_OTP");
                        editor.remove("SUCCESS");
                        editor.remove("OTP");
                        editor.remove("MOB_NUM");
                        editor.remove("NUMBER");
                        editor.commit();
                        finishAffinity();
                        startActivity(new Intent(Profile.this, SignIn.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        }
        if (view.getId() == R.id.txtMyOrders) {
            if (userType.equals("Cargo Provider")) {
                startActivity(new Intent(Profile.this, MyOrder.class));
            } else {
                startActivity(new Intent(Profile.this, MyTrips.class));
            }
        }
        if (view.getId() == R.id.txtSelectLanguage) {
            startActivity(new Intent(Profile.this, ChooseLanguage.class));
            finish();
        }
        if (view.getId() == R.id.txtSupport) {
            sendEmail();
        }
        if (view.getId() == R.id.txtAbout) {
            startActivity(new Intent(Profile.this, AboutUs.class));
        }
        if (view.getId() == R.id.btnDeleteAccount) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Profile.this);
            alertBuilder.setMessage(getResources().getString(R.string.want_to_delete_account));
            alertBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                    new DeleteUserProfile(Profile.this).execute();
                }
            });
            dialog = alertBuilder.create();
            dialog.show();
        }
    }

    private void sendEmail() {
        try {
            String[] TO = {"contact@waysideutilities.com"};
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/html");
            emailIntent.putExtra(Intent.EXTRA_EMAIL,TO);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback from " + user.getUserName());
            emailIntent.putExtra(Intent.EXTRA_TEXT, "This is Email feedback message from wayside freight Android Application");
            final PackageManager pm = this.getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
            String className = null;
            for (final ResolveInfo info : matches) {
                if (info.activityInfo.packageName.equals(GMAIL_PACKAGE_NAME)) {
                    className = info.activityInfo.name;
                    if (className != null && !className.isEmpty()) {
                        break;
                    }
                }
            }
            emailIntent.setClassName(GMAIL_PACKAGE_NAME, className);
            startActivity(emailIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
       /*
        String[] TO = {"contact@waysideutilities.com"};
        Intent emailIntent = new Intent(Intent.ACTION_VIEW);
        emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback from " + user.getUserName());
        emailIntent.putExtra(Intent.EXTRA_TEXT, "This is Email feedback message from wayside freight Android Application");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //finish();
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Profile.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }*/
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
