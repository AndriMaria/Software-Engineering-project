package com.example.pedri;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PriceUpdatePage extends PricesPage {

    private String selectedServiceName, userNameOwner, location ,serv_price ;

    String newPrice;

    double averagePrice;
    private TextView serviceNameTextView, currentPriceTextView;
    private EditText newPriceEditText;
    private Button updatePriceButton;
    private ExecutorService executorService;
    private TextView averagePriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.price_update_page);

        serviceNameTextView = findViewById(R.id.serviceNameTextView);
        currentPriceTextView = findViewById(R.id.currentPriceTextView);
        newPriceEditText = findViewById(R.id.newPriceEditText);
        updatePriceButton = findViewById(R.id.updatePriceButton);
        averagePriceTextView = findViewById(R.id.averagePriceTextView);
        selectedServiceName = getIntent().getStringExtra("service_name");
        serv_price = getIntent().getStringExtra("current_price");
        userNameOwner = getIntent().getStringExtra("username_owner");
        location = getIntent().getStringExtra("area");



        serviceNameTextView.setText(selectedServiceName);
        currentPriceTextView.setText(serv_price);

        executorService = Executors.newSingleThreadExecutor();

        updatePriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePrice();
                double newPrice1 = Double.parseDouble(newPrice);
                if (averagePrice > newPrice1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PriceUpdatePage.this);
                    builder.setTitle("Δημιουργία Προσφοράς");
                    builder.setMessage(" Θέλετε να προσθέσετε την υπηρεσία στις προσφορές της επιχειρισης");
                    builder.setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ExecutorService executorService = Executors.newSingleThreadExecutor();
                            executorService.execute(() -> {
                                Connection con = ConnectionClass.CONN();
                                try {
                                    // Έλεγχος αν υπάρχει ήδη προσφορά με το ίδιο όνομα υπηρεσίας
                                    String checkSql = "SELECT * FROM Offers WHERE service_name = ?";
                                    PreparedStatement checkStmt = con.prepareStatement(checkSql);
                                    checkStmt.setString(1, selectedServiceName);
                                    ResultSet rs = checkStmt.executeQuery();

                                    if (rs.next()) {
                                        // Αν υπάρχει, διαγραφή της προσφοράς
                                        String deleteSql = "DELETE FROM Offers WHERE service_name = ?";
                                        PreparedStatement deleteStmt = con.prepareStatement(deleteSql);
                                        deleteStmt.setString(1, selectedServiceName);
                                        deleteStmt.executeUpdate();
                                    }

                                    // Εισαγωγή της νέας προσφοράς
                                    String insertSql = "INSERT INTO Offers (username_owner, service_name, current_price) VALUES (?, ?, ?)";
                                    PreparedStatement insertStmt = con.prepareStatement(insertSql);
                                    insertStmt.setString(1, userNameOwner);
                                    insertStmt.setString(2, selectedServiceName);
                                    insertStmt.setString(3, newPrice);
                                    insertStmt.executeUpdate();

                                    con.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                // Πλοήγηση στην OfferPage
                                Intent intent = new Intent(PriceUpdatePage.this, OffersPage.class);
                                intent.putExtra("service_name", selectedServiceName);
                                intent.putExtra("current_price", newPrice);
                                intent.putExtra("username_owner", userNameOwner);
                                startActivity(intent);
                            });
                        }
                    });
                    builder.setNegativeButton("Όχι", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(PriceUpdatePage.this,  PricesPage.class);
                            intent.putExtra("service_name", selectedServiceName);
                            intent.putExtra("current_price", serv_price);
                            intent.putExtra("username_owner", userNameOwner);
                            intent.putExtra("area",location);
                            Log.d("PricesAdapter", "Starting PriceUpdatePage with service: " + selectedServiceName + ", price: " + serv_price);
                            startActivity(intent);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });


        fetchAndDisplayAveragePrice(location);
    }

    private void changePrice() {
        newPrice = newPriceEditText.getText().toString();

        if (newPrice.isEmpty() || selectedServiceName == null || userNameOwner == null) {
            Toast.makeText(PriceUpdatePage.this, "Please enter all required information.", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection con = ConnectionClass.CONN()) {
                    if (con != null) {
                        String query = "UPDATE service_price SET current_price = ? WHERE service_name = ? AND username_owner = ?";
                        PreparedStatement stmt = con.prepareStatement(query);
                        stmt.setString(1, newPrice);
                        stmt.setString(2, selectedServiceName);
                        stmt.setString(3, userNameOwner);

                        int rowsUpdated = stmt.executeUpdate();
                        runOnUiThread(() -> {
                            if (rowsUpdated > 0) {
                                Toast.makeText(PriceUpdatePage.this, "Price updated successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PriceUpdatePage.this, "Failed to update price.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.e("ERROR", "Connection is null");
                    }
                } catch (SQLException e) {
                    Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
                    runOnUiThread(() -> Toast.makeText(PriceUpdatePage.this, "Database error occurred.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
    private void fetchAndDisplayAveragePrice(String location) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection con = ConnectionClass.CONN()) {
                    if (con != null) {
                        String query = "SELECT AVG(current_price) AS average_price FROM service_price WHERE area = ? and service_name = ?";
                        PreparedStatement stmt = con.prepareStatement(query);
                        stmt.setString(1, location);
                        stmt.setString(2,selectedServiceName );
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            averagePrice = rs.getDouble("average_price");
                            // Correct the way you set the text
                            runOnUiThread(() -> averagePriceTextView.setText(String.format(" %.2f", averagePrice)));
                        }
                    } else {
                        Log.e("ERROR", "Connection is null");
                    }
                } catch (SQLException e) {
                    Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
                    runOnUiThread(() -> Toast.makeText(PriceUpdatePage.this, "Database error occurred.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    public void backButton(View view) {
        Intent intent = new Intent(PriceUpdatePage.this,  PricesPage.class);
        intent.putExtra("service_name", selectedServiceName);
        intent.putExtra("current_price", serv_price);
        intent.putExtra("username_owner", userNameOwner);
        intent.putExtra("area",location);
        Log.d("PricesAdapter", "Starting PriceUpdatePage with service: " + selectedServiceName + ", price: " + serv_price);
        startActivity(intent);

    }
}