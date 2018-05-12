package com.waysideutilities.waysidetruckfreights.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.waysideutilities.waysidetruckfreights.MapsActivity;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Cargo;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Request;
import com.waysideutilities.waysidetruckfreights.Profile.MyOrder;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import java.util.ArrayList;


/**
 * Created by Archana on 1/4/2017.
 */

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Cargo> orderList;

    public OrderAdapter(Context context, ArrayList<Cargo> orderList) {
        this.context = context;
        this.orderList = orderList;
    }


    @Override
    public int getCount() {
        return this.orderList.size();
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
        final Cargo cargo = this.orderList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_trip_adapter, null);
            holder.txtLoadPostNo = convertView.findViewById(R.id.txtLoadPostNo);
            holder.txtLoadDate = convertView.findViewById(R.id.txtLoadDate);
            holder.txtLoadFrom = convertView.findViewById(R.id.txtLoadFrom);
            holder.txtLoadTo = convertView.findViewById(R.id.txtLoadTo);
            holder.txtLoadWeight = convertView.findViewById(R.id.txtLoadWeight);
            holder.txtLoadType = convertView.findViewById(R.id.txtLoadType);
            holder.txtTripStatus = convertView.findViewById(R.id.txtTripStatus);
            holder.txtCash = convertView.findViewById(R.id.txtCash);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String from = cargo.getFrom_street_name().concat(",").concat(cargo.getFrom_landmark()).concat(",").concat(cargo.getFrom_city()).concat(",").concat(cargo.getFrom_state()).concat(",").concat(cargo.getFrom_pincode());
        final String to = cargo.getTo_street_name().concat(",").concat(cargo.getTo_landmark()).concat(",").concat(cargo.getTo_city()).concat(",").concat(cargo.getTo_state()).concat(",").concat(cargo.getTo_pincode());

       // holder.txtLoadPostNo.setText("Truck Id : " + cargo.getId());
        holder.txtLoadPostNo.setText("Load Id : " + cargo.getId());
        holder.txtLoadDate.setText("Date : " + FrightUtils.getFormattedDate("yyyy-MM-dd", "dd/MM/yyyy", cargo.getDate(), null));
        holder.txtLoadFrom.setText("From : " + from);
        holder.txtLoadTo.setText("To : " + to);
        holder.txtLoadWeight.setText("Total Amount : " + cargo.getTotal_charges());
        holder.txtLoadType.setText("Paid Amount : " + cargo.getPaid_amount());
        holder.txtCash.setText("Remaining Amount : " + cargo.getRemaining_amount());

        if (cargo.getTrip_status().equals("1")) {
            holder.txtTripStatus.setText(R.string.running);
            holder.txtTripStatus.setTextColor(ContextCompat.getColor(context, R.color.blue));
        } else if (cargo.getTrip_status().equals("2")) {
            holder.txtTripStatus.setText(R.string.completed);
            holder.txtTripStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.txtTripStatus.setClickable(false);
        } else {
            holder.txtTripStatus.setText(R.string.track_trip);
            holder.txtTripStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        }

        holder.txtTripStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cargo.getTrip_status().equals("2")) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("ORIGIN", from);
                    bundle.putString("DEST", to);
                    bundle.putString("TRUCK_ID", cargo.getBooked_by_id());
                    bundle.putString("LOAD_ID", cargo.getId());
                    intent.putExtra("BUNDLE", bundle);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView txtLoadPostNo, txtLoadDate, txtLoadFrom, txtLoadTo, txtLoadWeight, txtLoadType, txtCash, txtTripStatus;
    }
}


// bundle.putString("FROM", cargo.getFrom_street_name().concat(",").concat(cargo.getFrom_landmark()).concat(",").concat(cargo.getFrom_city()).concat(",").concat(cargo.getFrom_state()).concat(",").concat(cargo.getFrom_pincode()));
// bundle.putString("TO", cargo.getTo_street_name().concat(",").concat(cargo.getTo_landmark()).concat(",").concat(cargo.getTo_city()).concat(",").concat(cargo.getTo_state()).concat(",").concat(cargo.getTo_pincode()));

 /* holder.txtMessage.setText(request.getReceived_message());
        holder.txtTotalCharges.setText("Total Amount : " + request.getTotal_charges());
        holder.txtPaiidCharges.setText("Paid Amount : " + request.getPaid_charges());
        holder.txtRemainingCharges.setText("Remaining Amount : " + request.getRemaining_charges());

            holder.txtTripTrack.setText(R.string.track_trip);
            holder.txtTripTrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    Bundle bundle = new Bundle();
                   // bundle.putString("ORIGIN", request.getFrom_street_name().concat(",").concat(cargo.getFrom_landmark()).concat(",").concat(cargo.getFrom_city()).concat(",").concat(cargo.getFrom_state()).concat(",").concat(cargo.getFrom_pincode()));
                    //bundle.putString("DEST", cargo.getTo_street_name().concat(",").concat(cargo.getTo_landmark()).concat(",").concat(cargo.getTo_city()).concat(",").concat(cargo.getTo_state()).concat(",").concat(cargo.getTo_pincode()));
                    bundle.putString("TRUCK_ID",request.getRtruck_id() );
                    bundle.putString("LOAD_ID",request.getLoad_id() );
                    intent.putExtra("", bundle);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            });*/