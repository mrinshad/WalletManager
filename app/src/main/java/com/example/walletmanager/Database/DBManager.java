package com.example.walletmanager.Database;


import static android.content.ContentValues.TAG;

import android.util.Log;



public class DBManager {
    public String DBNAME = "WalletManager.db";
    public String TABLE_EXPENSE = "CREATE TABLE IF NOT EXISTS EXPENSE(id INTEGER PRIMARY KEY AUTOINCREMENT,date DATETIME,time TEXT,party_name TEXT,amount double,narration TEXT)";
    public String TABLE_PARTY = "CREATE TABLE IF NOT EXISTS PARTY(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,balance double)";
    public String TABLE_LEND = "CREATE TABLE IF NOT EXISTS LEND(id INTEGER PRIMARY KEY AUTOINCREMENT,date DATETIME,time TEXT,party_name TEXT,amount double,narration TEXT)";

    public String dateOrTimeConverter(int num) {
        String newNum = "";
        if (num < 10) {
            newNum = "0" + String.valueOf(num);
        } else {
            newNum = String.valueOf(num);
        }
        Log.d(TAG, "dateOrTimeConverter: "+newNum);
        return newNum;
    }
}
