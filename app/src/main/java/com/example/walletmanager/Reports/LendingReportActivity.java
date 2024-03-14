package com.example.walletmanager.Reports;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.walletmanager.Adapters.DBAdapter;
import com.example.walletmanager.Database.DBManager;
import com.example.walletmanager.Models.MyData;
import com.example.walletmanager.Database.MyDatabaseHelper;
import com.example.walletmanager.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LendingReportActivity extends AppCompatActivity {
    private Calendar fromDate;
    private Calendar toDate;
    private TextView fromDateTextView,totalAmount;
    private TextView toDateTextView;
    Button partyButton;
    EditText textPartySearch;
    ArrayAdapter<String> adapter2;
    SQLiteDatabase mydb;
    DBManager db = new DBManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lending_report);
        getSupportActionBar().hide();
        toDateTextView = findViewById(R.id.toDateTextView);
        fromDateTextView = findViewById(R.id.fromDateTextView);
        totalAmount = findViewById(R.id.totalAmount);
        partyButton = findViewById(R.id.selectPartyButton);
        // Attach click listeners to the TextViews
        fromDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFromDateDialog();
            }
        });

        toDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToDateDialog();
            }
        });
    }

    // The showFromDateDialog() and showToDateDialog() methods from the previous response go here...
    private void showFromDateDialog() {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Create a Calendar instance and set the selected date
                fromDate = Calendar.getInstance();
                fromDate.set(year, monthOfYear, dayOfMonth);

                // Update the UI or perform any necessary actions with the selected date
                // For example, display the selected date in a TextView
                String formattedDate = formatDate(fromDate);
                fromDateTextView.setText(formattedDate);
            }
        }, year, month, day);

        // Show the DatePickerDialog

        datePickerDialog.show();
    }

    // The formatDate() method from the previous response goes here...
    private void showToDateDialog() {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Create a Calendar instance and set the selected date
                toDate = Calendar.getInstance();
                toDate.set(year, monthOfYear, dayOfMonth);

                // Update the UI or perform any necessary actions with the selected date
                // For example, display the selected date in a TextView
                String formattedDate = formatDate(toDate);
                toDateTextView.setText(formattedDate);
            }
        }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private String formatDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
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
    public void viewData(View v) {
        String partyName = partyButton.getText().toString();
        String fDate = fromDateTextView.getText().toString();
        String tDate = toDateTextView.getText().toString();

        MyDatabaseHelper databaseHelper = new MyDatabaseHelper(getApplicationContext());



        // Call the getDataWithinDateRange method and obtain the data list
        List<MyData> dataList = databaseHelper.getDataWithinDateRange(partyName,fDate, tDate);
        // Iterate over the data list and insert each item into the database
        double total = 0.0;
        for (MyData data : dataList) {
            total = data.getTotal();
        }

        ListView listView = findViewById(R.id.lenderReportListview);
        DBAdapter adapter = new DBAdapter(this, dataList);
        listView.setAdapter(adapter);
        totalAmount.setText(String.valueOf("₹" + total));
    }
}
