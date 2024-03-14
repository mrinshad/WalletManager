package com.example.walletmanager.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.walletmanager.Database.MyDatabaseHelper;
import com.example.walletmanager.R;

import java.util.List;

public class PartySelectionUtil {
    public interface PartySelectionCallback {
        void onPartySelected(String partyName);
    }

    public static void showPartySelectionDialog(Context context, PartySelectionCallback callback) {
        MyDatabaseHelper databaseHelper = new MyDatabaseHelper(context);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.select_party_list, null);
        alertDialog.setView(convertView);

        ListView lv = convertView.findViewById(R.id.listView);
        ArrayAdapter<String> adapter;
        List<String> partyList = databaseHelper.getPartyName(databaseHelper.getReadableDatabase());
        adapter = new ArrayAdapter<>(context, R.layout.party_list, partyList);
        lv.setAdapter(adapter);

        EditText textPartySearch = convertView.findViewById(R.id.partySearch);
        textPartySearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(textPartySearch.getText().toString());
            }
        });

        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener((parent, view, position, id) -> {
            String partyName = (String) parent.getItemAtPosition(position);
            callback.onPartySelected(partyName);
            ad.dismiss();
        });
    }
}