package com.waysideutilities.waysidetruckfreights;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.Cargo.Cargo_Provider;
import com.waysideutilities.waysidetruckfreights.Owner.Truck_Owner;
import com.waysideutilities.waysidetruckfreights.PojoClasses.User;

import java.util.Arrays;
import java.util.List;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;

public class Verify extends BaseActivity implements View.OnClickListener {
    private EditText edEmail, edOPT;
    private Button btnVerify;
    private ListView languageListView;
    private AlertDialog dialog;
    private String userType, otp, success, mobile, email, random_otp = null;
    private TextView txtEmail, txtMessage,txtResendOTP;
    private String language;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);

        init();
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        userType = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERTYPE", null);
        otp = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("OTP", null);
        random_otp = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("RANDOM_OTP", null);
        success = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("SUCCESS", null);
        mobile = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("NUMBER", null);
        email = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("EMAIL", null);
        if (mobile != null) {
            txtEmail.setText(mobile);
            txtMessage.setText(R.string.num_OTP);
        } else {
            txtEmail.setText(email);
            txtMessage.setText(R.string.email_OTP);
        }
    }

    private void init() {
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        edEmail = (EditText) findViewById(R.id.edEmail);
        txtMessage = (TextView) findViewById(R.id.txtMessage);
        edOPT = (EditText) findViewById(R.id.edOPT);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(Verify.this);
        txtResendOTP = (TextView)findViewById(R.id.txtResendOTP);
        txtResendOTP.setOnClickListener(Verify.this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnVerify) {
            if (otp != null) {
                if (otp.equals(edOPT.getText().toString())) {
                    editor.putString("VERIFIED", "1");
                    editor.commit();
                    if (language == null) {
                        showPopUp();
                    } else {
                        if (userType.equals("Cargo Provider")) {
                            startActivity(new Intent(Verify.this, Cargo_Provider.class));
                            finish();
                        } else if (userType.equals("Truck Owner")) {
                            startActivity(new Intent(Verify.this, Truck_Owner.class));
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(this, R.string.err_msg_correct_otp, Toast.LENGTH_SHORT).show();
                }
            } else {
                if (random_otp.equals(edOPT.getText().toString())) {
                    editor.putString("VERIFIED", "1");
                    editor.commit();
                    if (language == null) {
                        showPopUp();
                    } else {
                        if(userType != null) {
                            if (userType.equals("Cargo Provider")) {
                                startActivity(new Intent(Verify.this, Cargo_Provider.class));
                                finish();
                            } else if (userType.equals("Truck Owner")) {
                                startActivity(new Intent(Verify.this, Truck_Owner.class));
                                finish();
                            }
                        }else{
                            startActivity(new Intent(Verify.this, SignUp.class));
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(this, R.string.err_msg_correct_otp, Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(v.getId() == R.id.txtResendOTP){
            User user = new User();
            user.setContact_number(mobile);
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
            new UserSignInWithMobile(user, Verify.this).execute();
        }
    }

    private void showPopUp() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View titleView = inflater.inflate(R.layout.layout_title, null);
        alertBuilder.setCustomTitle(titleView);
        TextView title = (TextView) titleView.findViewById(R.id.title);
        title.setText(R.string.select_language);
        View convertView = inflater.inflate(R.layout.pop_up_list_view, null);
        languageListView = (ListView) convertView.findViewById(R.id.listView);
        final List<String> arrayList = Arrays.asList(getResources().getStringArray(R.array.language));

        languageListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
        alertBuilder.setView(convertView);
        languageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedString = arrayList.get(position);
                if (selectedString.equals(getResources().getString(R.string.english))) {
                    editor.putString("LANGUAGE", getResources().getString(R.string.english));
                    editor.commit();
                } else if (selectedString.equals(getResources().getString(R.string.bengali))) {
                    editor.putString("LANGUAGE", getResources().getString(R.string.bengali));
                    editor.commit();
                } else if (selectedString.equals(getResources().getString(R.string.gujarati))) {
                    editor.putString("LANGUAGE", getResources().getString(R.string.gujarati));
                    editor.commit();
                } else if (selectedString.equals(getResources().getString(R.string.hindi))) {
                    editor.putString("LANGUAGE", getResources().getString(R.string.hindi));
                    editor.commit();
                } else if (selectedString.equals(getResources().getString(R.string.kannada))) {
                    editor.putString("LANGUAGE", getResources().getString(R.string.kannada));
                    editor.commit();
                } else if (selectedString.equals(getResources().getString(R.string.malayalam))) {
                    editor.putString("LANGUAGE", getResources().getString(R.string.malayalam));
                    editor.commit();
                } else if (selectedString.equals(getResources().getString(R.string.punjabi))) {
                    editor.putString("LANGUAGE", getResources().getString(R.string.punjabi));
                    editor.commit();
                } else if (selectedString.equals(getResources().getString(R.string.telugu))) {
                    editor.putString("LANGUAGE", getResources().getString(R.string.telugu));
                    editor.commit();
                } else if (selectedString.equals(getResources().getString(R.string.tamil))) {
                    editor.putString("LANGUAGE", getResources().getString(R.string.tamil));
                    editor.commit();
                }
                dialog.dismiss();
                if (success.equals("1")) {
                    if (userType.equals("Cargo Provider")) {
                        startActivity(new Intent(Verify.this, Cargo_Provider.class));
                        finish();
                    } else if (userType.equals("Truck Owner")) {
                        startActivity(new Intent(Verify.this, Truck_Owner.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(Verify.this, SignUp.class));
                    finish();
                }

            }
        });
        dialog = alertBuilder.create();
        dialog.show();
    }

}
