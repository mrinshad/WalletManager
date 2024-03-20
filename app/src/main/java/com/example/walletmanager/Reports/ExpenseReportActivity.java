package com.example.walletmanager.Reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.walletmanager.Database.MyDatabaseHelper;
import com.example.walletmanager.Models.ELBReportModel;
import com.example.walletmanager.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpenseReportActivity extends Fragment {

    private ArrayList<ELBReportModel> ELBReportModel = new ArrayList<>();
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private SimpleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_expense_report, container, false);

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
        ELBReportModel = dbHelper.getELBDataFromDB("EXPENSE");

        ListView listView = view.findViewById(R.id.listView);
        adapter = new SimpleAdapter(getContext(), arrayList, R.layout.list_report_elb, new String[]{"sino", "party", "narration", "amount"}, new int[]{R.id.idTextView, R.id.partyNameTextView, R.id.narrationTextView, R.id.amountTextView});
        listView.setAdapter(adapter);

        populateArrayList();

        return view;
    }

    private void populateArrayList() {
        arrayList.clear();
        HashMap<String, String> expenseData;
        for (int x = 0; x < ELBReportModel.size(); x++) {
            ELBReportModel expenseDetail = ELBReportModel.get(x);
            expenseData = new HashMap<>();
            expenseData.put("sino", " " + String.valueOf(x + 1) + ".");
            expenseData.put("party", expenseDetail.getPatyName());
            expenseData.put("narration", expenseDetail.getNarration());
            expenseData.put("amount", String.valueOf("₹" + expenseDetail.getAmount()));
            arrayList.add(expenseData);
        }
        adapter.notifyDataSetChanged();
    }
}