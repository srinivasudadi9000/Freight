package com.waysideutilities.waysidetruckfreights.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.waysideutilities.waysidetruckfreights.Cargo.TruckDetail;
import com.waysideutilities.waysidetruckfreights.Owner.LoadDetail;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Cargo;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Truck;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;

import java.util.ArrayList;

/**
 * Created by Archana on 1/4/2017.
 */
public class BoardAdapter extends BaseAdapter {
    private Context context;
    public ArrayList<Cargo> arrayList;
    public ArrayList<Truck> arrayListTruck;
    private int selectList = 0;
    private Cargo cargo;
    private Truck truck;

    public BoardAdapter(Context context, ArrayList<Cargo> listCargo) {
        this.selectList = 1;
        this.context = context;
        this.arrayList = listCargo;
    }

    public BoardAdapter(ArrayList<Truck> listTruck, Context context) {
        this.selectList = 2;
        this.context = context;
        this.arrayListTruck = listTruck;
    }

    @Override
    public int getCount() {

        if (selectList == 1) {
            return this.arrayList.size();
        } else {
            return this.arrayListTruck.size();
        }
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        Activity activity = (Activity) context;
        LayoutInflater inflater = activity.getLayoutInflater();
        BoardHolder holder = null;
        if (selectList == 1) {
            cargo = this.arrayList.get(position);
        } else {
            truck = this.arrayListTruck.get(position);
        }
        if (convertView == null) {
            holder = new BoardHolder();
            convertView = inflater.inflate(R.layout.board, null);
            holder.txtSrNo = (TextView) convertView.findViewById(R.id.txtSrNo);
            holder.txtAdate = (TextView) convertView.findViewById(R.id.txtAdate);
            holder.txtPFrom = (TextView) convertView.findViewById(R.id.txtPFrom);
            holder.txtPTo = (TextView) convertView.findViewById(R.id.txtPTo);
            holder.txtSrNo.setTag(position);
            convertView.setTag(holder);
        } else {
            holder = (BoardHolder) convertView.getTag();
        }
        if (selectList == 1) {
            holder.txtSrNo.setText(cargo.getId());
            holder.txtAdate.setText(FrightUtils.getFormattedDate("yyyy-MM-dd", "dd/MM/yyyy", cargo.getDate(), null));
            holder.txtPFrom.setText(cargo.getFrom_city());
            holder.txtPTo.setText(cargo.getTo_city());
            holder.txtSrNo.setTag(position);
            holder.txtSrNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int sPosotion = (Integer) view.getTag();
                    Cargo sCargo = arrayList.get(sPosotion);
                    Bundle bundle = new Bundle();
                    bundle.putString("LOADID", sCargo.getId());
                    bundle.putString("CARGO_OWNER_ID",sCargo.getCargo_user_id());
                    bundle.putString("DATE", sCargo.getDate());

                    bundle.putString("FROM", sCargo.getFrom_street_name().concat(",").concat(sCargo.getFrom_landmark()).concat(",").concat(sCargo.getFrom_city()).concat(",").concat(sCargo.getFrom_state()).concat(",").concat(sCargo.getFrom_pincode()));
                    bundle.putString("TO", sCargo.getTo_street_name().concat(",").concat(sCargo.getTo_landmark()).concat(",").concat(sCargo.getTo_city()).concat(",").concat(sCargo.getTo_state()).concat(",").concat(sCargo.getTo_pincode()));

                    bundle.putString("CONTACT_NO", sCargo.getContactNumber());
                    bundle.putString("FTL_LTL", sCargo.getFtl_ltl());
                    bundle.putString("WEIGHT", sCargo.getWeight());
                    bundle.putString("CATEGORY", sCargo.getLoad_Category());
                    bundle.putString("TRUCK_TYPE", sCargo.getType_of_truck());
                    bundle.putString("DESCRIPTION", sCargo.getLoad_description());
                    Intent intent = new Intent(context, LoadDetail.class);
                    intent.putExtra("BUNDLE", bundle);
                    context.startActivity(intent);
                }
            });
        } else {
            holder.txtSrNo.setText(truck.getPost_truck_id());
            holder.txtSrNo.setTag(position);
            holder.txtAdate.setText(FrightUtils.getFormattedDate("yyyy-MM-dd", "dd/MM/yyyy", truck.getDate(), null));
            holder.txtPFrom.setText(truck.getFrom());
            holder.txtPTo.setText(truck.getTo());
            holder.txtSrNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer sPosition = (Integer) v.getTag();
                    Truck sTruck = arrayListTruck.get(sPosition);
                    Bundle bundle = new Bundle();
                    bundle.putString("POST_TRUCKID", sTruck.getPost_truck_id());
                    bundle.putString("TRUCKID", sTruck.getId());
                    bundle.putString("TRUCK_OWNER_ID", sTruck.getTruck_owner_id());
                    bundle.putString("DATE", sTruck.getDate());
                    bundle.putString("FROM", sTruck.getFrom());
                    bundle.putString("TO", sTruck.getTo());
                    bundle.putString("CATEGORY", sTruck.getLoad_Category());
                    bundle.putString("DRIVER_NUMBER", sTruck.getDriverNumber());
                    bundle.putString("CONTACT_NO",sTruck.getContactNumber());
                    bundle.putString("TRUCK_TYPE", sTruck.getType_of_truck());
                    bundle.putString("FTL_LTL", sTruck.getFtl_ltl());
                    bundle.putString("LOAD_CAPACITY", sTruck.getLoad_Capacity());
                    bundle.putString("CHARGES", sTruck.getCharges());
                    bundle.putString("DESCRIPTION", sTruck.getLoad_description());
                    Intent intent = new Intent(context, TruckDetail.class);
                    intent.putExtra("BUNDLE", bundle);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    static class BoardHolder {
        TextView txtSrNo, txtAdate, txtPFrom, txtPTo;
    }
}
