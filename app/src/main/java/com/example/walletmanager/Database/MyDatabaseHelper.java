package com.example.walletmanager.Database;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.example.walletmanager.Models.ELBModel;
import com.example.walletmanager.Models.ELBReportModel;
import com.example.walletmanager.Models.PartyListModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    // Database related constants
    private static final String DATABASE_NAME = "WalletManager.db";
    private static final int DATABASE_VERSION = 1;
    private boolean isFirebaseMode;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser ;

    // Constructor
    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        isFirebaseMode = sharedPreferences.getBoolean("firebase_mode", false);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
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

    public void getAllParties(OnPartyDataListener listener) {
        if (isFirebaseMode) {
            String uid = mAuth.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PARTY");
            getPartyFromFirebase(uid, databaseReference, listener);
        } else {
            SQLiteDatabase db = getReadableDatabase(); // Open a new database connection
            Cursor cursor = db.query("PARTY", null, null, null, null, null, null);

            List<PartyListModel> partyList = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    double balance = cursor.getDouble(cursor.getColumnIndex("balance"));
                    PartyListModel party = new PartyListModel(id, name, balance);
                    partyList.add(party);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close(); // Close the database connection

            listener.onPartyDataReceived(partyList); // Notify listener with the partyList
        }
    }


    public interface SyncCallback {
        void onSyncSuccess();
        void onSyncFailure(Exception e);
    }
    public interface OnPartyDataListener {
        void onPartyDataReceived(List<PartyListModel> partyList);
    }

    public void getPartyFromFirebase(String uid, DatabaseReference databaseReference, OnPartyDataListener listener) {
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PartyListModel> partyList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot partySnapshot : dataSnapshot.getChildren()) {
                        String id = partySnapshot.child("id").getValue(String.class);
                        String name = partySnapshot.child("name").getValue(String.class);
                        double balance = partySnapshot.child("balance").getValue(Double.class);
                        PartyListModel party = new PartyListModel(id, name, balance);
                        partyList.add(party);
                    }
                }
                listener.onPartyDataReceived(partyList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database Error: " + databaseError.getMessage());
                listener.onPartyDataReceived(null); // Notify listener about the error
            }
        });
    }

    public void syncELBDataToFirebase(String tablename, SyncCallback callback) {
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
                SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                String id = ft.format(new Date());
                String idOld = cursor.getString(cursor.getColumnIndexOrThrow("id"));
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
                                updateSyncedField(idOld, true, db,tablename);
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
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PARTY").child(uid);

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
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));
                int syncedValue = cursor.getInt(cursor.getColumnIndexOrThrow("synced"));

                // Check if the synced field is false (0)
                if (syncedValue == 0) {
                    PartyListModel party = new PartyListModel(id, name,balance); // Renamed from 'title'

                    // Insert data into Realtime Database under the user's uid
                    databaseReference.child(party.getName()).setValue(party)
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
    public interface OnPartyAddedListener {
        void onPartyAdded();
    }

    public void addParty(boolean status, String partyName, double partyAmount, OnPartyAddedListener listener, Context context, RelativeLayout layout) {

        if (status){

            String uid = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PARTY").child(uid);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Check if the desired value exists within the data
                        if (dataSnapshot.hasChild(partyName)) { // Replace "specificField" with your actual field name
                            Snackbar.make(layout, "Party Already Exists", Snackbar.LENGTH_SHORT).show();
                            // The value exists, handle it here
                        } else {
                            SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                            String datetime = ft.format(new Date());
                            PartyListModel party = new PartyListModel(datetime, partyName,partyAmount);
                            databaseReference.child(party.getName()).setValue(party);
                        }
                    } else {
                        // Get a reference to the database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PARTY");
                        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                        String datetime = ft.format(new Date());
                        PartyListModel party = new PartyListModel(datetime, partyName,partyAmount);
                        databaseReference.child(uid).child(party.getName()).setValue(party);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("TAG", "Failed to read data", databaseError.toException());
                }
            });
            listener.onPartyAdded();
        }else {
            SQLiteDatabase db = this.getWritableDatabase();

            // Check if a party with the same name already exists
            String selectQuery = "SELECT * FROM PARTY WHERE name = ?";
            Cursor cursor = db.rawQuery(selectQuery, new String[]{partyName});

            if (cursor.moveToFirst()) {
                // Party with the same name already exists
                cursor.close();
                db.close();

                // Show an error message or take appropriate action
                showDuplicatePartyError(context, partyName, layout);
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
                showPartyAddedSuccessfully(context, layout);
            } else {
                // Error adding party
                showErrorAddingParty(context, layout);
            }

            db.close();
            listener.onPartyAdded();
        }
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

    public void saveELB(String id,boolean status,String tableName,SQLiteDatabase mydb, String date, String time, String partyName, double amount, String narration) {
        if (status) {
            String uid = mAuth.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(tableName).child(uid);
            ELBModel entry = new ELBModel(id, date, time.toString(), partyName,amount, narration);
            databaseReference.child(entry.getId()).setValue(entry);
        } else {
            mydb.execSQL("INSERT INTO " + tableName + " (date, time, party_name, amount, narration) VALUES (?, ?, ?, ?, ?)",
                    new Object[]{date, time, partyName, amount, narration});
        }
    }

}
