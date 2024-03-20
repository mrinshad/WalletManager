package com.example.walletmanager.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import com.example.walletmanager.Models.SettingsListModel;
import com.example.walletmanager.R;
import java.util.ArrayList;

public class SettingsListCustomAdapter extends ArrayAdapter<SettingsListModel> implements View.OnClickListener {
    private ArrayList<SettingsListModel> dataSet;
    private Context mContext;// Reference to the descButton

    String TAG = "SettingListCustomAdapter";

    @Override
    public void onClick(View v) {

    }

    private static class ViewHolder {
        TextView txtButtonName;
        TextView descTextView;
        Switch isToggleNeed;
    }

    public SettingsListCustomAdapter(ArrayList<SettingsListModel> data, Context context) {
        super(context, R.layout.settings_list_item_layout, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SettingsListModel settingsListModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.settings_list_item_layout, parent, false);
            viewHolder.txtButtonName = convertView.findViewById(R.id.logout_button_text);
            viewHolder.descTextView = convertView.findViewById(R.id.list_item_desc);
            viewHolder.isToggleNeed = convertView.findViewById(R.id.list_item_toggle);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.txtButtonName.setText(settingsListModel.getButtonName());
        if(settingsListModel.getIsToggleNeed()){
            viewHolder.isToggleNeed.setVisibility(View.VISIBLE);
        }


        // Update visibility of descButton based on conditions
        if (settingsListModel.getDesc() != null && !settingsListModel.getDesc().isEmpty()) {
            viewHolder.descTextView.setVisibility(View.VISIBLE);
            viewHolder.descTextView.setText(settingsListModel.getDesc());
        } else {
            viewHolder.descTextView.setVisibility(View.GONE);
        }

        // Return the completed view to render on screen
        return convertView;
    }




}
