package com.example.walletmanager.Database;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.walletmanager.Activity.Party;
import com.example.walletmanager.Models.MyData;
import com.example.walletmanager.Models.PartyListModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    // Database related constants
    private static final String DATABASE_NAME = "WalletManager.db";
    private static final int DATABASE_VERSION = 1;

    // Table related constants
    String TABLE_NAME = "Lend";
    String COLUMN_TITLE = "title";
    String COLUMN_DESCRIPTION = "description";
    String COLUMN_DATE = "date";

    SQLiteDatabase db = getReadableDatabase();

    // Constructor
    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public List<MyData> getDataWithinDateRange(String partyName, String fromDate, String toDate) {
        List<MyData> dataList = new ArrayList<>();

        Log.d(TAG, "viewData: =" + fromDate + toDate);
        String query = "SELECT * FROM LEND WHERE date BETWEEN '"+fromDate+"' AND '"+toDate+"' And party_name = '"+partyName+"'";
        Cursor cursor = db.rawQuery(query, null);
        double total= 0.0;
        if (cursor.moveToFirst()) {
            do {
                int idColumnIndex = cursor.getColumnIndex("id");
                String id = (idColumnIndex != -1) ? cursor.getString(idColumnIndex) : "";

                int dateColumnIndex = cursor.getColumnIndex("date");
                String date = (dateColumnIndex != -1) ? cursor.getString(dateColumnIndex) : "";

                int timeColumnIndex = cursor.getColumnIndex("time");
                String time = (timeColumnIndex != -1) ? cursor.getString(timeColumnIndex) : "";

                int amountColumnIndex = cursor.getColumnIndex("amount");
                String amount = (amountColumnIndex != -1) ? cursor.getString(amountColumnIndex) : "";

                int partyNameColumnIndex = cursor.getColumnIndex("party_name");
                String party_name = (partyNameColumnIndex != -1) ? cursor.getString(partyNameColumnIndex) : "";

                int narrationColumnIndex = cursor.getColumnIndex("narration");
                String narration = (narrationColumnIndex != -1) ? cursor.getString(narrationColumnIndex) : "";

                total += Double.parseDouble(amount);
                MyData data = new MyData(id, date, time, amount, party_name,narration,total);
//                Log.d(TAG, " party: =" + party_name +" amount: =" +  amount);
                dataList.add(data);
            } while (cursor.moveToNext());
        }


        cursor.close();
        db.close();
        return dataList;
    }

    // Helper method to convert a Calendar object to a string format
    private String convertDateToString(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public List<PartyListModel> getAllParties() {
        List<PartyListModel> partyList = new ArrayList<>();
        Cursor cursor = db.query("PARTY", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                Log.d(TAG, "getAllParties: " + name);
                double balance = cursor.getDouble(cursor.getColumnIndex("balance"));
                PartyListModel party = new PartyListModel(id, name, balance);
                partyList.add(party);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return partyList;
    }
}
