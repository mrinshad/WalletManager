package com.example.walletmanager.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.walletmanager.DBManager;
import com.example.walletmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG ="GHRR....." ;
    SQLiteDatabase mydb;
    DBManager db = new DBManager();

    private ProgressBar progressBar;
    private TextView progressText;
    private LinearLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        contentLayout = findViewById(R.id.contentLayout);

        new CountDownTimer(3000, 1000) { // Increased the countdown to 3 seconds for better visibility

            public void onTick(long millisUntilFinished) {
                // This method will be called every 1 second (you can leave it empty)
            }

            public void onFinish() {
                if (isInternetAvailable()) {
                    // Internet is available, implement online mode logic here
                    // For example, you can navigate to the next activity for online mode
                    progressBar.setVisibility(View.GONE);
                    progressText.setText("Internet connection available");
                    
                    statusWindow();
//                    startNextActivity();
                } else {
                    // No internet connection, implement offline mode logic here
                    // For now, it starts the MainActivity, update it as per your requirements
                    startNextActivity();
                }
            }
        }.start();

        createTables();
    }

    private void createTables() {
        mydb = openOrCreateDatabase(db.DBNAME, 0, null);

        // TABLE QUERIES
        mydb.execSQL(db.TABLE_EXPENSE);
        mydb.execSQL(db.TABLE_PARTY);
        mydb.execSQL(db.TABLE_LEND);

        mydb.close();
    }

    private void startNextActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private boolean isInternetAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }catch (Exception e){
            Log.d(TAG, "Connectivity Problem ");
        }
        return false;
    }

    private void statusWindow() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            startNextActivity();
        }else{
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

    }
}
