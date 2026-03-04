package com.example.pedri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pedri.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Password extends AppCompatActivity {
    private EditText passwordEditText;

    private String userName, userNameOwner,currentMoneySql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);

        passwordEditText = findViewById(R.id.passwordEditText);

        // Λήψη του username από το Intent
        userName = getIntent().getStringExtra("username");
        userNameOwner = getIntent().getStringExtra("username_owner");
    }

    public void InsertPassword(View view) {
        String enteredPassword = passwordEditText.getText().toString();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                String sql = "SELECT money FROM PaysafeCard WHERE code = ? ";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, enteredPassword);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    double addedAmount = rs.getDouble("money");

                    // Fetch the current money_account value
                    String currentMoneySql = "SELECT money_account FROM WalletProfileUser WHERE username_wallet = ?";
                    PreparedStatement currentMoneyStmt = con.prepareStatement(currentMoneySql);
                    currentMoneyStmt.setString(1, userName != null ? userName : userNameOwner);
                    ResultSet currentMoneyRs = currentMoneyStmt.executeQuery();

                    if (currentMoneyRs.next()) {
                        double currentMoney = currentMoneyRs.getDouble("money_account");

                        // Update the money_account value
                        sql = "UPDATE WalletProfileUser SET money_account = ? WHERE username_wallet = ?";
                        PreparedStatement updateStmt = con.prepareStatement(sql);
                        updateStmt.setDouble(1, currentMoney + addedAmount);
                        updateStmt.setString(2, userName != null ? userName : userNameOwner);
                        int rowsAffected = updateStmt.executeUpdate();

                        if (rowsAffected > 0) {
                            // Insert into HistoryTransaction
                            String historySql = "INSERT INTO HistoryTransaction (username_wallet, transaction_date, amount, description,name_business) VALUES (?, NOW(), ?, ?,?)";
                            PreparedStatement historyStmt = con.prepareStatement(historySql);
                            historyStmt.setString(1, userName != null ? userName : userNameOwner);
                            historyStmt.setDouble(2, addedAmount);
                            historyStmt.setString(3, "Προσθήκη Χρημάτων");
                            historyStmt.setString(4, "Paysafe");


                            historyStmt.executeUpdate();

                            runOnUiThread(() -> {
                                Toast.makeText(Password.this, "Επιτυχής προσθήκη χρημάτων", Toast.LENGTH_SHORT).show();

                                // Navigate back to WalletProfile activity
                                Intent intent = new Intent(Password.this, WalletProfile.class);
                                if (userName != null) {
                                    intent.putExtra("username", userName);
                                } else if (userNameOwner != null) {
                                    intent.putExtra("username_owner", userNameOwner);
                                }
                                intent.putExtra("money_account", currentMoney + addedAmount);  // Update the balance
                                startActivity(intent);
                                finish();  // Close the current activity
                            });
                        }
                    }
                } else {
                    // Invalid code
                    runOnUiThread(() -> {
                        Toast.makeText(Password.this, "Ο 16ψηφιος κωδικός δεν είναι σωστός", Toast.LENGTH_SHORT).show();
                    });
                }

                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



}
