package com.example.walletmanager;


public class DBManager {
    public String DBNAME = "WalletManager.db";
    String TABLE_EXPENSE = "CREATE TABLE IF NOT EXISTS EXPENSE(id INTEGER PRIMARY KEY AUTOINCREMENT,date DATETIME,time TEXT,party_name TEXT,amount double)";
    String TABLE_PARTY = "CREATE TABLE IF NOT EXISTS PARTY(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,balance double)";

}
