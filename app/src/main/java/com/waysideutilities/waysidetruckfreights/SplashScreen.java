package com.waysideutilities.waysidetruckfreights;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import com.waysideutilities.waysidetruckfreights.Cargo.Cargo_Provider;
import com.waysideutilities.waysidetruckfreights.Owner.Truck_Owner;
import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;

public class SplashScreen extends AppCompatActivity {
    private static final int SLEEP_TIME = 3;
    private String userType = null;
    private String VERIFIED = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//Remove Title Bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    // Removes notification bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        userType = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERTYPE", null);
        VERIFIED = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("VERIFIED", null);
        Thread background = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(SLEEP_TIME * 1000);
                    if (VERIFIED != null) {
                        if (userType != null) {
                            if (userType.equals("Cargo Provider")) {
                                startActivity(new Intent(SplashScreen.this, Cargo_Provider.class));
                            } else if (userType.equals("Truck Owner")) {
                                startActivity(new Intent(SplashScreen.this, Truck_Owner.class));
                            }
                        } else {
                            if (userType == null){
                                startActivity(new Intent(SplashScreen.this, SignIn.class));
                            }else {
                                startActivity(new Intent(SplashScreen.this, Verify.class));
                            }
                        }
                    } else {
                        startActivity(new Intent(SplashScreen.this, SignIn.class));
                    }
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        background.start();
       //  startActivity(new Intent(SplashScreen.this, SignUp.class));
    }
}
/*if (userType != null) {
                        if(VERIFIED != null) {
                            if (userType.equals("Cargo Provider")) {
                                startActivity(new Intent(SplashScreen.this, Cargo_Provider.class));
                            } else if (userType.equals("Truck Owner")) {
                                startActivity(new Intent(SplashScreen.this, Truck_Owner.class));
                            }
                        }else{
                            startActivity(new Intent(SplashScreen.this, Verify.class));
                        }
                    } else {
                        if(VERIFIED != null) {
                            startActivity(new Intent(SplashScreen.this, SignIn.class));
                        }else{
                            startActivity(new Intent(SplashScreen.this, SignIn.class));
                        }
                    }*/