package com.waysideutilities.waysidetruckfreights.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import java.util.Arrays;
import java.util.List;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;
/**
 * Created by Archana on 8/29/2016.
 */
public class ChooseLanguage extends AppCompatActivity {

    private ListView listLanguage;
    private Toolbar toolbar;
    private String language;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_choose_language);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.select_language);
        final List<String> arrayList = Arrays.asList(getResources().getStringArray(R.array.language));

        listLanguage.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
        listLanguage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
                startActivity(new Intent(ChooseLanguage.this, Profile.class));
                finish();
            }
        });
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listLanguage = (ListView) findViewById(R.id.listLanguage);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ChooseLanguage.this, Profile.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(ChooseLanguage.this, Profile.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
