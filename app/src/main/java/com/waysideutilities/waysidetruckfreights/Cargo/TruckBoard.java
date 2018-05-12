package com.waysideutilities.waysidetruckfreights.Cargo;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.waysideutilities.waysidetruckfreights.BaseActivity;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Truck;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import java.util.Date;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;
/**
 * Created by Archana on 2/3/2017.
 */
public class TruckBoard extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView txtDate, txtFrom, txtTo, txtSearchTruck, txtClear;
    private ListView listTruckBoard;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_truck_board);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.truck_board);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
        new GetTruckList(TruckBoard.this, listTruckBoard).execute();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtDate.setText(FrightUtils.getFormattedDate(null, "dd/MM/yyyy", new Date(), null));
        txtDate.setOnClickListener(TruckBoard.this);
        txtFrom = (TextView) findViewById(R.id.txtFrom);
        txtTo = (TextView) findViewById(R.id.txtTo);
        listTruckBoard = (ListView) findViewById(R.id.listTruckBoard);
        txtSearchTruck = (TextView) findViewById(R.id.txtSearch);
        txtSearchTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((txtFrom.getText().toString().length() != 0) && (txtTo.getText().toString().length() != 0)) {
                    Truck truck = new Truck();
                    truck.setDate(FrightUtils.getFormattedDate("dd/MM/yyyy", "yyyy-MM-dd", txtDate.getText().toString(), null));
                    truck.setFrom(txtFrom.getText().toString());
                    truck.setTo(txtTo.getText().toString());
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                    new SearchTruckList(TruckBoard.this, truck, listTruckBoard).execute();
                }
            }
        });
        txtClear = (TextView) findViewById(R.id.txtClear);
        txtClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtFrom.setText("");
                txtTo.setText("");
            }
        });
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
    public void onClick(View view) {
        if (view.getId() == R.id.txtDate) {
            DatePickerDialog.OnDateSetListener callBack = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    txtDate.setText(String.format("%02d", dayOfMonth) + "/" + String.format("%02d", (monthOfYear) + 1) + "/" + String.format("%04d", year));
                }
            };
            String date[] = (txtDate.getText().toString()).split("/");
            int year = Integer.parseInt(date[2]);
            int month = Integer.parseInt(date[1]) - 1;
            int day = Integer.parseInt(date[0]);
            DatePickerDialog datePicker = new DatePickerDialog(this, callBack, year, month, day);
            datePicker.getDatePicker().setMinDate(new Date().getTime() - 10000);
            datePicker.show();
        }
    }
}
