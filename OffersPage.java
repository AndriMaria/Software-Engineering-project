package com.example.pedri;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OffersPage extends PriceUpdatePage {

    private TextView offersTextView;
    private String selectedServiceName, userNameOwner,newPrice,location;
    //public static List<String> offersList = new ArrayList<>(); // Static list to store offers

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offers_page);

        userNameOwner = getIntent().getStringExtra("username_owner");
        selectedServiceName = getIntent().getStringExtra("service_name");
        newPrice = getIntent().getStringExtra("current_price");

        offersTextView = findViewById(R.id.offersTextView);

        fetchOffers();
        // Display offers
        /*StringBuilder offersDisplay = new StringBuilder();
        for (String offer : offersList) {
            offersDisplay.append(offer).append("\n");
        }
        offersTextView.setText(offersDisplay.toString());*/
    }

    public void fetchOffers() {
        Runnable fetchOffersTask = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = ConnectionClass.CONN();
                    if (con != null) {
                        String sql = "SELECT * FROM Offers WHERE username_owner = ?";
                        PreparedStatement stmt = con.prepareStatement(sql);
                        stmt.setString(1, userNameOwner);
                        ResultSet rs = stmt.executeQuery();

                        StringBuilder offersDisplay = new StringBuilder();
                        while (rs.next()) {
                            String serviceName = rs.getString("service_name");
                            String price = rs.getString("current_price");
                            offersDisplay.append(serviceName).append(": ").append(price).append("\n");
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                offersTextView.setText(offersDisplay.toString());
                            }
                        });

                        con.close();
                    } else {
                        Log.e("ERROR", "Connection is null");
                    }
                } catch (Exception e) {
                    Log.e("ERROR", "Exception occurred: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        // Execute the task in a separate thread
        new Thread(fetchOffersTask).start();
    }

    public void ButtonBack(View view) {
        Intent intent = new Intent(OffersPage.this,  CentralMenuOwner.class);

        intent.putExtra("service_name", selectedServiceName);
        intent.putExtra("current_price", newPrice);
        intent.putExtra("username_owner", userNameOwner);
        intent.putExtra("area",location);

        startActivity(intent);

        startActivity(intent);
    }
}