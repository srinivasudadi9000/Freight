package com.waysideutilities.waysidetruckfreights.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.waysideutilities.waysidetruckfreights.PojoClasses.Cargo;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import java.util.ArrayList;

/**
 * Created by Archana on 1/4/2017.
 */
public class SelectLoadAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Cargo> unBookedCargo;

    public SelectLoadAdapter(Context selectLoad, ArrayList<Cargo> listUnBookedCargo) {
        this.context = selectLoad;
        this.unBookedCargo = listUnBookedCargo;
    }

    @Override
    public int getCount() {
        return this.unBookedCargo.size();
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
        ViewHolder holder = null;
        Cargo cargo = this.unBookedCargo.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_select_load_adapter, null);
            holder.txtLoadPostNo = (TextView) convertView.findViewById(R.id.txtLoadPostNo);
            holder.txtLoadDate = (TextView) convertView.findViewById(R.id.txtLoadDate);
            holder.txtLoadFrom = (TextView) convertView.findViewById(R.id.txtLoadFrom);
            holder.txtLoadTo = (TextView) convertView.findViewById(R.id.txtLoadTo);
            holder.txtLoadWeight = (TextView) convertView.findViewById(R.id.txtLoadWeight);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtLoadPostNo.setText("Post ID: " + cargo.getId());
        holder.txtLoadDate.setText("Date : " + FrightUtils.getFormattedDate("yyyy-MM-dd", "dd/MM/yyyy", cargo.getDate(), null));
        holder.txtLoadFrom.setText("From : " + cargo.getFrom_street_name().concat(",").concat(cargo.getFrom_landmark()).concat(",").concat(cargo.getFrom_city()).concat(",").concat(cargo.getFrom_state()).concat(",").concat(cargo.getFrom_pincode()));
        holder.txtLoadTo.setText("To : " + cargo.getTo_street_name().concat(",").concat(cargo.getTo_landmark()).concat(",").concat(cargo.getTo_city()).concat(",").concat(cargo.getTo_state()).concat(",").concat(cargo.getTo_pincode()));
        holder.txtLoadWeight.setText("Weight : " + cargo.getWeight());
        return convertView;
    }

    static class ViewHolder {
        TextView txtLoadPostNo, txtLoadDate, txtLoadFrom, txtLoadTo, txtLoadWeight;
    }
}
