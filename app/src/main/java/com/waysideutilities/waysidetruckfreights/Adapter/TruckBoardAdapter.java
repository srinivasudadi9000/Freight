package com.waysideutilities.waysidetruckfreights.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.waysideutilities.waysidetruckfreights.PojoClasses.Truck;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Archana on 1/4/2017.
 */
public class TruckBoardAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Truck> truckArrayList;

    public TruckBoardAdapter(Context context, ArrayList<Truck> listTrucks) {
        this.context = context;
        this.truckArrayList = listTrucks;
    }

    @Override
    public int getCount() {
        return this.truckArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Activity activity = (Activity) context;
        LayoutInflater inflater = activity.getLayoutInflater();
        Holder holder = null;
        Truck truck = this.truckArrayList.get(i);
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.layout_my_trucks, null);
            holder.cardView = (CardView)convertView.findViewById(R.id.cardView);
            holder.imageTruck = (ImageView) convertView.findViewById(R.id.imageTruck);
            holder.txtTruckNumber = (TextView) convertView.findViewById(R.id.txtTruckNumber);
            holder.txtDriverName = (TextView) convertView.findViewById(R.id.txtDriverName);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            holder.txtRoute = (TextView) convertView.findViewById(R.id.txtRoute);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if((truck.getTruckImage() != null) && (!truck.getTruckImage().equals("")))
            Picasso.with(context.getApplicationContext()).load(truck.getTruckImage()).placeholder(R.drawable.image_not_found).noFade().error(R.drawable.image_not_found).resize(200, 200).centerCrop().into(holder.imageTruck);
       // holder.txtTruckNumber.setText(truck.getTruckNumber());
        holder.txtTruckNumber.setText(truck.getTruckRegNumber());
        holder.txtDriverName.setText(truck.getDriverName());
        holder.txtDate.setText(FrightUtils.getFormattedDate("yyyy-MM-dd", "dd/MM/yyyy",truck.getDate(), null));
        holder.txtRoute.setText(truck.getFrom() + " To " + truck.getTo());
        return convertView;
    }

    static class Holder {
        ImageView imageTruck;
        CardView cardView;
        TextView txtTruckNumber, txtDriverName, txtDate, txtRoute;
    }
}
