package com.waysideutilities.waysidetruckfreights.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.waysideutilities.waysidetruckfreights.PojoClasses.Truck;
import com.waysideutilities.waysidetruckfreights.Profile.MyTrips;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import java.util.ArrayList;


/**
 * Created by Archana on 1/4/2017.
 */

public class TripAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Truck> tripList;

    public TripAdapter(MyTrips myTrips, ArrayList<Truck> listTrip) {
        this.context = myTrips;
        this.tripList = listTrip;
    }

    @Override
    public int getCount() {
        return this.tripList.size();
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
        Truck truck = this.tripList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_trip_adapter, null);
            holder.cardView = (CardView) convertView.findViewById(R.id.cardView);
            holder.txtLoadPostNo = (TextView) convertView.findViewById(R.id.txtLoadPostNo);
            holder.txtLoadDate = (TextView) convertView.findViewById(R.id.txtLoadDate);
            holder.txtLoadFrom = (TextView) convertView.findViewById(R.id.txtLoadFrom);
            holder.txtLoadTo = (TextView) convertView.findViewById(R.id.txtLoadTo);
            holder.txtLoadWeight = (TextView) convertView.findViewById(R.id.txtLoadWeight);
            holder.txtLoadType = (TextView) convertView.findViewById(R.id.txtLoadType);
            holder.txtTripStatus = (TextView) convertView.findViewById(R.id.txtTripStatus);
            holder.txtCash = (TextView) convertView.findViewById(R.id.txtCash);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (truck.getTrip_status().equals("2")) {
            holder.txtTripStatus.setText(R.string.completed);
            holder.txtTripStatus.setTextColor(ContextCompat.getColor(context,R.color.green));
        } else if(truck.getTrip_status().equals("0")){
            holder.txtTripStatus.setText(R.string.up_coming);
            holder.txtTripStatus.setTextColor(ContextCompat.getColor(context,R.color.yellow));
        }else{
            holder.txtTripStatus.setText(R.string.running);
            holder.txtTripStatus.setTextColor(ContextCompat.getColor(context,R.color.blue));
        }
        holder.txtLoadPostNo.setText("Truck Id : "+truck.getPost_truck_id());
        holder.txtLoadFrom.setText("From : "+truck.getFrom() + " To " + truck.getTo());
        holder.txtLoadDate.setText("Date : " +FrightUtils.getFormattedDate("yyyy-MM-dd", "dd/MM/yyyy", truck.getDate(), null));
        holder.txtCash.setText("Truck registration number : "+truck.getTruckRegNumber());
        holder.txtLoadWeight.setVisibility(View.GONE);
        holder.txtLoadType.setVisibility(View.GONE);
        holder.txtLoadTo.setVisibility(View.GONE);
        //String charges[] = truck.getCharges().split("#");
        //holder.txtCash.setText("Cash : Rs. "+charges[0] + " /- " +charges[1]);
        return convertView;
    }

    static class ViewHolder {
        CardView cardView;
        TextView txtLoadPostNo, txtLoadDate, txtLoadFrom, txtLoadTo, txtLoadWeight, txtLoadType, txtCash, txtTripStatus;
    }
}
