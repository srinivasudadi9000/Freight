package com.waysideutilities.waysidetruckfreights.Owner;

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
import com.waysideutilities.waysidetruckfreights.PojoClasses.Cargo;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import java.util.Date;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;
/**
 * Created by Archana on 8/29/2016.
 */
public class LoadBoard extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView txtDate, txtFrom, txtTo, txtSearch, txtClear;
    private ListView listLoadBoard;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_load_board);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.load_board);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
        new GetLoadList(LoadBoard.this, listLoadBoard).execute();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtDate.setText(FrightUtils.getFormattedDate(null, "dd/MM/yyyy", new Date(), null));
        txtDate.setOnClickListener(LoadBoard.this);
        txtFrom = (TextView) findViewById(R.id.txtFrom);
        txtTo = (TextView) findViewById(R.id.txtTo);
        listLoadBoard = (ListView) findViewById(R.id.listLoadBoard);
        txtSearch = (TextView) findViewById(R.id.txtSearch);
        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((txtFrom.getText().toString().length() != 0) && (txtTo.getText().toString().length() != 0)) {
                    Cargo cargo = new Cargo();
                    cargo.setDate(FrightUtils.getFormattedDate("dd/MM/yyyy", "yyyy-MM-dd", txtDate.getText().toString(), null));
                    cargo.setFrom_city(txtFrom.getText().toString());
                    cargo.setTo_city(txtTo.getText().toString());
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                    new SearchLoadList(LoadBoard.this, cargo, listLoadBoard).execute();
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
