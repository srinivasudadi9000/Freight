package com.waysideutilities.waysidetruckfreights.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.waysideutilities.waysidetruckfreights.PojoClasses.Truck;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import java.util.ArrayList;

/**
 * Created by Archana on 1/4/2017.
 */
public class SelectTruckAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Truck> unBookedTruck;

    public SelectTruckAdapter(Context selectTruck, ArrayList<Truck> listUnBookedTrucks) {
        this.context = selectTruck;
        this.unBookedTruck = listUnBookedTrucks;
    }

    @Override
    public int getCount() {
        return this.unBookedTruck.size();
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
        Truck truck = this.unBookedTruck.get(position);
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
        holder.txtLoadPostNo.setText("Post ID: " + truck.getPost_truck_id());
        holder.txtLoadDate.setText("Date : " + FrightUtils.getFormattedDate("yyyy-MM-dd", "dd/MM/yyyy", truck.getDate(), null));
        holder.txtLoadFrom.setText("From : " + truck.getFrom());
        holder.txtLoadTo.setText("To : " + truck.getTo());
        String charges[] = truck.getCharges().split("#");
        holder.txtLoadWeight.setText("Charges : Rs. " + charges[0] + " /- " + charges[1]);
        return convertView;
    }

    static class ViewHolder {
        TextView txtLoadPostNo, txtLoadDate, txtLoadFrom, txtLoadTo, txtLoadWeight;
    }
}
