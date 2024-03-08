package com.example.walletmanager.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.walletmanager.Database.DBManager;
import com.example.walletmanager.Database.MyDatabaseHelper;
import com.example.walletmanager.Models.PartyListModel;
import com.example.walletmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class Party extends AppCompatActivity {

    SQLiteDatabase mydb;
    DBManager db = new DBManager();
    RelativeLayout layout;
    ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);
        layout = findViewById(R.id.partyLayout);
        getSupportActionBar().hide();

        listView = (ListView) findViewById(R.id.listview_party);
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(this);

        List<PartyListModel> partyList = myDatabaseHelper.getAllParties();

        ArrayAdapter<PartyListModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, partyList);
        listView.setAdapter(adapter);

    }

    public void addParty(View v) {
        try {
            final Dialog dialog2 = new Dialog(Party.this);
            dialog2.setContentView(R.layout.add_party);
            Button addBtn = (Button) dialog2.findViewById(R.id.add_party_addButton);

            addBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    EditText partyNameEditText, partyAmountEditText;
                    partyNameEditText = dialog2.findViewById(R.id.add_party_partyName);
                    partyAmountEditText = dialog2.findViewById(R.id.add_party_partyAmount);

                    String partyName, partyAmount;

                    partyName = partyNameEditText.getText().toString();
                    partyAmount = partyAmountEditText.getText().toString();

                    if (partyName.isEmpty()) {
                        partyAmountEditText.setError("Please input name");
                        return;
                    }

                    if (partyAmount.isEmpty()) {
                        partyAmountEditText.setError("Please input amount");
                        return;
                    }

                    mydb = openOrCreateDatabase(db.DBNAME, 0, null);
                    mydb.execSQL("INSERT INTO PARTY (name,balance) VALUES ('" + partyName + "'," + Double.parseDouble(partyAmount) + ")");
                    mydb.close();

                    Snackbar.make(layout, "Party Added successfully", Snackbar.LENGTH_SHORT).show();

                    dialog2.dismiss();
                }
            });
            dialog2.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}