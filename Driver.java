package com.example.pedri;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Προσθέστε αυτό για logging
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Driver extends Vehicle {

    private TextView driverNameTextView, yearsDrivingTextView, carNameTextView, costTextView, commentsTextView, RouteAgainTextView;
    private String name,carName, userName, number_passenger, number_luggage, selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver);

        // Αντιστοίχιση των TextViews στα IDs τους
        driverNameTextView = findViewById(R.id.driverNameTextView);
        yearsDrivingTextView = findViewById(R.id.yearsDrivingTextView);
        carNameTextView = findViewById(R.id.carNameTextView);
        costTextView = findViewById(R.id.costTextView);
        commentsTextView = findViewById(R.id.commentsTextView);
        RouteAgainTextView = findViewById(R.id.RouteAgainTextView);

        // Λήψη των extras από το Intent
        number_luggage = getIntent().getStringExtra("luggageCount");
        number_passenger = getIntent().getStringExtra("passengerCount");
        carName = getIntent().getStringExtra("carName");
        userName = getIntent().getStringExtra("username");
        selectedDate = getIntent().getStringExtra("selectedDate");

        carNameTextView.setText(carName);

        // Κλήση της μεθόδου για να λάβετε τις πληροφορίες του οδηγού
        getDriverInfo();
    }

    private void getDriverInfo() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                if (con == null) {
                    Log.e("DB Connection", "Connection to database failed.");
                    return;
                }




                String sql = "SELECT CarPoolDriver.*, user.name " +
                        "FROM CarPoolDriver " +
                        "JOIN user ON CarPoolDriver.username_driver = user.username " +
                        "WHERE car_name = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, carName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String driverName = rs.getString("name");
                    String yearsDriving = rs.getString("years_driving");
                    String cost = rs.getString("cost_route");
                    String comments = rs.getString("comments");
                    String routeAgain = rs.getString("route_again");

                    Log.d("DB Result", "Driver Name: " + driverName);
                    Log.d("DB Result", "Years Driving: " + yearsDriving);
                    Log.d("DB Result", "Cost: " + cost);
                    Log.d("DB Result", "Comments: " + comments);
                    Log.d("DB Result", "Route Again: " + routeAgain);

                    runOnUiThread(() -> {
                        driverNameTextView.setText(driverName);
                        yearsDrivingTextView.setText(yearsDriving);
                        costTextView.setText(cost);
                        commentsTextView.setText(comments);
                        RouteAgainTextView.setText(routeAgain);
                    });
                } else {
                    Log.e("DB Result", "No results found.");
                }

                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void ButtonBack(View view) {
        Intent intent = new Intent(Driver.this, Vehicle.class);
        intent.putExtra("username", userName);
        intent.putExtra("carName", carName);
        intent.putStringArrayListExtra("carList", getIntent().getStringArrayListExtra("carList")); // Περνάμε πίσω τη λίστα των αυτοκινήτων
        startActivity(intent);
        finish();
    }

    public void Check_In(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                if (con == null) {
                    runOnUiThread(() -> new AlertDialog.Builder(Driver.this)
                            .setTitle("Error")
                            .setMessage("Connection to database failed.")
                            .setPositiveButton(android.R.string.ok, null)
                            .show());
                    return;
                }
                userName = getIntent().getStringExtra("username");
                String sql1 = "Select name from user where username=?";
                PreparedStatement stmt1 = con.prepareStatement(sql1);
                stmt1.setString(1, userName);
                ResultSet rs1 = stmt1.executeQuery();
                if(rs1.next()) {
                    name = rs1.getString("name");
                    runOnUiThread(() -> {

                    });
                }

                String sql = "INSERT INTO CarPoolPassRequest (name_passenger, car_name, number_passenger, number_luggage) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, carName);
                stmt.setString(3, number_passenger);
                stmt.setString(4, number_luggage);

                int rowsInserted = stmt.executeUpdate();
                con.close();

                runOnUiThread(() -> {
                    if (rowsInserted > 0) {
                        new AlertDialog.Builder(Driver.this)
                                .setTitle("Επιτυχία")
                                .setMessage("Το αίτημα επιβίβασης υποβλήθηκε επιτυχώς.")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Driver.this, CentralMenuUser.class);
                                    intent.putExtra("username", userName);
                                    startActivity(intent);
                                })
                                .show();
                    } else {
                        new AlertDialog.Builder(Driver.this)
                                .setTitle("Λάθος")
                                .setMessage("Ανεπιτυχής υποβολή αιτήματος επιβίβασης")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> new AlertDialog.Builder(Driver.this)
                        .setTitle("Error")
                        .setMessage("An error occurred while submitting the request.")
                        .setPositiveButton(android.R.string.ok, null)
                        .show());
            }
        });
    }

}

