package com.waysideutilities.waysidetruckfreights.Cargo;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.waysideutilities.waysidetruckfreights.PojoClasses.Cargo;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import java.util.ArrayList;
import java.util.Date;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;
/**
 * Created by Archana on 2/3/2017.
 */
public class PostLoad extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView txtDate, txtTypeOfTruck, txtFLT, txtLoadCategory;
    private EditText edtxtLoadDesc, edtxtFrom, txtLandMark, txtCity, txtState, txtPin, txtWeight, edtxtMobile, txtDestinationPin, txtDestinationState, txtDestinationCity, txtDestinationLandMark, edtxtTo, edtxtAddress;
    private Button btnSubmit, btnClear;
    private AlertDialog dialog;
    private ListView listView;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_post_load);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.post_load);
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtDate.setText(FrightUtils.getFormattedDate(null, "dd/MM/yyyy", new Date(), null));
        txtDate.setOnClickListener(PostLoad.this);
        edtxtFrom = (EditText) findViewById(R.id.txtFrom);
        txtLandMark = (EditText) findViewById(R.id.txtLandMark);
        txtCity = (EditText) findViewById(R.id.txtCity);
        txtState = (EditText) findViewById(R.id.txtState);
        txtPin = (EditText) findViewById(R.id.txtPin);
        edtxtTo = (EditText) findViewById(R.id.txtTo);
        txtDestinationLandMark = (EditText) findViewById(R.id.txtDestinationLandMark);
        txtDestinationCity = (EditText) findViewById(R.id.txtDestinationCity);
        txtDestinationState = (EditText) findViewById(R.id.txtDestinationState);
        txtDestinationPin = (EditText) findViewById(R.id.txtDestinationPin);
        edtxtMobile = (EditText) findViewById(R.id.edtxtMobile);
        txtLoadCategory = (TextView) findViewById(R.id.txtLoadCategory);
        txtLoadCategory.setOnClickListener(PostLoad.this);
        txtFLT = (TextView) findViewById(R.id.txtFLT);
        txtFLT.setOnClickListener(PostLoad.this);
        txtWeight = (EditText) findViewById(R.id.txtWeight);
        txtTypeOfTruck = (TextView) findViewById(R.id.txtTypeOfTruck);
        txtTypeOfTruck.setOnClickListener(PostLoad.this);
        edtxtLoadDesc = (EditText) findViewById(R.id.edtxtLoadDesc);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(PostLoad.this);
        //btnClear = (Button) findViewById(R.id.btnClear);
        //btnClear.setOnClickListener(PostLoad.this);
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
                    txtDate.setText( String.format("%02d", dayOfMonth)+ "/" +String.format("%02d", (monthOfYear) + 1)   + "/" + String.format("%04d", year));
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
        if (view.getId() == R.id.txtTypeOfTruck) {
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
            if (txtDate.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_date, Toast.LENGTH_SHORT).show();
            } else if (edtxtFrom.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_from, Toast.LENGTH_SHORT).show();
            }else if (txtLandMark.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_landmark, Toast.LENGTH_SHORT).show();
            } else if (txtCity.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_city, Toast.LENGTH_SHORT).show();
            } else if (txtState.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_state, Toast.LENGTH_SHORT).show();
            } else if (txtPin.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_pin, Toast.LENGTH_SHORT).show();
            } else if (edtxtTo.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_from, Toast.LENGTH_SHORT).show();
            }else if (txtDestinationLandMark.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_landmark, Toast.LENGTH_SHORT).show();
            } else if (txtDestinationCity.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_city, Toast.LENGTH_SHORT).show();
            } else if (txtDestinationState.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_state, Toast.LENGTH_SHORT).show();
            } else if (txtDestinationPin.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_pin, Toast.LENGTH_SHORT).show();
            }else if (edtxtMobile.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_driver_number, Toast.LENGTH_SHORT).show();
            } else if (txtLoadCategory.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_load_category, Toast.LENGTH_SHORT).show();
            } else if (txtFLT.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_ftl_ltl, Toast.LENGTH_SHORT).show();
            } else if (txtWeight.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_weight, Toast.LENGTH_SHORT).show();
            } else if (txtTypeOfTruck.getText().toString().length() == 0) {
                Toast.makeText(PostLoad.this, R.string.err_msg_truck_type, Toast.LENGTH_SHORT).show();
            } else {
                Cargo cargo = new Cargo();
                cargo.setDate(FrightUtils.getFormattedDate( "dd/MM/yyyy","yyyy-MM-dd", txtDate.getText().toString(), null));
                //cargo.setFrom((edtxtFrom.getText().toString()).concat(",").concat(txtLandMark.getText().toString()).concat(",").concat(txtCity.getText().toString()).concat(",").concat(txtState.getText().toString()).concat(",").concat(txtPin.getText().toString()));
                //cargo.setTo((edtxtTo.getText().toString()).concat(",").concat(txtDestinationLandMark.getText().toString()).concat(",").concat(txtDestinationCity.getText().toString()).concat(",").concat(txtDestinationState.getText().toString()).concat(",").concat(txtDestinationPin.getText().toString()));

                cargo.setFrom_street_name(edtxtFrom.getText().toString());
                cargo.setFrom_landmark(txtLandMark.getText().toString());
                cargo.setFrom_city(txtCity.getText().toString());
                cargo.setFrom_state(txtState.getText().toString());
                cargo.setFrom_pincode(txtPin.getText().toString());

                cargo.setTo_street_name(edtxtTo.getText().toString());
                cargo.setTo_landmark(txtDestinationLandMark.getText().toString());
                cargo.setTo_city(txtDestinationCity.getText().toString());
                cargo.setTo_state(txtDestinationState.getText().toString());
                cargo.setTo_pincode(txtDestinationPin.getText().toString());


                cargo.setContactNumber(edtxtMobile.getText().toString());
                cargo.setLoad_Category(txtLoadCategory.getText().toString());
                cargo.setFtl_ltl(txtFLT.getText().toString());
                cargo.setWeight(txtWeight.getText().toString());
                cargo.setType_of_truck(txtTypeOfTruck.getText().toString());
                cargo.setLoad_description(edtxtLoadDesc.getText().toString());
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                new PostLoadAsyncTask(cargo, PostLoad.this).execute();
            }
        }
       /* if (view.getId() == R.id.btnClear) {
            edtxtFrom.setText("");
            edtxtTo.setText("");
            txtLoadCategory.setText("");
            txtFLT.setText("");
            txtWeight.setText("");
            txtFLT.setText("");
            txtTypeOfTruck.setText("");
            edtxtLoadDesc.setText("");
        }*/
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
                    showOtherPopUpTypeOfLoad(getResources().getString(R.string.load_category));
                } else {
                    txtLoadCategory.setText(selectedString);
                    dialog.dismiss();
                }
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
                    txtTypeOfTruck.setText(selectedString);
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
                    txtTypeOfTruck.setText(edtxtOthers.getText().toString());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
