package com.example.pedri;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WalletProfile extends AppCompatActivity {

    private String userName, userNameOwner;
    private double moneyAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_profile);

        userName = getIntent().getStringExtra("username");
        userNameOwner = getIntent().getStringExtra("username_owner");
        moneyAccount = getIntent().getDoubleExtra("money_account", 0.0);

        if (userName != null) {
            fetchProfileDetailsUser();
        } else if (userNameOwner != null) {
            fetchProfileDetailsOwner();
        }
    }

    private void fetchProfileDetailsUser() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                String sql = "SELECT * FROM WalletProfileUser WHERE username_wallet = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, userName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    moneyAccount = rs.getDouble("money_account");
                    runOnUiThread(() -> {
                        EditText lockedField = findViewById(R.id.lockedField);
                        lockedField.setText(String.valueOf(moneyAccount));
                    });
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void fetchProfileDetailsOwner() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                String sql = "SELECT * FROM WalletProfileUser WHERE username_wallet = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, userNameOwner);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    moneyAccount = rs.getDouble("money_account");
                    runOnUiThread(() -> {
                        EditText lockedField = findViewById(R.id.lockedField);
                        lockedField.setText(String.valueOf(moneyAccount));
                    });
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void ChooseDeposit(View view) {
        showPaymentM();
    }

    public void showPaymentM() {
        if (userName != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Επιλογή Τρόπου Προσθήκης Χρημάτων")
                    .setItems(new CharSequence[]{"Paysafecard", "Paypal"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    Intent intent = new Intent(WalletProfile.this, Password.class);
                                    intent.putExtra("username", userName);
                                    startActivity(intent);
                                    break;
                                case 1:
                                    // Επιλογή Paypal
                                    break;
                            }
                        }
                    });
            builder.show();
        } else if (userNameOwner != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Επιλογή Τρόπου Προσθήκης Χρημάτων")
                    .setItems(new CharSequence[]{"Paysafecard", "Paypal"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    Intent intent = new Intent(WalletProfile.this, Password.class);
                                    intent.putExtra("username_owner", userNameOwner);
                                    startActivity(intent);
                                    break;
                                case 1:
                                    // Επιλογή Paypal
                                    break;
                            }
                        }
                    });
            builder.show();
        }
    }

    public void ButtonBack(View view) {
        if (userName != null) {
            Intent intent = new Intent(WalletProfile.this, CentralMenuUser.class);
            intent.putExtra("username", userName);
            startActivity(intent);
        } else if (userNameOwner != null) {
            Intent intent = new Intent(WalletProfile.this, CentralMenuOwner.class);
            intent.putExtra("username_owner", userNameOwner);
            startActivity(intent);
        }
    }

    public void TransactionHistory(View view) {
        Intent intent = new Intent(WalletProfile.this, TransactionsHistory.class);
        if (userName != null) {
            intent.putExtra("username", userName);
        } else if (userNameOwner != null) {
            intent.putExtra("username_owner", userNameOwner);
        }
        intent.putExtra("money_account", moneyAccount);
        startActivity(intent);
    }
}
