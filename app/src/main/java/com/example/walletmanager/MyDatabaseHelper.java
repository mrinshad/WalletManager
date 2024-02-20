package com.example.walletmanager;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    // Constructor
    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the database table
//        String createTableQuery = "CREATE TABLE " + TABLE_NAME + "(" +
//                COLUMN_TITLE + " TEXT," +
//                COLUMN_DESCRIPTION + " TEXT," +
//                COLUMN_DATE + " TEXT" +
//                ")";
//        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing table and recreate it
//        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
//        db.execSQL(dropTableQuery);
//        onCreate(db);
    }

    // Method to retrieve data within the specified date range
    public List<MyData> getDataWithinDateRange(String partyName,String fromDate, String toDate) {
        List<MyData> dataList = new ArrayList<>();

        Log.d(TAG, "viewData: =" + fromDate + toDate);
        // Convert the from and to dates to a string format suitable for querying
//        String fromDateString = convertDateToString(fromDate);
//        String toDateString = convertDateToString(toDate);

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM Lend WHERE date BETWEEN '"+fromDate+"' AND '"+toDate+"' And party_name = '"+partyName+"'";
        Cursor cursor = db.rawQuery(query, null);
        double total= 0.0;
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String amount = cursor.getString(cursor.getColumnIndex("amount"));
                String party_name = cursor.getString(cursor.getColumnIndex("party_name"));
                String narration = cursor.getString(cursor.getColumnIndex("narration"));
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
}
