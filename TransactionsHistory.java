package com.example.pedri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionsHistory extends Profile {

    private String userName, userNameOwner;
    private ListView listView;
    private ArrayList<String> transactionsList;
    private ArrayAdapter<String> adapter;
    double moneyAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions_history);

        // Initialize the ListView and ArrayList
        listView = findViewById(R.id.listView);
        transactionsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, transactionsList);
        listView.setAdapter(adapter);

        // Get the username or username_owner from the Intent
        userName = getIntent().getStringExtra("username");
        userNameOwner = getIntent().getStringExtra("username_owner");
        moneyAccount=getIntent().getDoubleExtra("money_account",0.0);
        // Fetch the transaction history
        fetchTransactionHistory();
    }

    private void fetchTransactionHistory() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                String sql = "SELECT transaction_date, description, amount,name_business FROM HistoryTransaction WHERE username_wallet = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, userName != null ? userName : userNameOwner);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp("transaction_date");
                    double amount = rs.getDouble("amount");
                    String description = rs.getString("description");
                    String name_business = rs.getString("name_business");
                    String transactionInfo = String.format("%s | %s |%.2f€|%s", timestamp.toString(), description, amount,name_business); // Συνδυασμός περιγραφής και ποσού
                    transactionsList.add(transactionInfo); // Προσθήκη περιγραφής στην καταχώρηση της συναλλαγής
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



    public void ButtonBack(View view) {
        Intent intent = null;
        if(userName != null) {
            intent = new Intent(TransactionsHistory.this, WalletProfile.class);
            intent.putExtra("username", userName);
        } else if(userNameOwner != null) {
            intent = new Intent(TransactionsHistory.this, WalletProfile.class);
            intent.putExtra("username_owner", userNameOwner);
        } 
        // Προσθήκη του νέου υπολοίπου στο Intent
        intent.putExtra("money_account", moneyAccount);
        startActivity(intent);
    }


}

