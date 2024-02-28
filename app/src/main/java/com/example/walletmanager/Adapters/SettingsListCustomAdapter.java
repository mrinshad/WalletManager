package com.example.walletmanager.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.walletmanager.Models.SettingsListModel;
import com.example.walletmanager.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class SettingsListCustomAdapter  extends ArrayAdapter<SettingsListModel> implements View.OnClickListener{
    private ArrayList<SettingsListModel> dataSet;
    Context mContext;

    String TAG = "SettingListCustomAdapter";
    private static class ViewHolder {
        TextView txtButtonName;
    }

    public SettingsListCustomAdapter(ArrayList<SettingsListModel> data, Context context) {
        super(context, R.layout.settings_list_item_layout, data);
        this.dataSet = data;
        this.mContext=context;

    }
    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        SettingsListModel settingsListModel=(SettingsListModel)object;

        switch (v.getId())
        {
            case R.id.logout_button_text:
                Snackbar.make(v, "Release date " +settingsListModel.getButtonName(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

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
            viewHolder.txtButtonName = (TextView) convertView.findViewById(R.id.logout_button_text);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        try {
            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);

            result.startAnimation(animation);
            lastPosition = position;
        }catch (Exception e){
            Log.e(TAG, "getView: ", e );
        }

        viewHolder.txtButtonName.setText(settingsListModel.getButtonName());
        // Return the completed view to render on screen
        return convertView;
    }
}
