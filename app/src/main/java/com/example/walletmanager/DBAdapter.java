package com.example.walletmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.walletmanager.MyData;

import java.util.List;

public class DBAdapter extends ArrayAdapter<MyData> {
    private LayoutInflater inflater;

    public DBAdapter(Context context, List<MyData> dataList) {
        super(context, 0, dataList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.idTextView = convertView.findViewById(R.id.idTextView);
            viewHolder.dateTextView = convertView.findViewById(R.id.dateTextView);
            viewHolder.timeTextView = convertView.findViewById(R.id.timeTextView);
            viewHolder.amountTextView = convertView.findViewById(R.id.amountTextView);
            viewHolder.patyNameTextView = convertView.findViewById(R.id.patyNameTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MyData data = getItem(position);
        if (data != null) {
            viewHolder.idTextView.setText(data.getId());
            viewHolder.dateTextView.setText(data.getDate());
            viewHolder.timeTextView.setText(data.getTime());
            viewHolder.amountTextView.setText(data.getAmount());
            viewHolder.patyNameTextView.setText(data.getParty_name());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView idTextView,dateTextView,timeTextView,amountTextView,patyNameTextView;
    }
}
