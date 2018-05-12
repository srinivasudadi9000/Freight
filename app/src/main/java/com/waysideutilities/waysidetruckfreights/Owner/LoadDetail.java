package com.waysideutilities.waysidetruckfreights.Owner;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.Cargo.SelectTruck;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.*;

/**
 * Created by Archana on 8/29/2016.
 */
public class LoadDetail extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtLoadId, txtDate, txtFrom, txtTo, txtLoadCategory, txtTruckType, txtWeight, txtLoadFlt, txtLoadDesc;
    private Button btnCall, btnRequest;
    private int callPermission;
    private String contact_number, language, load_id, cargo_owner_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_load_detail);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.load_details);
        Bundle bundle = getIntent().getBundleExtra("BUNDLE");
        getSetValueFromBundle(bundle);
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtLoadId = (TextView) findViewById(R.id.txtLoadId);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtFrom = (TextView) findViewById(R.id.txtFrom);
        txtTo = (TextView) findViewById(R.id.txtTo);
        txtLoadCategory = (TextView) findViewById(R.id.txtLoadCategory);
        txtTruckType = (TextView) findViewById(R.id.txtTruckType);
        txtWeight = (TextView) findViewById(R.id.txtWeight);
        txtLoadFlt = (TextView) findViewById(R.id.txtLoadFlt);
        txtLoadDesc = (TextView) findViewById(R.id.txtLoadDesc);
        btnRequest = (Button) findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("NUMBER", contact_number);
                bundle.putString("LOAD_ID", load_id);
                bundle.putString("CARGO_OWNER_ID", cargo_owner_id);
                Intent intent = new Intent(LoadDetail.this, SelectTruck.class);
                intent.putExtra("BUNDLE", bundle);
                startActivity(intent);
                finish();
            }
        });
        btnCall = (Button) findViewById(R.id.btnCall);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
    }

    private void getSetValueFromBundle(Bundle bundle) {
        if (bundle.getString("LOADID") != null) {
            load_id = bundle.getString("LOADID");
            txtLoadId.setText(bundle.getString("LOADID"));
        }
        if (bundle.getString("CARGO_OWNER_ID") != null)
            cargo_owner_id = bundle.getString("CARGO_OWNER_ID");
        if (bundle.getString("DATE") != null)
            txtDate.setText(FrightUtils.getFormattedDate("yyyy-MM-dd", "dd/MM/yyyy", bundle.getString("DATE"), null));
        if (bundle.getString("FROM") != null)
            txtFrom.setText(bundle.getString("FROM"));
        String currentString, data = "";
        currentString = txtFrom.getText().toString();

        String[] separated = currentString.split(",");
        for (int i = 0; i < separated.length; i++) {
            data = data + separated[i].toString() + "\n";
        }
        txtFrom.setText(data.toString());

        if (bundle.getString("TO") != null)
            txtTo.setText(bundle.getString("TO"));
        String currents, dataa = "";
        currents = txtTo.getText().toString();

        String[] separateda = currents.split(",");
        for (int i = 0; i < separateda.length; i++) {
            dataa = dataa + separateda[i].toString() + "\n";
        }
        txtTo.setText(dataa.toString());
        if (bundle.getString("CONTACT_NO") != null)
            contact_number = bundle.getString("CONTACT_NO");
        if (bundle.getString("CATEGORY") != null)
            txtLoadCategory.setText(bundle.getString("CATEGORY"));
        if (bundle.getString("TRUCK_TYPE") != null)
            txtTruckType.setText(bundle.getString("TRUCK_TYPE"));
        if (bundle.getString("WEIGHT") != null)
            txtWeight.setText(bundle.getString("WEIGHT"));
        if (bundle.getString("FTL_LTL") != null)
            txtLoadFlt.setText(bundle.getString("FTL_LTL"));
        if (bundle.getString("DESCRIPTION") != null)
            txtLoadDesc.setText(bundle.getString("DESCRIPTION"));
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

    private void call() {
        if (Build.VERSION.SDK_INT >= 23) {
            callPermission = checkSelfPermission(android.Manifest.permission.CALL_PHONE);
        }
        if (callPermission == 0) {
            callOwner();
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
                if ((grantResults.length == 2) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(LoadDetail.this, "CALL PERMISSION_GRANTED", Toast.LENGTH_SHORT);
                    callPermission = checkSelfPermission(android.Manifest.permission.CAMERA);
                    if (callPermission == 0) {
                        callOwner();
                    }
                } else {
                    Toast.makeText(LoadDetail.this, "CALL permission allows us to access phone state. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void callOwner() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + contact_number));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        }
    }

}
