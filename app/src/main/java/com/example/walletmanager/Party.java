package com.example.walletmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class Party extends AppCompatActivity {

    SQLiteDatabase mydb;
    DBManager db = new DBManager();
    RelativeLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);
    layout = findViewById(R.id.partyLayout);
    }

    public void addParty(View v){
        try {
            final Dialog dialog2 = new Dialog(Party.this);
            dialog2.setContentView(R.layout.add_party);

//        TextInputLayout empPassword = (TextInputLayout) dialog2.findViewById(R.id.admin_code_edt);
//        empPassword.setHint("Enter Admin code");
            Button addBtn = (Button) dialog2.findViewById(R.id.add_party_addButton);
//        Button cancelBtn = (Button) dialog2.findViewById(R.id.cancel_btn);

            addBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
//                TextInputLayout empPassword = (TextInputLayout) dialog2.findViewById(R.id.admin_code_edt);

//                String empPass = empPassword.getEditText().getText().toString();
//                if (empPass == null || empPass.equals("")) {
//                    empPassword.setError("Please input Amount");
//                } else {
//                    addAmount(empPass);
//                }
                    EditText partyNameEditText, partyAmountEditText;
                    partyNameEditText = dialog2.findViewById(R.id.add_party_partyName);
                    partyAmountEditText = dialog2.findViewById(R.id.add_party_partyAmount);

                    String partyName, partyAmount;

                    partyName = partyNameEditText.getText().toString();
                    partyAmount = partyAmountEditText.getText().toString();

                    if (partyName.isEmpty()) {
                        partyAmountEditText.setError("Please input name");
                        return;
                    }

                    if (partyAmount.isEmpty()) {
                        partyAmountEditText.setError("Please input amount");
                        return;
                    }

                    mydb = openOrCreateDatabase(db.DBNAME, 0, null);
                    mydb.execSQL("INSERT INTO PARTY (name,balance) VALUES ('" + partyName + "'," + Double.parseDouble(partyAmount) + ")");
                    mydb.close();

                    Snackbar.make(layout,"Party Added successfully",Snackbar.LENGTH_SHORT).show();

                dialog2.dismiss();
                }
            });

//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog2.dismiss();
//            }
//
//
//        });
            dialog2.show();
        }catch (Exception e){
            e.printStackTrace();
        }
}
}