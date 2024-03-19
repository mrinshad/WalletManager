package com.example.walletmanager.Reports;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.walletmanager.Database.DBManager;
import com.example.walletmanager.Models.ELBReportModel;
import com.example.walletmanager.Database.MyDatabaseHelper;
import com.example.walletmanager.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class LendingReportActivity extends AppCompatActivity {
    private ArrayList<ELBReportModel> ELBReportModel = new ArrayList<ELBReportModel>();
    ArrayList arrayList = new ArrayList();
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lending_report);
        getSupportActionBar().hide();

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        ELBReportModel = dbHelper.getELBDataFromDB("LEND");

        ListView listView = findViewById(R.id.listView);
        adapter = new SimpleAdapter(this, arrayList,
                R.layout.list_report_expense, new String[]{"sino", "party",
                "narration", "amount"}, new int[]{
                R.id.idTextView, R.id.partyNameTextView, R.id.narrationTextView, R.id.amountTextView});

        listView.setAdapter(adapter);
        populateArrayList();
    }

    private void populateArrayList() {
        arrayList.clear();
        HashMap<String, String> expenseData;
        for (int x = 0; x < ELBReportModel.size(); x++) {
            ELBReportModel expenseDetail = ELBReportModel.get(x);
            expenseData = new HashMap<>();
            expenseData.put("sino", " " + String.valueOf(x + 1) + ".");
            expenseData.put("party", expenseDetail.getPatyName());
            expenseData.put("narration", expenseDetail.getNarration());
            expenseData.put("amount", String.valueOf("₹" + expenseDetail.getAmount()));
            arrayList.add(expenseData);
        }
    }
    public void goBack(View v){
        finish();
    }
}