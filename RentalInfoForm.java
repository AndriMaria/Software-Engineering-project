package com.example.pedri;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RentalInfoForm extends InformationOfCars {
    private EditText phoneEditText, addressEditText, ageEditText, licenseDurationEditText, DurationEditText;
    private String userName,companyName,carName,name;

    private Spinner pointsSpinner, hoursSpinner, returnPointsSpinner, returnHoursSpinner;
    private List<String> pointsList = new ArrayList<>();
    private List<String> hoursList = new ArrayList<>();
    ArrayList<String> carList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rental_info_form);

        // Λήψη του username από το Intent
        userName = getIntent().getStringExtra("username");
        companyName=getIntent().getStringExtra("cars_rental_company");
        carName=getIntent().getStringExtra("carName");
        carList=carList=getIntent().getStringArrayListExtra("carList");

        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        ageEditText = findViewById(R.id.ageEditText);
        licenseDurationEditText = findViewById(R.id.licenseDurationEditText);
        DurationEditText = findViewById(R.id.DurationEditText);

        pointsSpinner = findViewById(R.id.pointsSpinner);
        hoursSpinner = findViewById(R.id.hoursSpinner);
        returnPointsSpinner = findViewById(R.id.returnPointsSpinner);
        returnHoursSpinner = findViewById(R.id.returnHoursSpinner);

        fetchPoints();
        fetchHours();

        ArrayAdapter<String> pointsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pointsList);
        pointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pointsSpinner.setAdapter(pointsAdapter);

        ArrayAdapter<String> hoursAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hoursList);
        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hoursSpinner.setAdapter(hoursAdapter);

        fetchUserDetails();

        // Spinner παραλαβής αυτοκινήτου
        pointsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPoint = parent.getItemAtPosition(position).toString();
                fetchHoursForPoint(selectedPoint);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // όταν δεν επιλέγεται κανένα σημείο
            }
        });

        // Spinner επιστροφής αυτοκινήτου
        returnPointsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPoint = parent.getItemAtPosition(position).toString();
                fetchHoursForPoint(selectedPoint);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // όταν δεν επιλέγεται κανένα σημείο
            }
        });
    }

    private void fetchUserDetails() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                String sql = "SELECT contact_number, address, age FROM user WHERE username = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, userName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String contact = rs.getString("contact_number");
                    String address = rs.getString("address");
                    int age = rs.getInt("age");

                    runOnUiThread(() -> {
                        phoneEditText.setText(contact);
                        addressEditText.setText(address);
                        ageEditText.setText(String.valueOf(age));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(RentalInfoForm.this, "Σφάλμα κατά την ανάκτηση των στοιχείων", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void fetchPoints() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                String sql = "SELECT DISTINCT pickUp_loc FROM PickUpVehicle";
                PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String pointName = rs.getString("pickUp_loc");
                    pointsList.add(pointName);
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> pointsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pointsList);
                    pointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    pointsSpinner.setAdapter(pointsAdapter);
                    returnPointsSpinner.setAdapter(pointsAdapter);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(RentalInfoForm.this, "Σφάλμα κατά την ανάκτηση των σημείων", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void fetchHours() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                String sql = "SELECT pickUp_time FROM PickUpVehicle";
                PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String hourName = rs.getString("pickUp_time");
                    hoursList.add(hourName);
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> hoursAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hoursList);
                    hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    hoursSpinner.setAdapter(hoursAdapter);
                    returnHoursSpinner.setAdapter(hoursAdapter);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(RentalInfoForm.this, "Σφάλμα κατά την ανάκτηση των ωρών", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void fetchHoursForPoint(String selectedPoint) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                String sql = "SELECT pickUp_time FROM PickUpVehicle WHERE pickUp_loc = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, selectedPoint);
                ResultSet rs = stmt.executeQuery();

                ArrayList<String> hoursList = new ArrayList<>();
                while (rs.next()) {
                    String hourName = rs.getString("pickUp_time");
                    hoursList.add(hourName);
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> hoursAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hoursList);
                    hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    hoursSpinner.setAdapter(hoursAdapter);
                    returnHoursSpinner.setAdapter(hoursAdapter);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(RentalInfoForm.this, "Σφάλμα κατά την ανάκτηση των ωρών", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void InsertPersonalData(View view) {
        // έλεγχος αν τα πεδία είναι συμπληρωμένα
        if (phoneEditText.getText().toString().isEmpty() ||
                addressEditText.getText().toString().isEmpty() ||
                ageEditText.getText().toString().isEmpty() ||
                licenseDurationEditText.getText().toString().isEmpty() ||
                DurationEditText.getText().toString().isEmpty()) {
            Toast.makeText(RentalInfoForm.this, "Συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String phone = phoneEditText.getText().toString();
            String address = addressEditText.getText().toString();
            int age = Integer.parseInt(ageEditText.getText().toString());
            int licenseDuration = Integer.parseInt(licenseDurationEditText.getText().toString());
            int duration = Integer.parseInt(DurationEditText.getText().toString());


            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    Connection con = ConnectionClass.CONN();

                    String sql1="Select name from user where username =?";
                    PreparedStatement stmt1 = con.prepareStatement(sql1);
                    stmt1.setString(1, userName);
                    ResultSet rs1 = stmt1.executeQuery();

                    if (rs1.next()) {
                        name = rs1.getString("name");
                    }

                    String sql = "INSERT INTO carRentalRequest (name_driver,age_driver,duration_rent,name_car,license_duration) VALUES (?,?,?,?,?) ";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, name);
                    stmt.setInt(2, age);
                    stmt.setInt(3, duration);
                    stmt.setString(4, carName);
                    stmt.setInt(5, licenseDuration);
                    stmt.executeUpdate();

                    runOnUiThread(() -> {
                        Toast.makeText(RentalInfoForm.this, "Τα στοιχεία αποθηκεύτηκαν επιτυχώς", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RentalInfoForm.this, PreviewRent.class);
                        intent.putExtra("username", userName);
                        intent.putExtra("duration_rent",duration);
                        intent.putExtra("license_duration",licenseDuration);
                        intent.putExtra("cars_rental_company",companyName);
                        intent.putStringArrayListExtra("carList",carList);
                        intent.putExtra("carName",carName);

                        startActivity(intent);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(RentalInfoForm.this, "Σφάλμα κατά την αποθήκευση των στοιχείων", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }

    public void ButtonBack(View view) {
        Intent intent =new Intent(RentalInfoForm.this,InformationOfCars.class);
        intent.putExtra("username",userName);
        intent.putStringArrayListExtra("carList",carList);
        intent.putExtra("carName", carName);
        startActivity(intent);
    }
}
