package com.example.walletmanager.Activity;

import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.walletmanager.Adapters.SettingsListCustomAdapter;
import com.example.walletmanager.Models.SettingsListModel;
import com.example.walletmanager.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {

    private static final String TAG = "Logout Page";
    private FirebaseAuth mAuth;
    ArrayList<SettingsListModel> dataModels;
    private static SettingsListCustomAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mAuth = FirebaseAuth.getInstance();

//        listview
        listView = (ListView) findViewById(R.id.settings_actions_list);
        dataModels = new ArrayList<>();
        dataModels.add(new SettingsListModel("Logout"));
        dataModels.add(new SettingsListModel("About"));
        adapter = new SettingsListCustomAdapter(dataModels, getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("PrivateResource")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SettingsListModel dataModel = dataModels.get(position);
                if (dataModel.getButtonName().equals("Logout")) {
                   showAlert(Settings.this);

                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: " + "Working...");
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

    private void showAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and message for the dialog
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.logout_alert);

        // Set positive button (OK button) and its action
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void gotoLoginPage() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

}