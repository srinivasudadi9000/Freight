package com.waysideutilities.waysidetruckfreights.Cargo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;
import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;
/**
 * Created by Archana on 2/3/2017.
 */
public class WaitingCharges extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView listViewWaitingCharges;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_waiting_charges);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.halting_charges);
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
        listViewWaitingCharges = (ListView) findViewById(R.id.listViewWaitingCharges);
        listViewWaitingCharges.setAdapter(new Halting_ChargesAdapter(WaitingCharges.this));
    }

    private class Halting_ChargesAdapter extends BaseAdapter {
        private Context context;


        public Halting_ChargesAdapter(WaitingCharges waitingCharges) {
            this.context = waitingCharges;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.text_charge_layout, null);
            return convertView;
        }
    }
}










/*
final List<String> arrayListOne = Arrays.asList(getResources().getStringArray(R.array.one));
        final List<String> arrayListTwo = Arrays.asList(getResources().getStringArray(R.array.two));
        final List<String> arrayListThree = Arrays.asList(getResources().getStringArray(R.array.three));
        final List<String> arrayListFour = Arrays.asList(getResources().getStringArray(R.array.four));
        final List<String> arrayListFive = Arrays.asList(getResources().getStringArray(R.array.five));
        final List<String> arrayListSix = Arrays.asList(getResources().getStringArray(R.array.six));
        final List<String> arrayListSeven = Arrays.asList(getResources().getStringArray(R.array.seven));
        final List<String> arrayListEight = Arrays.asList(getResources().getStringArray(R.array.eight));
        final List<String> arrayListNine = Arrays.asList(getResources().getStringArray(R.array.nine));


listViewWaitingCharges.setAdapter(new Halting_ChargesAdapter(WaitingCharges.this, arrayListOne, arrayListTwo, arrayListThree, arrayListFour, arrayListFive, arrayListSix, arrayListSeven, arrayListEight, arrayListNine));

private List<String> arrayListOne;
        private List<String> arrayListTwo;
        private List<String> arrayListThree;
        private List<String> arrayListFour;
        private List<String> arrayListFive;
        private List<String> arrayListSix;
        private List<String> arrayListSeven;
        private List<String> arrayListEight;
        private List<String> arrayListNine;

        public Halting_ChargesAdapter(WaitingCharges waitingCharges, List<String> arrayListOne, List<String> arrayListTwo, List<String> arrayListThree, List<String> arrayListFour, List<String> arrayListFive, List<String> arrayListSix, List<String> arrayListSeven, List<String> arrayListEight, List<String> arrayListNine) {
            this.context = waitingCharges;
            this.arrayListOne = arrayListOne;
            this.arrayListTwo = arrayListTwo;
            this.arrayListThree = arrayListThree;
            this.arrayListFour = arrayListFour;
            this.arrayListFive = arrayListFive;
            this.arrayListSix = arrayListSix;
            this.arrayListSeven = arrayListSeven;
            this.arrayListEight = arrayListEight;
            this.arrayListNine = arrayListNine;
        }*/