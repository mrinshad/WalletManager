package com.example.walletmanager.Database;


import static android.content.ContentValues.TAG;

import android.util.Log;



public class DBManager {
    public String DBNAME = "WalletManager.db";
    String TABLE_NAME = "Lend";
    String COLUMN_TITLE = "title";
    String COLUMN_DESCRIPTION = "description";
    String COLUMN_DATE = "date";
    public String TABLE_EXPENSE = "CREATE TABLE IF NOT EXISTS EXPENSE(id INTEGER PRIMARY KEY AUTOINCREMENT,date DATETIME,time TEXT,party_name TEXT,amount double,narration TEXT,synced INTEGER DEFAULT 0)";
    public String TABLE_LEND = "CREATE TABLE IF NOT EXISTS LEND(id INTEGER PRIMARY KEY AUTOINCREMENT,date DATETIME,time TEXT,party_name TEXT,amount double,narration TEXT,synced INTEGER DEFAULT 0)";
    public String TABLE_BORROW = "CREATE TABLE IF NOT EXISTS BORROW(id INTEGER PRIMARY KEY AUTOINCREMENT,date DATETIME,time TEXT,party_name TEXT,amount double,narration TEXT,synced INTEGER DEFAULT 0)";
    public String TABLE_PARTY = "CREATE TABLE IF NOT EXISTS PARTY(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,balance double,synced INTEGER DEFAULT 0)";
    public String TABLE_SHARED_PREF = "CREATE TABLE IF NOT EXISTS SHARED_PREF(id INTEGER PRIMARY KEY AUTOINCREMENT,variable TEXT,value INTEGER DEFAULT 0)";

    public String dateOrTimeConverter(int num) {
        String newNum = "";
        if (num < 10) {
            newNum = "0" + String.valueOf(num);
        } else {
            newNum = String.valueOf(num);
        }
        return newNum;
    }
}
