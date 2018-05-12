package com.waysideutilities.waysidetruckfreights.Owner;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.BaseActivity;
import com.waysideutilities.waysidetruckfreights.Cargo.Cargo_Provider;
import com.waysideutilities.waysidetruckfreights.Profile.Profile;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.ASK_CALL_PERMISSIONS;
import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;
/**
 * Created by Archana on 8/29/2016.
 */
public class Truck_Owner extends BaseActivity implements View.OnClickListener {

    private ImageView imageProfile, btnLoadBoard;
    private int callPermission;
    private String language;
    private ImageView btnPostTruck;
    private TextView tollFreeCall,contactNumber;
    private static int callFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_truck__owner);
        init();
    }


    private void init() {
        btnPostTruck = (ImageView) findViewById(R.id.btnPostTruck);
        btnPostTruck.setOnClickListener(Truck_Owner.this);
        btnLoadBoard = (ImageView) findViewById(R.id.btnLoadBoard);
        btnLoadBoard.setOnClickListener(Truck_Owner.this);
        imageProfile = (ImageView) findViewById(R.id.imageProfile);
        imageProfile.setOnClickListener(Truck_Owner.this);
        tollFreeCall = (TextView)findViewById(R.id.tollFreeCall);
        tollFreeCall.setOnClickListener(Truck_Owner.this);
        contactNumber = (TextView)findViewById(R.id.contactNumber);
        contactNumber.setOnClickListener(Truck_Owner.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnPostTruck) {
            startActivity(new Intent(Truck_Owner.this, CheckTruck.class));
        }
        if (view.getId() == R.id.btnLoadBoard) {
            startActivity(new Intent(Truck_Owner.this, LoadBoard.class));
        }
        if (view.getId() == R.id.imageProfile) {
            if (FrightUtils.hasActiveInternetConnection(Truck_Owner.this)) {
                startActivity(new Intent(Truck_Owner.this, Profile.class));
                finish();
            } else {
                Toast.makeText(Truck_Owner.this, R.string.networkError, Toast.LENGTH_SHORT).show();
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
            if(callFlag == 0) {
                callOwner();
            }else{
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
                    Toast.makeText(Truck_Owner.this, "CALL PERMISSION_GRANTED", Toast.LENGTH_SHORT);
                    callPermission = checkSelfPermission(Manifest.permission.CALL_PHONE);
                    if (callPermission == 0) {
                        if (callPermission == 0) {
                            if(callFlag == 0) {
                                callOwner();
                            }else{
                                callTollFree();
                            }
                        }
                    }
                } else {
                    Toast.makeText(Truck_Owner.this, "CALL permission allows us to access phone state. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
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
}
