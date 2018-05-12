package com.waysideutilities.waysidetruckfreights;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.PojoClasses.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignIn extends BaseActivity implements View.OnClickListener {

    private EditText edEmail, edMobNumber;
    private Button btnSignIn;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    private TextView txtCreateAccount, txtForgotPassword;
    private EditText input_interest_email;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
    }

    private void init() {
        edEmail = (EditText) findViewById(R.id.edEmail);
        edMobNumber = (EditText) findViewById(R.id.edMobNumber);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(SignIn.this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btnSignIn) {
            User user;
            if ((edEmail.getText().toString().length() == 0) && (edMobNumber.getText().toString().length() == 0)) {
                 edMobNumber.setError(getResources().getString(R.string.err_msg_sign_in));
                //Toast.makeText(SignIn.this, R.string.err_msg_sign, Toast.LENGTH_SHORT).show();
            } else if ((edEmail.getText().toString().length() != 0) && (edMobNumber.getText().toString().length() != 0)) {
                edMobNumber.setError(getResources().getString(R.string.err_msg_sign_in));
              //  Toast.makeText(SignIn.this, R.string.err_msg_sign_in, Toast.LENGTH_SHORT).show();
            } else {
                if (edEmail.getText().toString().length() != 0) {
                    if (!validateEmail(edEmail.getText().toString())) {
                        edEmail.setError(getResources().getString(R.string.err_msg_email));
                       // Toast.makeText(SignIn.this, R.string.err_msg_email, Toast.LENGTH_SHORT).show();
                    } else {
                        user = new User();
                        user.setEmail(edEmail.getText().toString());
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new UserSignIn(user, SignIn.this).execute();
                    }
                } else {
                    if (edMobNumber.getText().toString().length() != 10) {
                        edMobNumber.setError(getResources().getString(R.string.err_msg_mob));
                     //   Toast.makeText(SignIn.this, R.string.err_msg_mob, Toast.LENGTH_SHORT).show();
                    } else {
                        user = new User();
                        user.setContact_number(edMobNumber.getText().toString());
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new UserSignInWithMobile(user, SignIn.this).execute();
                    }
                }
            }
        }
    }

 /*   public void showAlert() {
        AlertDialog.Builder dilogBuilder = new AlertDialog.Builder(SignIn.this);
        dilogBuilder.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        View convertView = inflater.inflate(R.layout.forget_password_layout, null);
        input_interest_email = (EditText) convertView.findViewById(R.id.input_interest_email);
        dilogBuilder.setView(convertView);
        dilogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dilogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog = dilogBuilder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new CustomListener(dialog));
    }*/

    private boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

  /*  private class CustomListener implements View.OnClickListener {
        private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
        private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        private Matcher matcher;
        private Dialog dialog;

        public CustomListener(AlertDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onClick(View view) {
            if (!validateEmail(input_interest_email.getText().toString())) {
                Toast.makeText(SignIn.this, R.string.err_msg_email, Toast.LENGTH_SHORT);
            } else {
                dialog.dismiss();
                if (FrightUtils.hasActiveInternetConnection(SignIn.this)) {
                    User user = new User();
                    user.setEmail(input_interest_email.getText().toString());
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                    new ForgotPassword(user, SignIn.this).execute();
                } else {
                    Toast.makeText(SignIn.this, R.string.networkError, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
 /* if (!validateEmail(edEmail.getText().toString())) {
                Toast.makeText(SignIn.this, R.string.err_msg_email, Toast.LENGTH_SHORT).show();
            } else if (edPassword.getText().toString().length() == 0) {
                Toast.makeText(SignIn.this, R.string.err_msg_password, Toast.LENGTH_SHORT).show();
            } else {
                User user = new User();
                user.setEmail(edEmail.getText().toString());
                user.setPassword(edPassword.getText().toString());
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                new UserSignIn(user,SignIn.this).execute();
            }*/
 /* if (view.getId() == R.id.txtCreateAccount) {
            startActivity(new Intent(SignIn.this, SignUp.class));
            finish();
        }
        if (view.getId() == R.id.txtForgotPassword) {
            showAlert();
        }*/
 /* edEmail = (EditText) findViewById(R.id.edEmail);
        edPassword = (EditText) findViewById(R.id.edPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(SignIn.this);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(SignIn.this);
        txtCreateAccount = (TextView) findViewById(R.id.txtCreateAccount);
        txtCreateAccount.setOnClickListener(SignIn.this);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(SignIn.this);*/