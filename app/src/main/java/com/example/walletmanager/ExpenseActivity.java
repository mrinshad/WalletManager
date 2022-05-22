package com.example.walletmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {
    FloatingActionButton mAddFab, mAddAlarmFab, mAddPersonFab;
    TextView addAlarmActionText, addPersonActionText;
    Boolean isAllFabsVisible;

    ArrayAdapter<String> adapter2;
    String[] arraylist;
    SQLiteDatabase mydb;
    DBManager db = new DBManager();

    EditText textPartySearch;
    TextView partyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        getSupportActionBar().hide();

        mAddFab = findViewById(R.id.add_fab);
        // FAB button
        mAddAlarmFab = findViewById(R.id.add_alarm_fab);
        mAddPersonFab = findViewById(R.id.add_person_fab);
        addAlarmActionText = findViewById(R.id.add_alarm_action_text);
        addPersonActionText = findViewById(R.id.add_person_action_text);

        mAddAlarmFab.setVisibility(View.GONE);
        mAddPersonFab.setVisibility(View.GONE);
        addAlarmActionText.setVisibility(View.GONE);
        addPersonActionText.setVisibility(View.GONE);

        isAllFabsVisible = false;

        mAddFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {

                            mAddAlarmFab.show();
                            mAddPersonFab.show();
                            addAlarmActionText.setVisibility(View.VISIBLE);
                            addPersonActionText.setVisibility(View.VISIBLE);

                            isAllFabsVisible = true;
                        } else {

                            mAddAlarmFab.hide();
                            mAddPersonFab.hide();
                            addAlarmActionText.setVisibility(View.GONE);
                            addPersonActionText.setVisibility(View.GONE);

                            isAllFabsVisible = false;
                        }
                    }
                });

        mAddPersonFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Person Added", Toast.LENGTH_SHORT).show();
                    }
                });

        mAddAlarmFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Alarm Added", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void selectParty(View v) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.select_party_list, null);
        alertDialog.setView(convertView);

        ListView lv = (ListView) convertView.findViewById(R.id.listView);
//        listAdapter = new CustomNewInvoiceAdapter(this, R.layout.batch_sales_list, new ArrayList<OrderListModel>());
        adapter2 = new ArrayAdapter<String>(this, R.layout.party_list, getPartyList());
        lv.setAdapter(adapter2);
        textPartySearch = (EditText) convertView.findViewById(R.id.partySearch);
        textPartySearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                adapter2.getFilter()
                        .filter(textPartySearch.getText().toString());
            }
        });
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter1, View v, int position, long arg3) {
//                String value = (String) adapter1.getItemAtPosition(position);
//                partySelectBtn.setText(value);
//                partyName = value;
//                getBalance();
//                ad.dismiss();

            }
        });


    }

    public String[] getPartyList() {
        List<String> partyList = new ArrayList<String>();
        try {
            mydb = openOrCreateDatabase(db.DBNAME, Context.MODE_PRIVATE, null);
            Cursor allrows = mydb.rawQuery("SELECT name FROM PARTY", null);
            if (allrows.moveToFirst()) {
                do {
                    partyList.add(allrows.getString(0));
                } while (allrows.moveToNext());
            }
            mydb.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
        // Create sequence of items
        final String[] party = partyList.toArray(new String[partyList.size()]);
        return party;
    }

}