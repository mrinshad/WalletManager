package com.example.walletmanager.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class Settings extends AppCompatActivity {

    private static final String TAG = "Settings";
    private FirebaseAuth mAuth;
    ArrayList<SettingsListModel> dataModels;
    private static SettingsListCustomAdapter adapter;
    ListView listView;
    SQLiteDatabase mydb;
    DBManager db = new DBManager();
    MyDatabaseHelper myDatabaseHelper;

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
//        listview
        listView = (ListView) findViewById(R.id.settings_actions_list);
        dataModels = new ArrayList<>();
        dataModels.add(new SettingsListModel("Logout"));
        dataModels.add(new SettingsListModel("Sync"));
        dataModels.add(new SettingsListModel("Db Updates"));
        dataModels.add(new SettingsListModel("About"));
        adapter = new SettingsListCustomAdapter(dataModels, getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("PrivateResource")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SettingsListModel dataModel = dataModels.get(position);
                switch (dataModel.getButtonName()) {
                    case "Logout":
                        showLogoutAlert(Settings.this);
                        break;
                    case "Sync":
                        syncDataToServer();
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
            myDatabaseHelper.syncDataToFirebase("Expense");
            myDatabaseHelper.syncDataToFirebase("Lend");
            myDatabaseHelper.syncDataToFirebase("Borrow");
            myDatabaseHelper.insertPartyToFirebase();

        } catch (Exception e) {
            Log.e(TAG, "syncDataToServer: ", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isNetworkAvailable()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
//                if (logout.getVisibility() == View.VISIBLE){
//                    logout.setVisibility(View.INVISIBLE);
//                }
            } else {
//                if (logout.getVisibility() == View.INVISIBLE){
//                    logout.setVisibility(View.VISIBLE);
//                }
            }
        } else {
//            logout.setVisibility(View.INVISIBLE);
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
                clearAllDataFromDatabase(context,dialog);
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

    private void clearAllDataFromDatabase(Context context,DialogInterface dialog) {
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

}