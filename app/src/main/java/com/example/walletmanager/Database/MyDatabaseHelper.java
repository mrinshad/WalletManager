package com.example.walletmanager.Database;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.walletmanager.Models.ELBModel;
import com.example.walletmanager.Models.ELBReportModel;
import com.example.walletmanager.Models.PartyListModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

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

    // Helper method to convert a Calendar object to a string format
    private String convertDateToString(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public List<PartyListModel> getAllParties() {
        List<PartyListModel> partyList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase(); // Open a new database connection

        Cursor cursor = db.query("PARTY", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                double balance = cursor.getDouble(cursor.getColumnIndex("balance"));
                PartyListModel party = new PartyListModel(id, name, balance);
                partyList.add(party);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close(); // Close the database connection

        return partyList;
    }
    public interface SyncCallback {
        void onSyncSuccess();
        void onSyncFailure(Exception e);
    }


    public void syncDataToFirebase(String tablename, SyncCallback callback) {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(uid).child(tablename);

            // Get the data from the SQLite database
            SQLiteDatabase db = this.getReadableDatabase();
            String[] projection = {
                    "id",
                    "date",
                    "time",
                    "party_name", // Renamed from 'title'
                    "amount",
                    "narration",
                    "synced"
            };

            String selection = "synced = ?";
            String[] selectionArgs = {"0"};

            Cursor cursor = db.query(
                    tablename,   // Table name
                    projection,  // Columns to retrieve
                    selection,   // Selection clause
                    selectionArgs, // Selection args
                    null,        // Group by clause
                    null,        // Having clause
                    null         // Order by clause
            );

            // Iterate through the cursor and insert data into Firebase
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String partyName = cursor.getString(cursor.getColumnIndexOrThrow("party_name")); // Renamed from 'title'
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("narration"));
                int syncedValue = cursor.getInt(cursor.getColumnIndexOrThrow("synced"));

                // Check if the synced field is false (0)
                if (syncedValue == 0) {
                    ELBModel expense = new ELBModel(id, date, time, partyName, amount, description); // Renamed from 'title'

                    // Insert data into Realtime Database under the user's uid
                    databaseReference.child(expense.getId()).setValue(expense)
                            .addOnSuccessListener(aVoid -> {
                                // Data successfully added, update the synced field to true
                                updateSyncedField(id, true, db,tablename);
                                Log.w(TAG, "Entry added");
                                callback.onSyncSuccess();
                            })
                            .addOnFailureListener(e -> {
                                // Handle errors here
                                Log.w(TAG, "Error adding expense", e);
                                callback.onSyncFailure(e);
                            });
                }
            }

            // Close the cursor
            cursor.close();
        } else {
            // No user is signed in
            Log.w(TAG, "User is not signed in");
            callback.onSyncFailure(new Exception("User is not signed in"));
        }
    }

    public void insertPartyToFirebase(){
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(uid).child("Party");

            // Get the data from the SQLite database
            SQLiteDatabase db = this.getReadableDatabase();
            String[] projection = {
                    "id",
                    "name",
                    "balance",
                    "synced"
            };

            String selection = "synced = ?";
            String[] selectionArgs = {"0"};

            Cursor cursor = db.query(
                    "Party",   // Table name
                    projection,  // Columns to retrieve
                    selection,   // Selection clause
                    selectionArgs, // Selection args
                    null,        // Group by clause
                    null,        // Having clause
                    null         // Order by clause
            );

            // Iterate through the cursor and insert data into Firebase
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));
                int syncedValue = cursor.getInt(cursor.getColumnIndexOrThrow("synced"));

                // Check if the synced field is false (0)
                if (syncedValue == 0) {
                    PartyListModel party = new PartyListModel(id, name,balance); // Renamed from 'title'

                    // Insert data into Realtime Database under the user's uid
                    databaseReference.child(String.valueOf(party.getId())).setValue(party)
                            .addOnSuccessListener(aVoid -> {
                                // Data successfully added, update the synced field to true
                                updateSyncedField(String.valueOf(id), true, db,"party");
                            })
                            .addOnFailureListener(e -> {
                                // Handle errors here
                                Log.w(TAG, "Error adding expense", e);
                            });
                }
            }

            // Close the cursor
            cursor.close();
        } else {
            // No user is signed in
            Log.w(TAG, "User is not signed in");
        }
    }

    private void updateSyncedField(String id, boolean synced, SQLiteDatabase db,String tablename) {
        ContentValues values = new ContentValues();
        values.put("synced", synced ? 1 : 0);

        String selection = "id = ?";
        String[] selectionArgs = {id};

        db.update(tablename, values, selection, selectionArgs);
    }

    public void clearAllData(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        while (c.moveToNext()) {
            String tableName = c.getString(0);
            db.delete(tableName, null, null); // Delete all rows from the table
        }
        c.close();
    }

    public ArrayList<ELBReportModel> getELBDataFromDB(String tableName) {
        ArrayList<ELBReportModel> ELBReportModel = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT party_name, narration, amount FROM '" + tableName +"'", null);

        if (cursor.moveToFirst()) {
            do {
                String partyName = cursor.getString(0);
                String narration = cursor.getString(1);
                double amount = cursor.getDouble(2);
                ELBReportModel.add(new ELBReportModel(partyName, narration, amount));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return ELBReportModel;
    }

    public void addParty(String partyName, double partyAmount, Context context,RelativeLayout layout) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if a party with the same name already exists
        String selectQuery = "SELECT * FROM PARTY WHERE name = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{partyName});

        if (cursor.moveToFirst()) {
            // Party with the same name already exists
            cursor.close();
            db.close();

            // Show an error message or take appropriate action
            showDuplicatePartyError(context, partyName,layout);
            return;
        }

        cursor.close();

        // No duplicate found, insert the new party
        ContentValues values = new ContentValues();
        values.put("name", partyName);
        values.put("balance", partyAmount);
        long rowId = db.insert("PARTY", null, values);

        if (rowId != -1) {
            // Party added successfully
            showPartyAddedSuccessfully(context,layout);
        } else {
            // Error adding party
            showErrorAddingParty(context,layout);
        }

        db.close();
    }

    private void showDuplicatePartyError(Context context, String partyName, RelativeLayout layout) {
        String message = "Party '" + partyName + "' already exists.";
        Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void showPartyAddedSuccessfully(Context context, RelativeLayout layout) {
        Snackbar.make(layout, "Party added successfully", Snackbar.LENGTH_SHORT).show();
    }

    private void showErrorAddingParty(Context context, RelativeLayout layout) {
        Snackbar.make(layout, "Error adding party", Snackbar.LENGTH_SHORT).show();
    }
    public List<String> getPartyName(SQLiteDatabase mydb) {
        List<String> partyList = new ArrayList<>();
        Cursor cursor = mydb.rawQuery("SELECT name FROM PARTY", null);
        if (cursor.moveToFirst()) {
            do {
                partyList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return partyList;
    }

    public void saveBorrow(SQLiteDatabase mydb, String date, String time, String partyName, double amount, String narration) {
        mydb.execSQL("INSERT INTO BORROW (date, time, party_name, amount, narration) VALUES (?, ?, ?, ?, ?)",
                new Object[]{date, time, partyName, amount, narration});
    }

    public void saveExpense(SQLiteDatabase mydb, String date, String time, String partyName, double amount, String narration) {
        mydb.execSQL("INSERT INTO EXPENSE (date, time, party_name, amount, narration) VALUES (?, ?, ?, ?, ?)",
                new Object[]{date, time, partyName, amount, narration});
    }

    public void saveLend(SQLiteDatabase mydb, String date, String time, String partyName, double amount, String narration) {
        mydb.execSQL("INSERT INTO LEND (date, time, party_name, amount, narration) VALUES (?, ?, ?, ?, ?)",
                new Object[]{date, time, partyName, amount, narration});
    }


}
