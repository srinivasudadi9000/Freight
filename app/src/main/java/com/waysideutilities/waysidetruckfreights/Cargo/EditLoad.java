package com.waysideutilities.waysidetruckfreights.Cargo;

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
import com.waysideutilities.waysidetruckfreights.PojoClasses.Cargo;
import com.waysideutilities.waysidetruckfreights.Profile.MyLoads;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import java.util.ArrayList;
import java.util.Date;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;

/**
 * Created by Archana on 2/3/2017.
 */

public class EditLoad extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView txLoadtDate, txtTypeOfTruck, txtFLT, txtLoadCategory;
    private EditText edtxtLoadDesc, edtxtFrom, txtLandMark, txtCity, txtState, txtPin, txtWeight, edtxtMobile, txtDestinationPin, txtDestinationState, txtDestinationCity, txtDestinationLandMark, edtxtTo, edtxtAddress;
    private Button btnSubmit, btnClear;
    private AlertDialog dialog;
    private ListView listEditView;
    private String loadId, language;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_post_load);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.Edit_Load);
        Bundle bundle = getIntent().getBundleExtra("BUNDLE");
        setValueFromBundle(bundle);
    }

    private void setValueFromBundle(Bundle bundle) {
        if (bundle.getString("LOADID") != null)
            loadId = bundle.getString("LOADID");
        if (bundle.getString("DATE") != null)
            txLoadtDate.setText(FrightUtils.getFormattedDate("yyyy-MM-dd", "dd/MM/yyyy", bundle.getString("DATE"), null));

        if (bundle.getString("FROM") != null) {
            edtxtFrom.setText(bundle.getString("FROM"));
            /*String from[] = (bundle.getString("FROM").toString()).split(",");
            edtxtFrom.setText(from[0]);
            if (from[1] != null) {
                txtLandMark.setText(from[1]);
                txtCity.setText(from[2]);
                txtState.setText(from[3]);
                txtPin.setText(from[4]);
            }*/
        }
        if(bundle.getString("FROM_LANDMARK") != null)
            txtLandMark.setText(bundle.getString("FROM_LANDMARK"));
        if(bundle.getString("FROM_CITY") != null)
            txtCity.setText(bundle.getString("FROM_CITY"));
        if(bundle.getString("FROM_STATE") != null)
            txtState.setText(bundle.getString("FROM_STATE"));
        if(bundle.getString("FROM_PIN") != null)
            txtPin.setText(bundle.getString("FROM_PIN"));


        if (bundle.getString("TO") != null) {
            edtxtTo.setText(bundle.getString("TO"));
           /* String to[] = (bundle.getString("TO").toString()).split(",");
            edtxtFrom.setText(to[0]);
            txtDestinationLandMark.setText(to[1]);
            txtDestinationCity.setText(to[2]);
            txtDestinationState.setText(to[3]);
            txtDestinationPin.setText(to[4]);*/
        }
        if(bundle.getString("TO_LANDMARK") != null)
            txtDestinationLandMark.setText(bundle.getString("TO_LANDMARK"));
        if(bundle.getString("TO_CITY") != null)
            txtDestinationCity.setText(bundle.getString("TO_CITY"));
        if(bundle.getString("TO_STATE") != null)
            txtDestinationState.setText(bundle.getString("TO_STATE"));
        if(bundle.getString("TO_PIN") != null)
            txtDestinationPin.setText(bundle.getString("TO_PIN"));

        if (bundle.getString("CONTACT_NO") != null)
            edtxtMobile.setText(bundle.getString("CONTACT_NO"));
        if (bundle.getString("CATEGORY") != null)
            txtLoadCategory.setText(bundle.getString("CATEGORY"));
        if (bundle.getString("TRUCK_TYPE") != null)
            txtTypeOfTruck.setText(bundle.getString("TRUCK_TYPE"));
        if (bundle.getString("WEIGHT") != null)
            txtWeight.setText(bundle.getString("WEIGHT"));
        if (bundle.getString("FTL_LTL") != null)
            txtFLT.setText(bundle.getString("FTL_LTL"));
        if (bundle.getString("DESCRIPTION") != null)
            edtxtLoadDesc.setText(bundle.getString("DESCRIPTION"));

    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txLoadtDate = (TextView) findViewById(R.id.txtDate);
        txLoadtDate.setOnClickListener(EditLoad.this);
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
        txtLoadCategory.setOnClickListener(EditLoad.this);
        txtFLT = (TextView) findViewById(R.id.txtFLT);
        txtFLT.setOnClickListener(EditLoad.this);
        txtWeight = (EditText) findViewById(R.id.txtWeight);
        txtTypeOfTruck = (TextView) findViewById(R.id.txtTypeOfTruck);
        txtTypeOfTruck.setOnClickListener(EditLoad.this);
        edtxtLoadDesc = (EditText) findViewById(R.id.edtxtLoadDesc);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(EditLoad.this);
        //btnClear = (Button) findViewById(R.id.btnClear);
        //btnClear.setOnClickListener(EditLoad.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(EditLoad.this, MyLoads.class));
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
                    txLoadtDate.setText(String.format("%02d", dayOfMonth) + "/" + String.format("%02d", (monthOfYear) + 1) + "/" + String.format("%04d", year));
                }
            };
            String date[] = (txLoadtDate.getText().toString()).split("/");
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
            if (txLoadtDate.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_date, Toast.LENGTH_SHORT).show();
            } else if (edtxtFrom.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_from, Toast.LENGTH_SHORT).show();
            } else if (edtxtTo.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_from, Toast.LENGTH_SHORT).show();
            } else if (edtxtFrom.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_from, Toast.LENGTH_SHORT).show();
            } else if (txtLandMark.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_landmark, Toast.LENGTH_SHORT).show();
            } else if (txtCity.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_city, Toast.LENGTH_SHORT).show();
            } else if (txtState.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_state, Toast.LENGTH_SHORT).show();
            } else if (txtPin.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_pin, Toast.LENGTH_SHORT).show();
            } else if (edtxtTo.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_from, Toast.LENGTH_SHORT).show();
            } else if (txtDestinationLandMark.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_landmark, Toast.LENGTH_SHORT).show();
            } else if (txtDestinationCity.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_city, Toast.LENGTH_SHORT).show();
            } else if (txtDestinationCity.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_state, Toast.LENGTH_SHORT).show();
            } else if (txtDestinationPin.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_pin, Toast.LENGTH_SHORT).show();
            } else if (edtxtMobile.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_driver_number, Toast.LENGTH_SHORT).show();
            } else if (txtLoadCategory.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_load_category, Toast.LENGTH_SHORT).show();
            } else if (txtFLT.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_ftl_ltl, Toast.LENGTH_SHORT).show();
            } else if (txtWeight.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_weight, Toast.LENGTH_SHORT).show();
            } else if (txtTypeOfTruck.getText().toString().length() == 0) {
                Toast.makeText(EditLoad.this, R.string.err_msg_truck_type, Toast.LENGTH_SHORT).show();
            } else {
                Cargo cargo = new Cargo();
                cargo.setId(loadId);
                cargo.setDate(FrightUtils.getFormattedDate("dd/MM/yyyy", "yyyy-MM-dd", txLoadtDate.getText().toString(), null));
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
                new EditLoadAsyncTask(cargo, EditLoad.this).execute();
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
        listEditView = (ListView) convertView.findViewById(R.id.listView);
        listEditView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
        alertBuilder.setView(convertView);
        listEditView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        listEditView = (ListView) convertView.findViewById(R.id.listView);

        listEditView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
        alertBuilder.setView(convertView);
        listEditView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedString = arrayList.get(position);
                if (selectedString.equals("OTHERS")) {
                    dialog.dismiss();
                    showOtherPopUpTypeOfLoad("Load category");
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
        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edtxtOthers.getText().toString().length() != 0) {
                    txtLoadCategory.setText(edtxtOthers.getText().toString());
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    ArrayList<String> arrayList = new ArrayList<String>();
                    arrayList.add("Chemical");
                    arrayList.add("Food & Agri products");
                    arrayList.add("Refrigerated products");
                    arrayList.add("COD");
                    arrayList.add("OTHERS");
                    showPopUpOfLoadCategory(arrayList, "Load category");
                }
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        listEditView = (ListView) convertView.findViewById(R.id.listView);
        listEditView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
        alertBuilder.setView(convertView);
        listEditView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedString = arrayList.get(position);
                if (selectedString.equals("OTHERS")) {
                    dialog.dismiss();
                    showOtherPopUpTypeOfTruck("Type of truck");
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
        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edtxtOthers.getText().toString().length() != 0) {
                    txtTypeOfTruck.setText(edtxtOthers.getText().toString());
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    ArrayList<String> arrayList = new ArrayList<String>();
                    arrayList.add("Container");
                    arrayList.add("Cold truck");
                    arrayList.add("Closed body");
                    arrayList.add("Open body");
                    arrayList.add("Trailer");
                    arrayList.add("OTHERS");
                    showPopUp(arrayList, "Type of truck");
                }
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
