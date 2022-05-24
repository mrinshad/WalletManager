package com.example.walletmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    SQLiteDatabase mydb;
    RelativeLayout layout;
    TextView mainExpenseDisplay;

    // DATE and TIME

    private Calendar calendar;
    DBManager db = new DBManager();
    Date c = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    String date = sdf.format(c);
    SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
    String currTime = time.format(c);
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String dateFinal;
    String month, year, day;
    StringBuilder date1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout);
        mainExpenseDisplay = findViewById(R.id.mainExpenseDisplay);

        getTodayDate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTodayDate();
    }

    private void getTodayDate() {
        calendar = Calendar.getInstance();
        year = String.valueOf(calendar.get(Calendar.YEAR));
//        dateView = findViewById(R.id.dateTextView);
        int newMonth = calendar.get(Calendar.MONTH) + 1;
        int newDay = calendar.get(Calendar.DAY_OF_MONTH);
        month = db.dateOrTimeConverter(newMonth);
        day = db.dateOrTimeConverter(newDay);
        getExpense(year, month, day);
    }

    private void getExpense(String year, String month, String day) {
        try {
            date1 = new StringBuilder().append(year).append("-")
                    .append(month).append("-").append(day);
//            StringBuilder date2 = new StringBuilder().append(year).append("-")
//                    .append(month).append("-").append("01");
            dateFinal = df.format(df.parse(date1.toString()));
            mydb = openOrCreateDatabase(db.DBNAME, 0, null);
            Cursor allrows = mydb.rawQuery("select sum(amount) from EXPENSE where date = '" + date1 + "'", null);
            if (allrows.moveToFirst()) {
                do {
                    Log.d("TAG", "getExpense: on date:"+date1+" amount: "+"₹"+allrows.getString(0));
                    mainExpenseDisplay.setText("₹"+allrows.getString(0));
                } while (allrows.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addExpense(View v) {
        startActivity(new Intent(getApplicationContext(), ExpenseActivity.class));
    }
    public void borrowDetails(View v){
        startActivity(new Intent(getApplicationContext(), Borrow.class));
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void addAmount(String amount) {
//
//        String party = "";
//        mydb = openOrCreateDatabase(db.DBNAME, 0, null);
//        mydb.execSQL("INSERT INTO EXPENSE (date,time,party_name,amount) VALUES ('" + date + "','" + currTime + "','" + party + "','" + amount + "')");
//        mydb.close();
//        Snackbar.make(layout, "Expense added !!", Snackbar.LENGTH_SHORT).show();
//    }

    public void getReports(View v) {
        Snackbar.make(layout, "Feature not available yet !!", Snackbar.LENGTH_SHORT).show();
    }

    public void partyDetails(View v) {
        startActivity(new Intent(getApplicationContext(), Party.class));
//        Snackbar.make(layout,"Feature not available yet !!",Snackbar.LENGTH_SHORT).show();
    }
}