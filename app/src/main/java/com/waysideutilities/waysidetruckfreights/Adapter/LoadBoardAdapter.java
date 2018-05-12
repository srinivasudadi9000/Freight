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
public class LoadBoardAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Cargo> cargoArrayList;

    public LoadBoardAdapter(Context context, ArrayList<Cargo> cargoArrayList) {
        this.context = context;
        this.cargoArrayList = cargoArrayList;

    }

    @Override
    public int getCount() {
        return this.cargoArrayList.size();
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
        ViewHolder holder = null;
        Cargo cargo = cargoArrayList.get(i);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_my_loads, null);
            holder = new ViewHolder();
            holder.txtLoadPostNo = (TextView) convertView.findViewById(R.id.txtLoadPostNo);
            holder.txtLoadWeight = (TextView) convertView.findViewById(R.id.txtLoadWeight);
            holder.txtLoadTypeOfTruck = (TextView) convertView.findViewById(R.id.txtLoadTypeOfTruck);
            holder.txtLoadFrom = (TextView) convertView.findViewById(R.id.txtLoadFrom);
            holder.txtLoadTo = (TextView) convertView.findViewById(R.id.txtLoadTo);
            holder.txtLoadDate = (TextView) convertView.findViewById(R.id.txtLoadDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtLoadPostNo.setText("Post ID : "+FrightUtils.getFormatedHtmlString(cargo.getId()),TextView.BufferType.SPANNABLE);
        holder.txtLoadDate.setText("Date : "+ FrightUtils.getFormatedHtmlString(FrightUtils.getFormattedDate("yyyy-MM-dd", "dd/MM/yyyy",cargo.getDate(), null)));
        holder.txtLoadFrom.setText("From : "+FrightUtils.getFormatedHtmlString(cargo.getFrom_street_name().concat(",").concat(cargo.getFrom_landmark()).concat(",").concat(cargo.getFrom_city().concat(",").concat(cargo.getFrom_state().concat(",").concat(cargo.getFrom_pincode())))));
        holder.txtLoadTo.setText("To : "+FrightUtils.getFormatedHtmlString(cargo.getTo_street_name().concat(",").concat(cargo.getTo_landmark()).concat(",").concat(cargo.to_city).concat(",").concat(cargo.getTo_state()).concat(",").concat(cargo.getTo_pincode())));
        holder.txtLoadWeight.setText("Weight : "+FrightUtils.getFormatedHtmlString(cargo.getWeight()));
        holder.txtLoadTypeOfTruck.setText("Type of truck : "+FrightUtils.getFormatedHtmlString(cargo.getType_of_truck()));
        return convertView;
    }

    static class ViewHolder {
        TextView txtLoadPostNo, txtLoadWeight, txtLoadTypeOfTruck, txtLoadFrom, txtLoadTo, txtLoadDate;
    }
}
