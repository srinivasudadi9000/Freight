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

import java.util.ArrayList;

/**
 * Created by Archana on 1/4/2017.
 */
public class CommentAdapter extends BaseAdapter {
    private ArrayList<Truck> arrayListComments;
    private Context context;

    public CommentAdapter(ArrayList<Truck> arrayListComments, Context commentList) {
        this.arrayListComments = arrayListComments;
        this.context = commentList;
    }

    @Override
    public int getCount() {
        return this.arrayListComments.size();
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
        CommentViewHolder holder = null;
        Truck truck = this.arrayListComments.get(position);
        if (convertView == null) {
            holder = new CommentViewHolder();
            convertView = inflater.inflate(R.layout.activity_comment_adapter, null);
            holder.txtName = convertView.findViewById(R.id.txtName);
            holder.txtComment = convertView.findViewById(R.id.txtComment);
            holder.txtRating = convertView.findViewById(R.id.txtRating);
            convertView.setTag(holder);
        } else {
            holder = (CommentViewHolder) convertView.getTag();
        }
        if (truck.getUserName() != null) {
            holder.txtName.setText(truck.getUserName());
            if (truck.getComment() != null) {
                holder.txtComment.setText(truck.getComment());
            } else {
                holder.txtComment.setText("");
            }
            if (truck.getRating() == null) {
                holder.txtRating.setVisibility(View.GONE);
            } else {
                holder.txtRating.setVisibility(View.VISIBLE);
                holder.txtRating.setText(truck.getRating());
            }
        }
        return convertView;
    }

    static class CommentViewHolder {
        TextView txtComment, txtName, txtRating;
    }
}
