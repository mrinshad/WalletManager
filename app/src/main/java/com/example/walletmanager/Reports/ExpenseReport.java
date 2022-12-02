package com.example.walletmanager.Reports;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.walletmanager.DBManager;
import com.example.walletmanager.Models.ExpenseReportModel;
import com.example.walletmanager.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpenseReport extends AppCompatActivity {
    SQLiteDatabase mydb;
    DBManager db = new DBManager();
    private ArrayList<ExpenseReportModel> expenseReportModel = new ArrayList<ExpenseReportModel>();
    ArrayList arrayList = new ArrayList();
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);
        getSupportActionBar().hide();
        getExpenseDataFromDB();

        ListView listView = findViewById(R.id.listView);
        adapter = new SimpleAdapter(this, arrayList,
                R.layout.list_report_expense, new String[]{"sino", "party",
                "narration", "amount",}, new int[]{
                R.id.idTextView, R.id.partyNameTextView, R.id.narrationTextView, R.id.amountTextView});

        listView.setAdapter(adapter);
    }

    private void getExpenseDataFromDB() {
        expenseReportModel.clear();
        mydb = openOrCreateDatabase(db.DBNAME, MODE_PRIVATE, null);
        Cursor allrows = mydb.rawQuery("SELECT party_name,narration,amount FROM EXPENSE"
//                        +" WHERE date BETWEEN "+date1+" AND "+date2
                , null);
        if (allrows.moveToFirst()) {
            do {
                String partyname = allrows.getString(0);
                String narration = allrows.getString(1);
                double amount = allrows.getDouble(2);
                expenseReportModel.add(new ExpenseReportModel(partyname, narration, amount));
            } while (allrows.moveToNext());
        }
        mydb.close();

        arrayList.clear();
        HashMap<String, String> expenseData;

        for (int x = 0; x < expenseReportModel.size(); x++) {

            ExpenseReportModel expenseDetial = expenseReportModel.get(x);

            expenseData = new HashMap<String, String>();

            expenseData.put("sino", " " + String.valueOf(x + 1)+".");
            expenseData.put("party", expenseDetial.getPatyName());
            expenseData.put("narration", expenseDetial.getNarration());
            expenseData.put("amount", String.valueOf("₹" + expenseDetial.getAmount()));

            arrayList.add(expenseData);
        }
    }
    public void goBack(View v){
        finish();
    }
}