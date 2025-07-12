package com.example.expensestracker;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TextView tvTotalExpenses;
    private RecyclerView rvExpenses;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        rvExpenses = findViewById(R.id.rvExpenses);

        // Set up RecyclerView
        expenseAdapter = new ExpenseAdapter(this, expenseList);
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        rvExpenses.setAdapter(expenseAdapter);

        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(v -> showAddExpenseDialog());

        loadExpenses();
    }

    private void showAddExpenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_expense, null);
        builder.setView(view);

        EditText etAmount = view.findViewById(R.id.etAmount);
        EditText etCategory = view.findViewById(R.id.etCategory);
        EditText etDate = view.findViewById(R.id.etDate);
        EditText etNote = view.findViewById(R.id.etNote);

        // Set default date to today
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        etDate.setText(currentDate);

        AlertDialog dialog = builder.create();

        view.findViewById(R.id.btnSave).setOnClickListener(v -> {
            try {
                double amount = Double.parseDouble(etAmount.getText().toString());
                String category = etCategory.getText().toString();
                String date = etDate.getText().toString();
                String note = etNote.getText().toString();

                if (dbHelper.addExpense(amount, category, date, note)) {
                    loadExpenses();
                    dialog.dismiss();
                }
            } catch (NumberFormatException e) {
                etAmount.setError("Please enter a valid amount");
            }
        });

        dialog.show();
    }

    private void loadExpenses() {
        expenseList.clear();
        Cursor cursor = dbHelper.getAllExpenses();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));

            expenseList.add(new Expense(id, amount, category, date, note));
        }

        cursor.close();
        expenseAdapter.notifyDataSetChanged();

        // Update total expenses
        double total = dbHelper.getTotalExpenses();
        tvTotalExpenses.setText(String.format("Total Expenses: $%.2f", total));
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}