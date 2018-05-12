package com.waysideutilities.waysidetruckfreights.Owner;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.BaseActivity;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Truck;
import com.waysideutilities.waysidetruckfreights.Profile.MyTrucks;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import java.util.ArrayList;
import java.util.Date;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.*;
/**
 * Created by Archana on 8/29/2016.
 */

public class EditTruck extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private AlertDialog dialog;
    public TextView txt_truck_Date, txtTruckType, txtChargeUnit, txtDriverName, txtTo, txtFrom, txtLoadCategory, txtFLT;
    private EditText edtxtCharge, edtxtTruckDesc, txtLoadCapacity;
    private Button btnCancel, btnSubmit;
    private String truckId, language, book_status, trip_status, post_truckId;
    private ListView listView, listEditView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_layout_edit_truck);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_truck);
        Bundle bundle = getIntent().getBundleExtra("BUNDLE");
        setValueFromBundle(bundle);
    }

    private void setValueFromBundle(Bundle bundle) {
        if (bundle.getString("POST_TRUCKID") != null)
            post_truckId = bundle.getString("POST_TRUCKID");
        if (bundle.getString("TRUCKID") != null)
            truckId = bundle.getString("TRUCKID");
        if (bundle.getString("DATE") != null)
            txt_truck_Date.setText(FrightUtils.getFormattedDate("yyyy-MM-dd", "dd/MM/yyyy", bundle.getString("DATE"), null));
        if (bundle.getString("FROM") != null)
            txtFrom.setText(bundle.getString("FROM"));
        if (bundle.getString("TO") != null)
            txtTo.setText(bundle.getString("TO"));
        if (bundle.getString("CATEGORY") != null)
            txtLoadCategory.setText(bundle.getString("CATEGORY"));
        if (bundle.getString("TRUCK_TYPE") != null)
            txtTruckType.setText(bundle.getString("TRUCK_TYPE"));
        if (bundle.getString("FTL_LTL") != null)
            txtFLT.setText(bundle.getString("FTL_LTL"));
        if (bundle.getString("LOAD_CAPACITY") != null)
            txtLoadCapacity.setText(bundle.getString("LOAD_CAPACITY"));
        if (bundle.getString("CHARGE") != null) {
            String charges[] = bundle.getString("CHARGE").split("#");
            edtxtCharge.setText(charges[0]);
            txtChargeUnit.setText(charges[1]);
        }
        if (bundle.getString("DESCRIPTION") != null)
            edtxtTruckDesc.setText(bundle.getString("DESCRIPTION"));
        if (bundle.getString("BOOK_STATUS") != null)
            book_status = bundle.getString("BOOK_STATUS");
        if (bundle.getString("TRIP_STATUS") != null)
            trip_status = bundle.getString("TRIP_STATUS");
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txt_truck_Date = (TextView) findViewById(R.id.txt_truck_Date);
        txt_truck_Date.setText(FrightUtils.getFormattedDate(null, "dd/MM/yyyy", new Date(), null));
        txt_truck_Date.setOnClickListener(EditTruck.this);
        txtFrom = (EditText) findViewById(R.id.txtFrom);
        txtTo = (EditText) findViewById(R.id.txtTo);
        txtTruckType = (TextView) findViewById(R.id.txtTruckType);
        txtTruckType.setOnClickListener(EditTruck.this);
        txtFLT = (TextView) findViewById(R.id.txtFLT);
        txtFLT.setOnClickListener(EditTruck.this);
        txtLoadCapacity = (EditText) findViewById(R.id.txtLoadCapacity);
        txtLoadCategory = (TextView) findViewById(R.id.txtLoadCategory);
        txtLoadCategory.setOnClickListener(EditTruck.this);
        edtxtCharge = (EditText) findViewById(R.id.edtxtCharge);
        edtxtTruckDesc = (EditText) findViewById(R.id.edtxtTruckDesc);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(EditTruck.this);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(EditTruck.this);
        txtChargeUnit = (TextView) findViewById(R.id.txtChargeUnit);
        txtChargeUnit.setOnClickListener(EditTruck.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(EditTruck.this, MyTrucks.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPopUpOfLoadCategory(final ArrayList<String> arrayList, String title) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View titleView = inflater.inflate(R.layout.layout_title, null);
        alertBuilder.setCustomTitle(titleView);
        TextView txtTitle = (TextView) titleView.findViewById(R.id.title);
        txtTitle.setText(title);
        View convertView = inflater.inflate(R.layout.pop_up_list_view, null);
        listView = (ListView) convertView.findViewById(R.id.listView);

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
        alertBuilder.setView(convertView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedString = arrayList.get(position);
                if (selectedString.equals(getResources().getString(R.string.others))) {
                    dialog.dismiss();
                    showOtherPopUpTypeOfLoad(getResources().getString(R.string.type_of_load));
                } else {
                    txtLoadCategory.setText(selectedString);
                    dialog.dismiss();
                }
            }
        });
        dialog = alertBuilder.create();
        dialog.show();
    }

    private void showOtherPopUpTypeOfTruck(String title) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View titleView = inflater.inflate(R.layout.layout_title, null);
        alertBuilder.setCustomTitle(titleView);
        TextView txtTitle = (TextView) titleView.findViewById(R.id.title);
        txtTitle.setText(title);
        View convertView = inflater.inflate(R.layout.others_pop_up_view, null);
        final EditText edtxtOthers = (EditText) convertView.findViewById(R.id.edtxtOthers);
        alertBuilder.setView(convertView);
        alertBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edtxtOthers.getText().toString().length() != 0) {
                    txtTruckType.setText(edtxtOthers.getText().toString());
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    ArrayList<String> arrayList = new ArrayList<String>();
                    arrayList.add(getResources().getString(R.string.container));
                    arrayList.add(getResources().getString(R.string.cold_truck));
                    arrayList.add(getResources().getString(R.string.open_body));
                    arrayList.add(getResources().getString(R.string.closed_body));
                    arrayList.add(getResources().getString(R.string.trailers));
                    arrayList.add(getResources().getString(R.string.others));
                    showPopUp(arrayList, getResources().getString(R.string.type_of_truck));
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

    private void showPopUp(final ArrayList<String> arrayList, String title) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View titleView = inflater.inflate(R.layout.layout_title, null);
        alertBuilder.setCustomTitle(titleView);
        TextView txtTitle = (TextView) titleView.findViewById(R.id.title);
        txtTitle.setText(title);
        View convertView = inflater.inflate(R.layout.pop_up_list_view, null);
        listView = (ListView) convertView.findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
        alertBuilder.setView(convertView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedString = arrayList.get(position);
                if (selectedString.equals(getResources().getString(R.string.others))) {
                    dialog.dismiss();
                    showOtherPopUpTypeOfTruck(getResources().getString(R.string.type_of_truck));
                } else {
                    txtTruckType.setText(selectedString);
                    dialog.dismiss();
                }
            }
        });
        dialog = alertBuilder.create();
        dialog.show();
    }

    private void showPopFLT(final ArrayList<String> arrayList, String title) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.pop_up_list_view, null);
        listView = (ListView) convertView.findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
        alertBuilder.setView(convertView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedString = arrayList.get(position);
                txtFLT.setText(selectedString);
                dialog.dismiss();
            }
        });
        dialog = alertBuilder.create();
        dialog.show();
    }

    private void showOtherPopUpTypeOfLoad(String title) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View titleView = inflater.inflate(R.layout.layout_title, null);
        alertBuilder.setCustomTitle(titleView);
        TextView txtTitle = (TextView) titleView.findViewById(R.id.title);
        txtTitle.setText(title);
        View convertView = inflater.inflate(R.layout.others_pop_up_view, null);
        final EditText edtxtOthers = (EditText) convertView.findViewById(R.id.edtxtOthers);
        alertBuilder.setView(convertView);
        alertBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edtxtOthers.getText().toString().length() != 0) {
                    txtLoadCategory.setText(edtxtOthers.getText().toString());
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    ArrayList<String> arrayList = new ArrayList<String>();
                    arrayList.add(getResources().getString(R.string.chemical));
                    arrayList.add(getResources().getString(R.string.food_agri));
                    arrayList.add(getResources().getString(R.string.refrigrated));
                    arrayList.add(getResources().getString(R.string.cod));
                    arrayList.add(getResources().getString(R.string.others));
                    showPopUpOfLoadCategory(arrayList, getResources().getString(R.string.load_category));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txt_truck_Date) {
            DatePickerDialog.OnDateSetListener callBack = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    txt_truck_Date.setText(String.format("%02d", dayOfMonth) + "/" + String.format("%02d", (monthOfYear) + 1) + "/" + String.format("%04d", year));
                }
            };
            String date[] = (txt_truck_Date.getText().toString()).split("/");
            int year = Integer.parseInt(date[2]);
            int month = Integer.parseInt(date[1]) - 1;
            int day = Integer.parseInt(date[0]);
            DatePickerDialog datePicker = new DatePickerDialog(this, callBack, year, month, day);
            datePicker.getDatePicker().setMinDate(new Date().getTime() - 10000);
            datePicker.show();
        }

        if (view.getId() == R.id.txtChargeUnit) {
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(getResources().getString(R.string.per_km));
            arrayList.add(getResources().getString(R.string.per_ton));
            arrayList.add(getResources().getString(R.string.point_to_point));
            showPopCharges(arrayList, getResources().getString(R.string.select_unit));
        }
        if (view.getId() == R.id.btnCancel) {
            finish();
        }
        if (view.getId() == R.id.txtTruckType) {
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(getResources().getString(R.string.container));
            arrayList.add(getResources().getString(R.string.cold_truck));
            arrayList.add(getResources().getString(R.string.closed_body));
            arrayList.add(getResources().getString(R.string.open_body));
            arrayList.add(getResources().getString(R.string.trailers));
            arrayList.add(getResources().getString(R.string.others));
            showPopUp(arrayList, getResources().getString(R.string.type_of_truck));
        }
        if (view.getId() == R.id.btnSubmit) {
            if (txt_truck_Date.getText().toString().length() == 0) {
                Toast.makeText(EditTruck.this, R.string.err_msg_date, Toast.LENGTH_SHORT).show();
            } else if (txtFrom.getText().toString().length() == 0) {
                Toast.makeText(EditTruck.this, R.string.err_msg_from, Toast.LENGTH_SHORT).show();
            } else if (txtTo.getText().toString().length() == 0) {
                Toast.makeText(EditTruck.this, R.string.err_msg_from, Toast.LENGTH_SHORT).show();
            } else if (txtFrom.getText().toString().equals(txtTo.getText().toString())) {
                Toast.makeText(EditTruck.this, R.string.err_msg_loading_unloding_point_differance, Toast.LENGTH_SHORT).show();
            } else if (txtLoadCategory.getText().toString().length() == 0) {
                Toast.makeText(EditTruck.this, R.string.err_msg_load_category, Toast.LENGTH_SHORT).show();
            } else if (txtTruckType.getText().toString().length() == 0) {
                Toast.makeText(EditTruck.this, R.string.err_msg_truck_type, Toast.LENGTH_SHORT).show();
            } else if (txtFLT.getText().toString().length() == 0) {
                Toast.makeText(EditTruck.this, R.string.err_msg_ftl_ltl, Toast.LENGTH_SHORT).show();
            } else if (txtLoadCapacity.getText().toString().length() == 0) {
                Toast.makeText(EditTruck.this, R.string.err_msg_load_capacity, Toast.LENGTH_SHORT).show();
            } else if (edtxtCharge.getText().toString().length() == 0) {
                Toast.makeText(EditTruck.this, R.string.err_msg_charges, Toast.LENGTH_SHORT).show();
            } else {
                Truck truck = new Truck();
                truck.setId(truckId);
                truck.setPost_truck_id(post_truckId);
                truck.setDate(FrightUtils.getFormattedDate("dd/MM/yyyy", "yyyy-MM-dd", txt_truck_Date.getText().toString(), null));
                truck.setFrom(txtFrom.getText().toString());
                truck.setTo(txtTo.getText().toString());
                truck.setType_of_truck(txtTruckType.getText().toString());
                truck.setFtl_ltl(txtFLT.getText().toString());
                truck.setLoad_Category(txtLoadCategory.getText().toString());
                truck.setLoad_Capacity(txtLoadCapacity.getText().toString());
                truck.setBook_status("0");
                truck.setTrip_status("0");
                truck.setBooked_by_id("0");
                truck.setCharges((edtxtCharge.getText().toString()).concat("#").concat(txtChargeUnit.getText().toString()));
                truck.setLoad_description(edtxtTruckDesc.getText().toString());

                if (book_status.equals("1") && trip_status.equals("2")) {
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                    new AddTruck(truck, EditTruck.this).execute();
                } else {
                    truck.setPost_truck_id(post_truckId);
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                    new EditTruckAsyncTask(truck, EditTruck.this).execute();
                }
            }
        }

        if (view.getId() == R.id.txtFLT) {
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(getResources().getString(R.string.f_tl));
            arrayList.add(getResources().getString(R.string.l_tl));
            showPopFLT(arrayList, " ");

        }
        if (view.getId() == R.id.txtLoadCategory) {
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(getResources().getString(R.string.chemical));
            arrayList.add(getResources().getString(R.string.food_agri));
            arrayList.add(getResources().getString(R.string.refrigrated));
            arrayList.add(getResources().getString(R.string.cod));
            arrayList.add(getResources().getString(R.string.others));
            showPopUpOfLoadCategory(arrayList, getResources().getString(R.string.load_category));
        }
    }

    private void showPopCharges(final ArrayList<String> arrayList, String title) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View titleView = inflater.inflate(R.layout.layout_title, null);
        alertBuilder.setCustomTitle(titleView);
        TextView txtTitle = (TextView) titleView.findViewById(R.id.title);
        txtTitle.setText(title);
        View convertView = inflater.inflate(R.layout.pop_up_list_view, null);
        listView = (ListView) convertView.findViewById(R.id.listView);

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
        alertBuilder.setView(convertView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedString = arrayList.get(position);
                txtChargeUnit.setText(selectedString);
                dialog.dismiss();
            }
        });
        dialog = alertBuilder.create();
        dialog.show();
    }
}
