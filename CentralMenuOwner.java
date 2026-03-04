package com.example.pedri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.Connection;

public class CentralMenuOwner extends AppCompatActivity {

    private String userNameOwner,business;
    private Button ChooseNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.central_menu_owner);

        ChooseNotification = findViewById(R.id.ChooseNotification);

        // Λήψη παραμέτρων από το Intent
        userNameOwner = getIntent().getStringExtra("username_owner");

        // Κλήση για λήψη του αριθμού αιτημάτων
        new FetchReservationRequestsTask().execute();


    }

    public void btnChooseUpdatePrice(View view) {
        Intent intent = new Intent(CentralMenuOwner.this, PricesPage.class);
        intent.putExtra("username_owner", userNameOwner);
        startActivity(intent);
    }
    public void btnOffers(View view) {
        Intent intent = new Intent(this, OffersPage.class);
        intent.putExtra("username_owner", userNameOwner);
        startActivity(intent);
    }
    public void btn_Profil(View view) {
        Intent intent = new Intent(CentralMenuOwner.this, Profile.class);
        intent.putExtra("username_owner", userNameOwner);
        startActivity(intent);
    }

    // Κουμπί Πορτοφόλι
    public void chooseWallet(View view) {
        show_wp();
    }

    public void show_wp() {
        Intent intent = new Intent(CentralMenuOwner.this, WalletProfile.class);
        intent.putExtra("username_owner", userNameOwner);
        startActivity(intent);
    }

    public void ChooseNotification(View view) {
        Intent intent = new Intent(CentralMenuOwner.this, SortingReservationRequest.class);
        intent.putExtra("username_owner", userNameOwner);
        startActivity(intent);
    }

    private class FetchReservationRequestsTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            int count = 0;
            try {
                Connection con = ConnectionClass.CONN();
                if (con != null) {


                        // Ερώτημα για τις κρατήσεις στο ParkingReservations
                        String queryParking = "SELECT COUNT(*) AS count FROM ParkingReservations WHERE username_owner = ?";
                        PreparedStatement stmtParking = con.prepareStatement(queryParking);
                        stmtParking.setString(1, userNameOwner);
                        ResultSet rsParking = stmtParking.executeQuery();
                        if (rsParking.next()) {
                            count += rsParking.getInt("count");
                        }
                        rsParking.close();
                        stmtParking.close();


                        // Ερώτημα για τις κρατήσεις στο CarWashReservations
                        String queryCarWash = "SELECT COUNT(*) AS count FROM CarWashReservations WHERE username_owner = ?";
                        PreparedStatement stmtCarWash = con.prepareStatement(queryCarWash);
                        stmtCarWash.setString(1, userNameOwner);
                        ResultSet rsCarWash = stmtCarWash.executeQuery();
                        if (rsCarWash.next()) {
                            count += rsCarWash.getInt("count");
                        }
                        rsCarWash.close();
                        stmtCarWash.close();

                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return count;
        }

        @Override
        protected void onPostExecute(Integer count) {
            if (count > 0) {
                ChooseNotification.setText("Αιτήματα Κρατήσεων (" + count + ")");
            } else {
                ChooseNotification.setText("Αιτήματα Κρατήσεων");
            }
        }
    }
}
