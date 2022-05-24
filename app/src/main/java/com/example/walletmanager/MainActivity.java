package com.example.walletmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
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

    // DATE and TIME
    DBManager db = new DBManager();
    Date c = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    String date = sdf.format(c);
    SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
    String currTime = time.format(c);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout);
    }

    public void addExpense(View v) {
        startActivity(new Intent(getApplicationContext(), ExpenseActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addAmount(String amount) {

        String party = "";
        mydb = openOrCreateDatabase(db.DBNAME, 0, null);
        mydb.execSQL("INSERT INTO EXPENSE (date,time,party_name,amount) VALUES ('" + date + "','" + currTime + "','" + party + "','" + amount + "')");
        mydb.close();
        Snackbar.make(layout, "Expense added !!", Snackbar.LENGTH_SHORT).show();
    }

    public void getReports(View v) {
        Snackbar.make(layout, "Feature not available yet !!", Snackbar.LENGTH_SHORT).show();
    }

    public void partyDetails(View v) {
        startActivity(new Intent(getApplicationContext(), Party.class));
//        Snackbar.make(layout,"Feature not available yet !!",Snackbar.LENGTH_SHORT).show();
    }
}