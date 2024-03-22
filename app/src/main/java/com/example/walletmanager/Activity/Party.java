package com.example.walletmanager.Activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walletmanager.Adapters.PartyListAdapter;
import com.example.walletmanager.Database.DBManager;
import com.example.walletmanager.Database.MyDatabaseHelper;
import com.example.walletmanager.Models.PartyListModel;
import com.example.walletmanager.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Party extends AppCompatActivity {
    RelativeLayout layout;
    ListView listView;
    private String TAG = "Party list Model";
    private MyDatabaseHelper myDatabaseHelper;
    private SharedPreferences sharedPreferences;
    boolean isFirebaseMode;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);
        layout = findViewById(R.id.partyLayout);
        getSupportActionBar().hide();

        myDatabaseHelper = new MyDatabaseHelper(this);// Getting the value of firebase_mode from sharedPreference
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isFirebaseMode = sharedPreferences.getBoolean("firebase_mode", false);
        progressBar =findViewById(R.id.progress_bar);
        listView = (ListView) findViewById(R.id.listview_party);
        List<PartyListModel> partyList;
        try (MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(this)) {
            progressBar.setVisibility(View.VISIBLE);
            myDatabaseHelper.getAllParties(new MyDatabaseHelper.OnPartyDataListener() {
                @Override
                public void onPartyDataReceived(List<PartyListModel> partyList) {
                    // Populate the ListView with the fetched partyList
                    if (partyList != null) {
                        PartyListAdapter adapter = new PartyListAdapter(Party.this, partyList);
                        listView.setAdapter(adapter);
                        listView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        // Handle null partyList or error condition
                        Log.e(TAG, "Failed to fetch party data.");
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDatabaseHelper.close();
    }


    public void addParty(View v) {
        try {
            final Dialog dialog2 = new Dialog(Party.this);
            dialog2.setContentView(R.layout.add_party);
            Button addBtn = dialog2.findViewById(R.id.add_party_addButton);

            addBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                    String datetime = ft.format(new Date());
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

                    // Call the addParty method with a callback to handle UI update
                    myDatabaseHelper.addParty(isFirebaseMode, partyName.trim(), Double.parseDouble(partyAmount), new MyDatabaseHelper.OnPartyAddedListener() {
                        @Override
                        public void onPartyAdded() {
                            // Dismiss the dialog
                            dialog2.dismiss();

                            // Show progress bar
                            progressBar.setVisibility(View.VISIBLE);

                            // Fetch the updated party list asynchronously
                            myDatabaseHelper.getAllParties(new MyDatabaseHelper.OnPartyDataListener() {
                                @Override
                                public void onPartyDataReceived(List<PartyListModel> partyList) {
                                    // Populate the ListView with the updated party list
                                    if (partyList != null) {
                                        PartyListAdapter adapter = new PartyListAdapter(Party.this, partyList);
                                        listView.setAdapter(adapter);

                                        // Hide progress bar
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        // Handle null partyList or error condition
                                        Log.e(TAG, "Failed to fetch party data after addition.");
                                    }
                                }
                            });
                        }
                    }, Party.this, layout);
                }
            });
            dialog2.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goBack(View v){
        finish();
    }
}