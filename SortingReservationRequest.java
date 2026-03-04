package com.example.pedri;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class SortingReservationRequest extends CentralMenuOwner {

    private String userNameOwner,userName,business;
    private ListView reservationListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> reservationList;
    private ArrayList<String[]> reservationDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sorting_reservation_request);

        userNameOwner = getIntent().getStringExtra("username_owner");
        reservationListView = findViewById(R.id.reservationListView);
        reservationList = new ArrayList<>();
        reservationDetailsList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reservationList);
        reservationListView.setAdapter(adapter);

        // Αρχική φόρτωση δεδομένων
        new FetchReservationRequestsTask().execute();

        // Ορισμός ακροατή για το ListView
        reservationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] details = reservationDetailsList.get(position);
                Intent intent = new Intent(SortingReservationRequest.this, ReservationRequestInfoPage.class);
                intent.putExtra("position", position);
                intent.putExtra("date", details[0]);
                intent.putExtra("time", details[1]);
                intent.putExtra("cost", details[2]);
                intent.putExtra("name", details[3]);
                intent.putExtra("phoneNumber", details[4]);
                intent.putExtra("username_owner", userNameOwner);
                intent.putExtra("username", userName);

                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            int position = data.getIntExtra("position", -1);
            if (position != -1) {
                reservationList.remove(position);
                reservationDetailsList.remove(position);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void ButtonBack(View view) {
        Intent intent = new Intent(SortingReservationRequest.this, CentralMenuOwner.class);
        intent.putExtra("username_owner", userNameOwner);
        startActivity(intent);
    }

    private class FetchReservationRequestsTask extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> reservations = new ArrayList<>();
            try {
                Connection con = ConnectionClass.CONN();
                if (con != null) {





                    // Ερώτημα για τις κρατήσεις στο ParkingReservations
                    String queryParking = "SELECT * FROM ParkingReservations WHERE username_owner = ? ORDER BY reservation_date ASC, reservation_time ASC";
                    PreparedStatement stmtParking = con.prepareStatement(queryParking);
                    stmtParking.setString(1, userNameOwner);
                    ResultSet rsParking = stmtParking.executeQuery();
                    while (rsParking.next()) {
                        String date = rsParking.getString("reservation_date");
                        String time = rsParking.getString("reservation_time");
                        String cost = rsParking.getString("cost");
                         userName = rsParking.getString("username");

                        String sql = "SELECT name, contact_number FROM user WHERE username = ?";
                        PreparedStatement stmt = con.prepareStatement(sql);
                        stmt.setString(1, userName);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            String name = rs.getString("name");
                            String phoneNumber = rs.getString("contact_number");

                            String reservation = "Parking: " + rsParking.getString("name_parking") + ", Date: " + date + ", Time: " + time;
                            reservations.add(reservation);
                            reservationDetailsList.add(new String[]{date, time, cost, name, phoneNumber});
                        }
                        rs.close();
                        stmt.close();
                    }
                    rsParking.close();
                    stmtParking.close();


                    // Ερώτημα για τις κρατήσεις στο CarWashReservations
                    String queryCarWash = "SELECT * FROM CarWashReservations WHERE username_owner = ? ORDER BY reservation_date ASC, reservation_time ASC";
                    PreparedStatement stmtCarWash = con.prepareStatement(queryCarWash);
                    stmtCarWash.setString(1, userNameOwner);
                    ResultSet rsCarWash = stmtCarWash.executeQuery();
                    while (rsCarWash.next()) {
                        String date = rsCarWash.getString("reservation_date");
                        String time = rsCarWash.getString("reservation_time");
                        String cost = rsCarWash.getString("cost");
                         userName = rsCarWash.getString("username");

                        String sql = "SELECT name, contact_number FROM user WHERE username = ?";
                        PreparedStatement stmt = con.prepareStatement(sql);
                        stmt.setString(1, userName);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            String name = rs.getString("name");
                            String phoneNumber = rs.getString("contact_number");

                            String reservation = "CarWash: " + rsCarWash.getString("name_carWash") + ", Date: " + date + ", Time: " + time;
                            reservations.add(reservation);
                            reservationDetailsList.add(new String[]{date, time, cost, name, phoneNumber});
                        }
                        rs.close();
                        stmt.close();
                    }
                    rsCarWash.close();
                    stmtCarWash.close();

                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return reservations;
        }

        @Override
        protected void onPostExecute(ArrayList<String> reservations) {
            if (reservations != null && !reservations.isEmpty()) {
                reservationList.addAll(reservations);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(SortingReservationRequest.this, "Δεν υπάρχουν Αιτήσεις", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
