package com.example.walletmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.walletmanager.Database.DBManager;
import com.example.walletmanager.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {
    RelativeLayout layout;
    FloatingActionButton mAddFab, mAddAlarmFab, mAddPersonFab;
    TextView addAlarmActionText, addPersonActionText;
    Boolean isAllFabsVisible;
    Button partyButton;

    ArrayAdapter<String> adapter2;
    String[] arraylist;
    SQLiteDatabase mydb;
    DBManager db = new DBManager();

    EditText textPartySearch;
    TextView partyName, dateView;
    TextInputLayout amountInputLayout, narrationInputLayout;
    String amountString, narrationString, dateFinal;
    String day, month, year;
    MaterialDatePicker.Builder materialDateBuilder;
    private Calendar calendar;
    Date c = new Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
    String currTime = time.format(c);
    StringBuilder date;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        getSupportActionBar().hide();
        layout = findViewById(R.id.expenselayout);

        // DATE PICKER
        try {
            calendar = Calendar.getInstance();
            year = String.valueOf(calendar.get(Calendar.YEAR));
            dateView = findViewById(R.id.dateTextView);
            int newMonth = calendar.get(Calendar.MONTH) + 1;
            int newDay = calendar.get(Calendar.DAY_OF_MONTH);
            month = db.dateOrTimeConverter(newMonth);
            day = db.dateOrTimeConverter(newDay);
            showDate(year, month, day);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Activity componnets
        partyButton = findViewById(R.id.selectPartyButton);

        // FAB button
        mAddFab = findViewById(R.id.add_fab);
        mAddAlarmFab = findViewById(R.id.add_alarm_fab);
        mAddPersonFab = findViewById(R.id.add_person_fab);
        addAlarmActionText = findViewById(R.id.add_alarm_action_text);
        addPersonActionText = findViewById(R.id.add_person_action_text);
        amountInputLayout = findViewById(R.id.amountTextView);
        narrationInputLayout = findViewById(R.id.narrationTextView);

        mAddAlarmFab.setVisibility(View.GONE);
        mAddPersonFab.setVisibility(View.GONE);
        addAlarmActionText.setVisibility(View.GONE);
        addPersonActionText.setVisibility(View.GONE);

        isAllFabsVisible = false;

        mAddFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {

                            mAddAlarmFab.show();
                            mAddPersonFab.show();
                            addAlarmActionText.setVisibility(View.VISIBLE);
                            addPersonActionText.setVisibility(View.VISIBLE);

                            isAllFabsVisible = true;
                        } else {

                            mAddAlarmFab.hide();
                            mAddPersonFab.hide();
                            addAlarmActionText.setVisibility(View.GONE);
                            addPersonActionText.setVisibility(View.GONE);

                            isAllFabsVisible = false;
                        }
                    }
                });

        mAddPersonFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Person Added", Toast.LENGTH_SHORT).show();
                    }
                });

        mAddAlarmFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Alarm Added", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDate(String year, String month, String day) {
        try {
            date = new StringBuilder().append(year).append("-")
                    .append(month).append("-").append(day);
            dateFinal = df.format(df.parse(date.toString()));
            dateView.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void selectParty(View v) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = (View) inflater.inflate(R.layout.select_party_list, null);
            alertDialog.setView(convertView);

            ListView lv = (ListView) convertView.findViewById(R.id.listView);
//        listAdapter = new CustomNewInvoiceAdapter(this, R.layout.batch_sales_list, new ArrayList<OrderListModel>());
            adapter2 = new ArrayAdapter<String>(this, R.layout.party_list, getPartyList());
            lv.setAdapter(adapter2);
            textPartySearch = (EditText) convertView.findViewById(R.id.partySearch);
            textPartySearch.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    adapter2.getFilter()
                            .filter(textPartySearch.getText().toString());
                }
            });
            final AlertDialog ad = alertDialog.show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter1, View v, int position, long arg3) {
                    String value = (String) adapter1.getItemAtPosition(position);
                    partyButton.setText(value);
//                partyName = value;
//                getBalance();
                    ad.dismiss();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String[] getPartyList() {
        List<String> partyList = new ArrayList<String>();
        try {
            mydb = openOrCreateDatabase(db.DBNAME, Context.MODE_PRIVATE, null);
            Cursor allrows = mydb.rawQuery("SELECT name FROM PARTY", null);
            if (allrows.moveToFirst()) {
                do {
                    partyList.add(allrows.getString(0));
                } while (allrows.moveToNext());
            }
            mydb.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
        // Create sequence of items
        final String[] party = partyList.toArray(new String[partyList.size()]);
        return party;
    }

    public void saveButton(View v) {
        try {
            amountString = amountInputLayout.getEditText().getText().toString();
            narrationString = narrationInputLayout.getEditText().getText().toString();
            String date = dateFinal;
            String party = partyButton.getText().toString();
            if (party.equals("PARTY")) {
                Snackbar.make(layout, "Please select a party", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (amountString.isEmpty()) {
                amountInputLayout.setError("Field cannot be empty");
                return;
            }

            mydb = openOrCreateDatabase(db.DBNAME, 0, null);
            mydb.execSQL("INSERT INTO EXPENSE (date,time,party_name,amount,narration) VALUES ('" + date + "','" + currTime + "','" + party + "','" + amountString + "','" + narrationString + "')");
            mydb.close();
            Snackbar.make(layout, "Expense recorded", Snackbar.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    arg2 = arg2 + 1;
                    // arg3 = day
                    showDate(String.valueOf(arg1), String.valueOf(arg2), String.valueOf(arg3));
                }
            };

    public void selectDate(View v) {

        try {

            showDialog(999);
            Toast.makeText(getApplicationContext(), "ca",
                    Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}