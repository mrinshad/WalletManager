package com.example.walletmanager.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.walletmanager.Adapters.SettingsListCustomAdapter;
import com.example.walletmanager.Database.DBManager;
import com.example.walletmanager.Database.MyDatabaseHelper;
import com.example.walletmanager.Models.SettingsListModel;
import com.example.walletmanager.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Settings extends AppCompatActivity implements MyDatabaseHelper.SyncCallback, SettingsListCustomAdapter.OnToggleChangeListener {

    private static final String TAG = "Settings";
    private FirebaseAuth mAuth;
    ArrayList<SettingsListModel> dataModels;
    private static SettingsListCustomAdapter adapter;
    ListView listView;
    SQLiteDatabase mydb;
    DBManager db = new DBManager();
    MyDatabaseHelper myDatabaseHelper;
    private SharedPreferences sharedPreferences;


    StringBuilder unsyncedTables = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mAuth = FirebaseAuth.getInstance();
        mydb = openOrCreateDatabase(db.DBNAME, 0, null);
        myDatabaseHelper = new MyDatabaseHelper(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        listview
        listView = (ListView) findViewById(R.id.settings_actions_list);
        dataModels = new ArrayList<>();
        dataModels.add(new SettingsListModel("Upload to server", hasUnsyncedData() ? unsyncedTables.toString() : "", false));
        dataModels.add(new SettingsListModel("Firebase Mode", "", true));
        dataModels.add(new SettingsListModel("About", "", false));
        adapter = new SettingsListCustomAdapter(dataModels, getApplicationContext());

        listView.setAdapter(adapter);
        adapter.setOnToggleChangeListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("PrivateResource")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SettingsListModel dataModel = dataModels.get(position);
                switch (dataModel.getButtonName()) {
                    case "Logout":
                        showLogoutAlert(Settings.this);
                        break;
                    case "Upload to server":
                        if (hasUnsyncedData()) {
                            syncDataToServer();
                        } else {
                            Toast.makeText(getApplicationContext(), "Nothing to sync", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Db Updates":
                        dbUpdates();
                        break;
                    case "About":
                        break;
                }

            }
        });
    }


    private boolean hasUnsyncedData() {
        getUnsyncedTables();
        if (unsyncedTables != null && unsyncedTables.length() > 0) {
            return true;
        } else {
            System.out.println("StringBuilder is null or empty");
            return false; // Placeholder return value, replace with actual implementation
        }

    }

    private String getUnsyncedTables() {
        MyDatabaseHelper databaseHelper = new MyDatabaseHelper(getApplicationContext());
        SQLiteDatabase mydb = databaseHelper.getWritableDatabase(); // Initialize StringBuilder to store table names

        try {
            // Check if there's unsynced data in each table
            int count = 0;
            String[] tables = {"EXPENSE", "LEND", "BORROW","PARTY"};
            for (int i = 0; i < tables.length; i++) {
                String table = tables[i];
                Cursor cursor = mydb.rawQuery("SELECT COUNT(*) FROM " + table + " WHERE synced = 0", null);
                if (cursor != null && cursor.moveToFirst()) {
                    int unsyncedCount = cursor.getInt(0);
                    if (unsyncedCount > 0) {
                        if (unsyncedTables.length() > 1)
                            unsyncedTables.append(", ");
                        unsyncedTables.append(table);
                    }
                    count++;
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getUnsyncedTables: ", e);
        }
        return unsyncedTables.toString();
    }


    private void dbUpdates() {
        try {
//            String alterPartyTable = "ALTER TABLE party ADD COLUMN synced INTEGER DEFAULT 0";
//
//            mydb.execSQL(alterPartyTable);
//            mydb.close();
        } catch (SQLException e) {
            Log.e(TAG, "dbUpdates: ", e);
        }
    }

    private void syncDataToServer() {
        try {
            myDatabaseHelper.syncELBDataToFirebase("EXPENSE", this);
            myDatabaseHelper.syncELBDataToFirebase("LEND", this);
            myDatabaseHelper.syncELBDataToFirebase("BORROW", this);
            myDatabaseHelper.insertPartyToFirebase();
            Toast.makeText(getApplicationContext(),"Data uploaded to server",Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception e) {
            Log.e(TAG, "syncDataToServer: ", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isNetworkAvailable()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                dataModels.add(new SettingsListModel("Logout", currentUser.getEmail(), false));
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showLogoutAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and message for the dialog
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.logout_alert);

        // Set positive button (OK button) and its action
        builder.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearAllDataFromDatabase(context, dialog);
            }
        });

        builder.setNegativeButton(R.string.decline, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create and show the alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void clearAllDataFromDatabase(Context context, DialogInterface dialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and message for the dialog
        builder.setTitle(R.string.clear_all_title);
        builder.setMessage(R.string.clear_all_message);

        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myDatabaseHelper.clearAllData(mydb);
                logoutFirebase(dialog);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.decline, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutFirebase(dialog);
                dialog.dismiss();
            }
        });

        // Create and show the alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void logoutFirebase(DialogInterface dialog) {
        GoogleSignInClient mGoogleSignInClient = LoginActivity.getGoogleSignInClient(getApplicationContext());
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mGoogleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                gotoLoginPage();
                            } else {
                                // Handle failure to revoke access
                            }
                        }
                    });
                } else {
                    // Handle failure to sign out
                }
            }
        });

        dialog.dismiss();
        gotoLoginPage();
    }

    private void gotoLoginPage() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    @Override
    public void onSyncSuccess() {
        finish();
    }

    @Override
    public void onSyncFailure(Exception e) {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onToggleChanged(boolean isChecked, int position) {
        SettingsListModel model = dataModels.get(position);
        if (model.getButtonName().equals("Firebase Mode")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firebase_mode", isChecked);
            editor.apply();
        } else {
            // Handle other toggle buttons if needed
        }
        Log.d(TAG, "onToggleChanged: " + isChecked);

    }
}