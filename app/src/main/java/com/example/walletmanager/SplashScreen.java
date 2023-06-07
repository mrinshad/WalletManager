package com.example.walletmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class SplashScreen extends AppCompatActivity {

    SQLiteDatabase mydb;
    DBManager db = new DBManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }

        }.start();

        createTables();
    }

    public void createTables() {
        mydb = openOrCreateDatabase(db.DBNAME, 0, null);

        // TABLE QUERIES
        mydb.execSQL(db.TABLE_EXPENSE);
        mydb.execSQL(db.TABLE_PARTY);
        mydb.execSQL(db.TABLE_LEND);

        mydb.close();
    }
}