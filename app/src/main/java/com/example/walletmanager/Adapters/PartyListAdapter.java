package com.example.walletmanager.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.walletmanager.Models.PartyListModel;
import com.example.walletmanager.R;
import android.widget.TextView;

import java.util.List;

public class PartyListAdapter extends ArrayAdapter<PartyListModel> {

    public PartyListAdapter(Context context, List<PartyListModel> parties) {
        super(context, 0, parties);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.party_report_model, parent, false);
        }

        PartyListModel party = getItem(position);

        TextView tvPartyId = convertView.findViewById(R.id.tv_party_id);
        TextView tvPartyName = convertView.findViewById(R.id.tv_party_name);
        TextView tvPartyBalance = convertView.findViewById(R.id.tv_party_balance);

        tvPartyId.setText(String.valueOf(party.getId()));
        tvPartyName.setText(party.getName());
        tvPartyBalance.setText(String.valueOf(party.getBalance()));

        return convertView;
    }
}