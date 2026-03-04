package com.example.pedri;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PreviewDriverPost extends DriverInfoForm {

    private String userName, name, startLocation, endLocation, date, time, tripCost, carName,
            years_driving, maxPassengers, hasTraveledBefore, comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_driver_post);

        Intent intent = getIntent();
        userName = intent.getStringExtra("username");
        name = intent.getStringExtra("name");
        startLocation = intent.getStringExtra("startLocation");
        endLocation = intent.getStringExtra("endLocation");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        tripCost = intent.getStringExtra("tripCost");
        carName = intent.getStringExtra("carName");
        years_driving = intent.getStringExtra("years_driving");
        maxPassengers = intent.getStringExtra("maxPassengers");
        hasTraveledBefore = intent.getStringExtra("hasTraveledBefore");
        comments = intent.getStringExtra("comments");

        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView startLocationTextView = findViewById(R.id.startLocationTextView);
        TextView endLocationTextView = findViewById(R.id.endLocationTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView timeTextView = findViewById(R.id.timeTextView);
        TextView tripCostTextView = findViewById(R.id.tripCostTextView);
        TextView carNameTextView = findViewById(R.id.carNameTextView);
        TextView yearsDrivingTextView = findViewById(R.id.yearsDrivingTextView);
        TextView maxPassengersTextView = findViewById(R.id.maxPassengersTextView);
        TextView hasTraveledBeforeTextView = findViewById(R.id.hasTraveledBeforeTextView);
        TextView commentsTextView = findViewById(R.id.commentsTextView);

        nameTextView.setText(name);
        startLocationTextView.setText(startLocation);
        endLocationTextView.setText(endLocation);
        dateTextView.setText(date);
        timeTextView.setText(time);
        tripCostTextView.setText(tripCost);
        carNameTextView.setText(carName);
        yearsDrivingTextView.setText(years_driving);
        maxPassengersTextView.setText(maxPassengers);
        hasTraveledBeforeTextView.setText(hasTraveledBefore);
        commentsTextView.setText(comments);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Επιβεβαίωση Δημοσίευσης");
        builder.setMessage("Είστε σίγουρος ότι θέλετε να δημοσιεύσετε τη διαδρομή;");
        builder.setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submitRoute();
            }
        });
        builder.setNegativeButton("Όχι", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private void submitRoute() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean success = false;
                try {
                    Connection con = ConnectionClass.CONN();

                    if (con == null) {
                        throw new Exception("Connection is null");
                    }

                    String insertSql = "INSERT INTO CarPoolDriver (username_driver, years_driving, starting_point, final_destination, car_name, date_route, month_route, year_route, cost_route, comments, max_number_passenger, route_again, start_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = con.prepareStatement(insertSql);
                    insertStmt.setString(1, userName);
                    insertStmt.setInt(2, Integer.parseInt(years_driving));
                    insertStmt.setString(3, startLocation);
                    insertStmt.setString(4, endLocation);
                    insertStmt.setString(5, carName);
                    String[] dateParts = date.split(" ");
                    insertStmt.setInt(6, Integer.parseInt(dateParts[1])); // day
                    insertStmt.setString(7, dateParts[0]); // month
                    insertStmt.setInt(8, Integer.parseInt(dateParts[2])); // year
                    insertStmt.setDouble(9, Double.parseDouble(tripCost));
                    insertStmt.setString(10, comments);
                    insertStmt.setInt(11, Integer.parseInt(maxPassengers));
                    insertStmt.setString(12, hasTraveledBefore);
                    insertStmt.setString(13, time);

                    int rowsInserted = insertStmt.executeUpdate();
                    if (rowsInserted > 0) {
                        success = true;
                    }

                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Toast.makeText(PreviewDriverPost.this, "Η διαδρομή δημοσιεύτηκε επιτυχώς.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PreviewDriverPost.this, CentralMenuUser.class);
                    intent.putExtra("username", userName);
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(PreviewDriverPost.this)
                            .setTitle("Σφάλμα")
                            .setMessage("Η δημοσίευση της διαδρομής απέτυχε.")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            }
        }.execute();
    }

    public void confirm(View v) {
        showConfirmationDialog();
    }

    public void ButtonBack(View view) {
        Intent intent = new Intent(PreviewDriverPost.this, DriverInfoForm.class);
        startActivity(intent);
    }
}
