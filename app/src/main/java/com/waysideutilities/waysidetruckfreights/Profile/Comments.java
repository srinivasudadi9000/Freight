package com.waysideutilities.waysidetruckfreights.Profile;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.waysideutilities.waysidetruckfreights.BaseActivity;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;
/**
 * Created by Archana on 8/29/2016.
 */
public class Comments extends BaseActivity {
    private Toolbar toolbar;
    private String language, truck_id,ratings;
    private Button btnComment, btnCancel;
    private Bundle bundle = null;
    private EditText edtxtcomment;
    private ImageView rating1, rating2, rating3, rating4, rating5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_comments);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.comment));
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

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnComment = (Button) findViewById(R.id.btnComment);
        rating1 = (ImageView) findViewById(R.id.rating1);
        rating2 = (ImageView) findViewById(R.id.rating2);
        rating3 = (ImageView) findViewById(R.id.rating3);
        rating4 = (ImageView) findViewById(R.id.rating4);
        rating5 = (ImageView) findViewById(R.id.rating5);
        rating1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratings = "1";
                rating1.setImageResource(R.mipmap.star_fill);
                rating2.setImageResource(R.mipmap.star);
                rating3.setImageResource(R.mipmap.star);
                rating4.setImageResource(R.mipmap.star);
                rating5.setImageResource(R.mipmap.star);
            }
        });
        rating2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratings = "2";
                rating1.setImageResource(R.mipmap.star_fill);
                rating2.setImageResource(R.mipmap.star_fill);
                rating3.setImageResource(R.mipmap.star);
                rating4.setImageResource(R.mipmap.star);
                rating5.setImageResource(R.mipmap.star);
            }
        });
        rating3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratings = "3";
                rating1.setImageResource(R.mipmap.star_fill);
                rating2.setImageResource(R.mipmap.star_fill);
                rating3.setImageResource(R.mipmap.star_fill);
                rating4.setImageResource(R.mipmap.star);
                rating5.setImageResource(R.mipmap.star);
            }
        });
        rating4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratings = "4";
                rating1.setImageResource(R.mipmap.star_fill);
                rating2.setImageResource(R.mipmap.star_fill);
                rating3.setImageResource(R.mipmap.star_fill);
                rating4.setImageResource(R.mipmap.star_fill);
                rating5.setImageResource(R.mipmap.star);
            }
        });
        rating5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratings = "5";
                rating1.setImageResource(R.mipmap.star_fill);
                rating2.setImageResource(R.mipmap.star_fill);
                rating3.setImageResource(R.mipmap.star_fill);
                rating4.setImageResource(R.mipmap.star_fill);
                rating5.setImageResource(R.mipmap.star_fill);
            }
        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edtxtcomment = (EditText) findViewById(R.id.edtxtcomment);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle = getIntent().getBundleExtra("BUNDLE");
                if (bundle.getString("TRUCK_ID") != null) {
                    truck_id = bundle.getString("TRUCK_ID");
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                    new AddComment(Comments.this, truck_id, edtxtcomment.getText().toString(),ratings).execute();
                }
            }
        });
    }
}
