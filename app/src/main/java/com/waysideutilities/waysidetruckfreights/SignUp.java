package com.waysideutilities.waysidetruckfreights;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.PojoClasses.User;
import com.waysideutilities.waysidetruckfreights.Profile.PrivacyPolicy;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;

public class SignUp extends BaseActivity implements View.OnClickListener {

    private EditText edtxtEmail, edtxtName, edtxtMobile, edtxtCity, edtxtEmergencyNumber, edPassword, edConfmPassword;
    private Button btnDone;
    private ListView listView;
    private TextView edtxtwho_u_r, termsandcondition;
    private AlertDialog dialog;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    private String email, language, mobile;
    private ImageView imagetick;
    private boolean isChecked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        FrightUtils.SetChoosenLanguage(language, SignUp.this);
        setContentView(R.layout.activity_sign_up);
        init();
        email = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("EMAIL", null);
        mobile = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("NUMBER", null);
        if (mobile != null)
            edtxtMobile.setText(mobile);
        if (email != null)
            edtxtEmail.setText(email);
    }

    private void init() {
        edtxtEmail = (EditText) findViewById(R.id.edtxtEmail);
        edtxtName = (EditText) findViewById(R.id.edtxtName);
        edtxtMobile = (EditText) findViewById(R.id.edtxtMobile);
        edtxtEmergencyNumber = (EditText) findViewById(R.id.edtxtEmergencyNumber);
        edtxtCity = (EditText) findViewById(R.id.edtxtCity);
        edtxtwho_u_r = (TextView) findViewById(R.id.edtxtwho_u_r);
        edtxtwho_u_r.setOnClickListener(SignUp.this);
        btnDone = (Button) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(SignUp.this);
        termsandcondition = (TextView) findViewById(R.id.termsandcondition);
        termsandcondition.setOnClickListener(SignUp.this);
        imagetick = (ImageView) findViewById(R.id.imagetick);
        imagetick.setOnClickListener(SignUp.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnDone) {
            if (!validateEmail(edtxtEmail.getText().toString())) {
                edtxtEmail.setError(getResources().getString(R.string.err_msg_email));
                edtxtEmail.setFocusable(true);
              //  Toast.makeText(SignUp.this, R.string.err_msg_email, Toast.LENGTH_SHORT).show();
            } else if (edtxtName.getText().toString().length() == 0) {
                edtxtName.setError(getResources().getString(R.string.err_msg_name));
                edtxtName.setFocusable(true);
             //   Toast.makeText(SignUp.this, R.string.err_msg_name, Toast.LENGTH_SHORT).show();
            } else if (edtxtMobile.getText().toString().length() != 10) {
                edtxtMobile.setError(getResources().getString(R.string.err_msg_mob));
                edtxtMobile.setFocusable(true);
              //  Toast.makeText(SignUp.this, R.string.err_msg_mob, Toast.LENGTH_SHORT).show();
            } else if (edtxtEmergencyNumber.getText().toString().length() != 10) {
                edtxtEmergencyNumber.setError(getResources().getString(R.string.err_msg_emg_mob));
                edtxtEmergencyNumber.setFocusable(true);
               // Toast.makeText(SignUp.this, R.string.err_msg_emg_mob, Toast.LENGTH_SHORT).show();
            } else if (edtxtCity.getText().toString().length() == 0) {
                edtxtCity.setError(getResources().getString(R.string.err_msg_city));
                edtxtCity.setFocusable(true);
               // Toast.makeText(SignUp.this, R.string.err_msg_city, Toast.LENGTH_SHORT).show();
            } else if (edtxtwho_u_r.getText().toString().length() == 0) {
               // edtxtwho_u_r.setError(getResources().getString(R.string.err_msg_who_u_r));
               /// edtxtwho_u_r.setFocusable(true);
                 Toast.makeText(SignUp.this, R.string.err_msg_who_u_r, Toast.LENGTH_SHORT).show();
            } else if (!isChecked) {
               // edtxtEmail.setError(getResources().getString(R.string.err_agree_terms));
                 Toast.makeText(SignUp.this, R.string.err_agree_terms, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    User user = new User();
                    user.setEmail(edtxtEmail.getText().toString());
                    user.setUserName(edtxtName.getText().toString());
                    user.setContact_number(edtxtMobile.getText().toString());
                    user.setEmg_contact_number(edtxtEmergencyNumber.getText().toString());
                    user.setCity(edtxtCity.getText().toString());
                    user.setWho_u_r(edtxtwho_u_r.getText().toString());
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                    new SignUpAsyncTask(user, SignUp.this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (view.getId() == R.id.edtxtwho_u_r) {
            try {
                showPopUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (view.getId() == R.id.imagetick) {
            if (!isChecked) {
                isChecked = true;
                imagetick.setBackgroundResource(R.mipmap.checkbox_tick);
            } else {
                isChecked = false;
                imagetick.setBackgroundResource(R.mipmap.uncheckbox);
            }
        }
        if (view.getId() == R.id.termsandcondition) {
            startActivity(new Intent(SignUp.this, PrivacyPolicy.class));
        }
    }

    private boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void showPopUp() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View titleView = inflater.inflate(R.layout.layout_title, null);
        alertBuilder.setCustomTitle(titleView);
        TextView title =  titleView.findViewById(R.id.title);
        title.setText(R.string.who_u_r);
        View convertView = inflater.inflate(R.layout.pop_up_list_view, null);
        listView =  convertView.findViewById(R.id.listView);
        final ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("Cargo Provider");
        arrayList.add("Truck Owner");
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
        alertBuilder.setView(convertView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedString = arrayList.get(position);
                edtxtwho_u_r.setText(selectedString);
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

