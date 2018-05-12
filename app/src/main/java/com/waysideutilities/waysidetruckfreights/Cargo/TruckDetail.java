package com.waysideutilities.waysidetruckfreights.Cargo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.Owner.SelectLoad;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.ASK_CALL_PERMISSIONS;
import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;
/**
 * Created by Archana on 2/3/2017.
 */
public class TruckDetail extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtTruckId, txtTruckDate, txtTruckFrom, txtTruckTo, txtLoadCategory, txtTruckCharges, txtTruckType, txtWeight, txtRating,txtTruckDesc, txtLoadFltTruck;
    private Button btnTruckCall, btnTruckRequest;
    private String contact_number, driver_number,language, truck_id, post_truck_id,truck_owner_id;
    private int callPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_truck_detail);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.truck_detail);
        Bundle bundle = getIntent().getBundleExtra("BUNDLE");
        getSetValueFromBundle(bundle);
    }

    private void getSetValueFromBundle(Bundle bundle) {
        if (bundle.getString("TRUCKID") != null)
            truck_id = bundle.getString("TRUCKID");
        if(bundle.getString("TRUCK_OWNER_ID") != null)
            truck_owner_id = bundle.getString("TRUCK_OWNER_ID");
        if (bundle.getString("POST_TRUCKID") != null) {
            post_truck_id = bundle.getString("POST_TRUCKID");
            txtTruckId.setText( bundle.getString("POST_TRUCKID"));
        }
        if (bundle.getString("DATE") != null)
            txtTruckDate.setText( FrightUtils.getFormattedDate("yyyy-MM-dd", "dd/MM/yyyy", bundle.getString("DATE"), null));
        if (bundle.getString("FROM") != null)
            txtTruckFrom.setText(  bundle.getString("FROM"));
        if (bundle.getString("TO") != null)
            txtTruckTo.setText( bundle.getString("TO"));
        if (bundle.getString("CONTACT_NO") != null)
            contact_number = bundle.getString("CONTACT_NO");
        if(bundle.getString("DRIVER_NUMBER") != null)
            driver_number = bundle.getString("DRIVER_NUMBER");
        if (bundle.getString("CATEGORY") != null)
            txtLoadCategory.setText( bundle.getString("CATEGORY"));
        if (bundle.getString("TRUCK_TYPE") != null)
            txtTruckType.setText(  bundle.getString("TRUCK_TYPE"));
        if (bundle.getString("LOAD_CAPACITY") != null)
            txtWeight.setText(  bundle.getString("LOAD_CAPACITY"));
        if (bundle.getString("CHARGES") != null) {
            String charges[] = bundle.getString("CHARGES").split("#");
            txtTruckCharges.setText( charges[0]+" /- "+charges[1]);
        }
        if (bundle.getString("FTL_LTL") != null)
            txtLoadFltTruck.setText(  bundle.getString("FTL_LTL"));
        if (bundle.getString("DESCRIPTION") != null)
            txtTruckDesc.setText( bundle.getString("DESCRIPTION"));
    }


    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtTruckId = (TextView) findViewById(R.id.txtId);
        txtTruckDate = (TextView) findViewById(R.id.txtDate);
        txtTruckFrom = (TextView) findViewById(R.id.txtFrom);
        txtTruckTo = (TextView) findViewById(R.id.txtTo);
        txtLoadCategory = (TextView) findViewById(R.id.txtLoadCategory);
        txtWeight = (TextView) findViewById(R.id.txtWeight);
        txtTruckType = (TextView) findViewById(R.id.txtTruckType);
        txtLoadFltTruck = (TextView) findViewById(R.id.txtftl_ltl);
        txtTruckCharges = (TextView) findViewById(R.id.txtCharges);
        txtTruckDesc = (TextView) findViewById(R.id.txtTruckDesc);
        txtRating = (TextView) findViewById(R.id.txtRating);
        btnTruckCall = (Button) findViewById(R.id.btnTruckCall);
        btnTruckCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
        btnTruckRequest = (Button) findViewById(R.id.btnTruckRequest);
        btnTruckRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("POST_TRUCK_ID",post_truck_id );
                bundle.putString("NUMBER",contact_number);
                bundle.putString("TRUCK_ID",truck_id);
                bundle.putString("TRUCK_OWNER_ID",truck_owner_id);
                Intent intent = new Intent(TruckDetail.this, SelectLoad.class);
                intent.putExtra("BUNDLE", bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_comments:
                Bundle bundle = new Bundle();
                bundle.putString("POSTTRUCK_ID", truck_id);
                Intent intent = new Intent(TruckDetail.this, CommentList.class);
                intent.putExtra("BUNDLE", bundle);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.truck_detail, menu);
        return true;
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
                    Toast.makeText(TruckDetail.this, "CALL PERMISSION_GRANTED", Toast.LENGTH_SHORT);
                    callPermission = checkSelfPermission(android.Manifest.permission.CAMERA);
                    if (callPermission == 0) {
                        callOwner();
                    }
                } else {
                    Toast.makeText(TruckDetail.this, "CALL permission allows us to access phone state. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
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
