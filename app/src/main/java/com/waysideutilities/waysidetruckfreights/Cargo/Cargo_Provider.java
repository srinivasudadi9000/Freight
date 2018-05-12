package com.waysideutilities.waysidetruckfreights.Cargo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.Profile.Profile;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.Constants;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;
import com.waysideutilities.waysidetruckfreights.helper.HTTPhelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.ASK_CALL_PERMISSIONS;
import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;

/**
 * Created by Archana on 1/4/2017.
 */
public class Cargo_Provider extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageProfile, btnTruckBoard;
    private TextView tollFreeCall, contactNumber;
    private AlertDialog dialog;
    private int callPermission;
    private String language;
    private ImageView btnPostLoad;
    private static int callFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_cargo);
        init();
    }


    private void init() {
        btnPostLoad = (ImageView) findViewById(R.id.btnPostLoad);
        btnPostLoad.setOnClickListener(Cargo_Provider.this);
        btnTruckBoard = (ImageView) findViewById(R.id.btnTruckBoard);
        btnTruckBoard.setOnClickListener(Cargo_Provider.this);
        imageProfile = (ImageView) findViewById(R.id.imageProfile);
        imageProfile.setOnClickListener(Cargo_Provider.this);
        tollFreeCall = (TextView) findViewById(R.id.tollFreeCall);
        tollFreeCall.setOnClickListener(Cargo_Provider.this);
        contactNumber = (TextView) findViewById(R.id.contactNumber);
        contactNumber.setOnClickListener(Cargo_Provider.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnPostLoad) {
            startActivity(new Intent(Cargo_Provider.this, PostLoad.class));
        }
        if (view.getId() == R.id.btnTruckBoard) {
            startActivity(new Intent(Cargo_Provider.this, TruckBoard.class));
        }
        if (view.getId() == R.id.imageProfile) {
            if (FrightUtils.hasActiveInternetConnection(Cargo_Provider.this)) {
                startActivity(new Intent(Cargo_Provider.this, Profile.class));
                finish();
            } else {
                Toast.makeText(Cargo_Provider.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
        if (view.getId() == R.id.tollFreeCall) {
            callFlag = 1;
            call();
        }

        if (view.getId() == R.id.contactNumber) {
            callFlag = 0;
            call();
        }
    }

    private void call() {
        if (Build.VERSION.SDK_INT >= 23) {
            callPermission = checkSelfPermission(android.Manifest.permission.CALL_PHONE);
        }
        if (callPermission == 0) {
            if (callFlag == 0) {
                callOwner();
            } else {
                callTollFree();
            }
        } else {
            checkForPermission();
        }
    }

    private void checkForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int count = 0;
            String[] permissions = new String[]{""};
            if (callPermission != PackageManager.PERMISSION_GRANTED) {

                permissions[count] = android.Manifest.permission.CALL_PHONE;
            }
            requestPermissions(permissions, ASK_CALL_PERMISSIONS);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ASK_CALL_PERMISSIONS:
                if ((grantResults.length == 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(Cargo_Provider.this, "CALL PERMISSION_GRANTED", Toast.LENGTH_SHORT);
                    callPermission = checkSelfPermission(Manifest.permission.CALL_PHONE);
                    if (callPermission == 0) {
                        if (callFlag == 0) {
                            callOwner();
                        } else {
                            callTollFree();
                        }
                    }
                } else {
                    Toast.makeText(Cargo_Provider.this, "CALL permission allows us to access phone state. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void callOwner() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + "7995566688"));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        }
    }

    private void callTollFree() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + "18004252552"));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        }
    }


    private class CheckTransactionStatus extends AsyncTask<Void, Void, InputStream> {
        private String checksumash, orderId;
        private String newString;

        public CheckTransactionStatus(String checkSum, String orderid) {
            this.checksumash = checkSum;
            this.orderId = orderid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*progressDialog = new ProgressDialog(Cargo_Provider.this);
            // progressDialog.setMessage(getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();*/
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(Constants.MID, Constants.MID_VALUE);
                jsonObject.put(Constants.ORDERID, this.orderId);
                jsonObject.put(Constants.CHECKSUMHASH, checksumash);
                newString = String.format("JsonData=%s", jsonObject.toString());
                inputStream = helper.makeHttpRequest("https://pguat.paytm.com/oltp/HANDLER_INTERNAL/getTxnStatus", "GET", newString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream s) {
            super.onPostExecute(s);
        }
    }
}
