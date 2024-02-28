package com.example.walletmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.walletmanager.Database.DBManager;
import com.example.walletmanager.R;
import com.example.walletmanager.Reports.LendingReportActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase mydb;
    RelativeLayout layout;
    TextView mainExpenseDisplay;

    private FirebaseAuth mAuth;
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
    TextView connected_button;
    View view_indicator;
    public String CONNECTION_STATUS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        actionBar();
        layout = findViewById(R.id.layout);
        mainExpenseDisplay = findViewById(R.id.mainExpenseDisplay);

        getTodayDate();
    }

    private final BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkAvailable()) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null){
                    connected_button.setText("Disconnected");
                    view_indicator.setBackgroundResource(R.drawable.round_red);
                }else{
                    connected_button.setText("Connected");
                    view_indicator.setBackgroundResource(R.drawable.round_green);
                }
            } else {
                connected_button.setText("Disconnected");
                view_indicator.setBackgroundResource(R.drawable.round_red);
            }
        }
    };
    public void actionBar() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_main_menu, null);
        connected_button = mCustomView.findViewById(R.id.button_text);
        view_indicator = mCustomView.findViewById(R.id.online_indicator);
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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
                    Log.d("TAG", "getExpense: on date:" + date1 + " amount: " + "₹" + allrows.getString(0));
                    String amount = "0.00";
                    if (allrows.getString(0) != null) {
                        amount = allrows.getString(0);
                    }
                    mainExpenseDisplay.setText("₹" + amount);
                } while (allrows.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addExpense(View v) {
        startActivity(new Intent(getApplicationContext(), ExpenseActivity.class));
    }

    public void lendDetails(View v) {
        startActivity(new Intent(getApplicationContext(), LendActivity.class));
    }


    public void getReports(View v) {
        final String[] reportOptions = {"Expense Report", "Lending Report", "Party Member Details"};

//        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a report");
        builder.setItems(reportOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                launchReportActivity(which);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void launchReportActivity(int optionIndex) {
        switch (optionIndex) {
            case 0:
//                Intent expenseIntent = new Intent(MainActivity.this, ExpenseReport.class);
//                startActivity(expenseIntent);
                Snackbar.make(layout, "Coming soon!", Snackbar.LENGTH_SHORT).show();
                break;
            case 1:
                Intent lendingIntent = new Intent(MainActivity.this, LendingReportActivity.class);
                startActivity(lendingIntent);
                break;
            case 2:
//                Intent partyIntent = new Intent(MainActivity.this, PartyMemberDetailsActivity.class);
//                startActivity(partyIntent);
                Snackbar.make(layout, "Coming soon!", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    public void partyDetails(View v) {
        startActivity(new Intent(getApplicationContext(), Party.class));
    }

    public void gotoSettings(View v){
        startActivity(new Intent(getApplicationContext(), Settings.class));
    }
}