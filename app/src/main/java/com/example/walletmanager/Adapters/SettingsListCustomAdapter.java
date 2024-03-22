package com.example.walletmanager.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.walletmanager.Models.SettingsListModel;
import com.example.walletmanager.R;

import java.util.ArrayList;

public class SettingsListCustomAdapter extends ArrayAdapter<SettingsListModel> implements View.OnClickListener {
    private ArrayList<SettingsListModel> dataSet;
    private Context mContext;// Reference to the descButton
    private OnToggleChangeListener toggleChangeListener;

    String TAG = "SettingListCustomAdapter";
    private SharedPreferences sharedPreferences;

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

    public interface OnToggleChangeListener {
        void onToggleChanged(boolean isChecked, int position);
    }
    public void setOnToggleChangeListener(OnToggleChangeListener listener) {
        this.toggleChangeListener = listener;
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

        //  Update the visibility of toggle button
        if(settingsListModel.getIsToggleNeed()){
            viewHolder.isToggleNeed.setVisibility(View.VISIBLE);
        }else{
            viewHolder.isToggleNeed.setVisibility(View.GONE);
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if(settingsListModel.getButtonName().equals("Firebase Mode")) {
           try {
               boolean isChecked = sharedPreferences.getBoolean("firebase_mode", false);
               viewHolder.isToggleNeed.setChecked(isChecked);
           }catch (Exception e){
               Log.e(TAG, "getView: ",e );
           }
        }


        // Update visibility of descButton based on conditions
        if (settingsListModel.getDesc() != null && !settingsListModel.getDesc().isEmpty()) {
            viewHolder.descTextView.setVisibility(View.VISIBLE);
            viewHolder.descTextView.setText(settingsListModel.getDesc());
        } else {
            viewHolder.descTextView.setVisibility(View.GONE);
        }



        viewHolder.isToggleNeed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (toggleChangeListener != null) {
                    toggleChangeListener.onToggleChanged(isChecked, position);
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }




}
