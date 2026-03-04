package com.example.pedri;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PreviewRent extends RentalInfoForm {
    private TextView companyNameTextView, phoneTextView, carTextView, costTextView, durationTextView;
    private String userName, companyName, carName;
    private int duration, licenseDuration;
    private ArrayList<String> carList;
    private double totalCost;
    private double moneyAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_rent);

        companyNameTextView = findViewById(R.id.companyNameTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        carTextView = findViewById(R.id.carTextView);
        costTextView = findViewById(R.id.costTextView);
        durationTextView = findViewById(R.id.durationTextView);

        carName = getIntent().getStringExtra("carName");
        duration = getIntent().getIntExtra("duration_rent", duration);
        licenseDuration = getIntent().getIntExtra("license_duration", licenseDuration);
        companyName = getIntent().getStringExtra("cars_rental_company");
        userName = getIntent().getStringExtra("username");
        carList = getIntent().getStringArrayListExtra("carList");

        fetchData();
    }

    private void fetchData() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();

                String sql = "SELECT * FROM cars WHERE name_car = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, carName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String companyName = rs.getString("cars_rental_company");
                    String phone = rs.getString("phone_number");
                    String carName = rs.getString("name_car");
                    double costPerDay = rs.getDouble("car_cost_per_day");

                    totalCost = costPerDay * duration;
                    runOnUiThread(() -> {
                        costTextView.setText(String.format("%.2f €", totalCost));
                        durationTextView.setText(duration + " ημέρες");
                        companyNameTextView.setText(companyName);
                        phoneTextView.setText(phone);
                        carTextView.setText(carName);
                    });
                }

                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void ButtonBack(View view) {
        Intent intent = new Intent(PreviewRent.this, RentalInfoForm.class);
        intent.putExtra("username", userName);
        intent.putStringArrayListExtra("carList", carList);
        intent.putExtra("carName", carName);
        //checkWalletBalance();
        startActivity(intent);
    }

    public void paymentButton(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Επιλογή Τρόπου Πληρωμής")
                .setItems(new CharSequence[]{"Μέσω Ψηφιακού Πορτοφολιού", "Στο Κατάστημα"}, (dialogInterface, i) -> {
                    if (i == 0) {
                        // Payment via digital wallet
                        checkWalletBalance();
                    } else {
                        // Payment at the store
                        builder.setTitle("Επιτυχής Ενοικίαση")
                                .setMessage("Το αίτημα σας για ενοικίαση οχήματος έγινε με επιτυχία.Η πληρωμή θα γίνει στο κατάστημα.")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                    Intent intent = new Intent(PreviewRent.this, CentralMenuUser.class);
                                    intent.putExtra("username", userName);
                                    startActivity(intent);
                                })
                                .show();
                    }
                });
        builder.show();
    }

    private void checkWalletBalance() {
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

                    if (moneyAccount >= totalCost) {
                        double newBalance = moneyAccount - totalCost;

                        // Προσθήκη της ενοικίασης ως συναλλαγή στο ιστορικό
                        String insertSql = "INSERT INTO HistoryTransaction (username_wallet, transaction_date, amount, description,name_business) VALUES (?, NOW(), ?, ?,?)";
                        PreparedStatement insertStmt = con.prepareStatement(insertSql);
                        insertStmt.setString(1, userName);
                        insertStmt.setDouble(2, totalCost);
                        insertStmt.setString(3, "Ενοικίαση αυτοκινήτου");
                        insertStmt.setString(4,companyName );

                        insertStmt.executeUpdate();

                        // Ενημέρωση του υπολοίπου του χρήστη
                        String updateSql = "UPDATE WalletProfileUser SET money_account = ? WHERE username_wallet = ?";
                        PreparedStatement updateStmt = con.prepareStatement(updateSql);
                        updateStmt.setDouble(1, newBalance);
                        updateStmt.setString(2, userName);
                        updateStmt.executeUpdate();

                        // Διαγραφή του επιλεγμένου αυτοκινήτου
                        String deleteSql = "DELETE FROM cars WHERE name_car = ?";
                        PreparedStatement deleteStmt = con.prepareStatement(deleteSql);
                        deleteStmt.setString(1, carName);
                        deleteStmt.executeUpdate();

                        // Εμφάνιση μηνύματος επιτυχίας και μετάβαση στη σελίδα WalletProfile
                        runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PreviewRent.this);
                            builder.setTitle("Επιτυχής Πληρωμή")
                                    .setMessage("Η πληρωμή σας έγινε με επιτυχία.")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        dialog.dismiss();
                                        Intent intent = new Intent(PreviewRent.this, WalletProfile.class);
                                        intent.putExtra("money_account", newBalance);
                                        intent.putExtra("username", userName);
                                        startActivity(intent);
                                    })
                                    .show();
                        });
                    } else {
                        // Εμφάνιση μηνύματος σφάλματος αν υπάρχει έλλειψη χρημάτων
                        runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PreviewRent.this);
                            builder.setTitle("Μη επαρκές ποσό")
                                    .setMessage("Δεν έχετε αρκετά χρήματα στο ψηφιακό πορτοφόλι σας για αυτήν την ενοικίαση.")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        dialog.dismiss();
                                        Intent intent = new Intent(PreviewRent.this, WalletProfile.class);
                                        intent.putExtra("username", userName);
                                        startActivity(intent);
                                    })
                                    .show();
                        });
                    }
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
