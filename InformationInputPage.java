package com.example.pedri;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pedri.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InformationInputPage extends ServicesPage {

    private Spinner regionSpinner;
    private Spinner hourSpinner;
    private Button datePickerButton;
    private Connection con;
    private String selectedDate;
    private String selectedRegion;
    private String selectedHour;
    private String userName;
    private String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_input_page);

        regionSpinner = findViewById(R.id.region_spinner);
        hourSpinner = findViewById(R.id.hour_spinner);
        datePickerButton = findViewById(R.id.date_picker_button);

        userName = getIntent().getStringExtra("username");
        source = getIntent().getStringExtra("source");


        datePickerButton.setOnClickListener(v -> showDatePickerDialog());


        loadHours();
        loadRegions();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
            datePickerButton.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void loadRegions() {

        if ("btnParking".equals(source)) {
            // Λογική για το btnParking

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                con = ConnectionClass.CONN();
                String sql = "SELECT DISTINCT location_parking FROM Parking";
                PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                List<String> regions = new ArrayList<>();
                while (rs.next()) {
                    regions.add(rs.getString("location_parking"));
                }
                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, regions);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    regionSpinner.setAdapter(adapter);
                    regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedRegion = parent.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                });
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        } else if ("btnCarwash".equals(source)) {
            // Λογική για το btnCarwash

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    con = ConnectionClass.CONN();
                    String sql = "SELECT DISTINCT location_carWash FROM CarWash";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();
                    List<String> regions = new ArrayList<>();
                    while (rs.next()) {
                        regions.add(rs.getString("location_carWash"));
                    }
                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, regions);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        regionSpinner.setAdapter(adapter);
                        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedRegion = parent.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });
                    });
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
    }

    private void loadHours() {
        List<String> hours = new ArrayList<>();
        hours.add("08:00-09:00");
        hours.add("09:00-10:00");
        hours.add("10:00-11:00");
        hours.add("11:00-12:00");
        hours.add("12:00-13:00");
        hours.add("13:00-14:00");
        hours.add("14:00-15:00");
        hours.add("15:00-16:00");
        hours.add("16:00-17:00");
        hours.add("17:00-18:00");
        hours.add("18:00-19:00");
        hours.add("19:00-20:00");
        hours.add("20:00-21:00");
        hours.add("21:00-22:00");
        hours.add("22:00-23:00");
        hours.add("23:00-00:00");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(adapter);
        hourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedHour = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void ButtonBack(View view) {
        Intent intent = new Intent(InformationInputPage.this, ServicesPage.class);
        intent.putExtra("username", userName);
        startActivity(intent);
    }

    public void InsertInfo(View view) {
        if ("btnParking".equals(source)) {
            // Λογική για το btnParking

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    con = ConnectionClass.CONN();
                    String sql = "SELECT p.name_parking, p.phone_number, p.address, p.parking_cost_per_hour,p.username_owner FROM Parking p LEFT JOIN ParkingReservations r ON p.name_parking = r.name_parking AND p.location_parking = r.location_parking AND r.reservation_date = ? AND r.reservation_time = ? WHERE p.location_parking = ? AND p.remaining_parking_spaces > (SELECT COUNT(*) FROM ParkingReservations WHERE name_parking = p.name_parking AND location_parking = p.location_parking AND reservation_date = ? AND reservation_time = ?)";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, selectedDate);
                    stmt.setString(2, selectedHour);
                    stmt.setString(3, selectedRegion);
                    stmt.setString(4, selectedDate);
                    stmt.setString(5, selectedHour);
                    ResultSet rs = stmt.executeQuery();
                    ArrayList<String[]> availableParkings = new ArrayList<>();
                    while (rs.next()) {
                        String name = rs.getString("name_parking");
                        String telephone = rs.getString("phone_number");
                        String address = rs.getString("address");
                        String parkingCostPerHour = rs.getString("parking_cost_per_hour");
                        String userNameOwner= rs.getString("username_owner");
                        availableParkings.add(new String[]{name, telephone, address, parkingCostPerHour,userNameOwner});
                    }
                    runOnUiThread(() -> {
                        if (availableParkings.isEmpty()) {
                            Toast.makeText(InformationInputPage.this, "Δεν υπάρχουν διαθέσιμα πάρκινγκ.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(InformationInputPage.this, ListOfBusiness.class);
                            intent.putExtra("availableParkings", availableParkings);
                            intent.putExtra("username", userName);
                            intent.putExtra("selected_date", selectedDate);
                            intent.putExtra("selected_hour", selectedHour);
                            intent.putExtra("location_parking", selectedRegion);
                            intent.putExtra("source", source);
                            startActivity(intent);
                        }
                    });
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } else if ("btnCarwash".equals(source)) {
            // Λογική για το btnCarwash
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    con = ConnectionClass.CONN();
                    String sql = "SELECT c.name_carWash, c.phone_number, c.address, c.carWash_cost_per_hour,c.username_owner FROM CarWash c LEFT JOIN CarWashReservations r ON c.name_carWash = r.name_carWash AND c.location_carWash = r.location_carWash AND r.reservation_date = ? AND r.reservation_time = ? WHERE c.location_carWash = ? AND c.remaining_carWash_spaces > (SELECT COUNT(*) FROM CarWashReservations WHERE name_carWash = c.name_carWash AND location_carWash = c.location_carWash AND reservation_date = ? AND reservation_time = ?)";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, selectedDate);
                    stmt.setString(2, selectedHour);
                    stmt.setString(3, selectedRegion);
                    stmt.setString(4, selectedDate);
                    stmt.setString(5, selectedHour);
                    ResultSet rs = stmt.executeQuery();
                    ArrayList<String[]> availableCarWash = new ArrayList<>();
                    while (rs.next()) {
                        String name = rs.getString("name_carWash");
                        String telephone = rs.getString("phone_number");
                        String address = rs.getString("address");
                        String CarWashCostPerHour = rs.getString("carWash_cost_per_hour");
                        String userNameOwner= rs.getString("username_owner");

                        availableCarWash.add(new String[]{name, telephone, address, CarWashCostPerHour,userNameOwner});
                    }
                    runOnUiThread(() -> {
                        if (availableCarWash.isEmpty()) {
                            Toast.makeText(InformationInputPage.this, "Δεν υπάρχουν διαθέσιμα πάρκινγκ.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(InformationInputPage.this, ListOfBusiness.class);
                            intent.putExtra("availableCarWash", availableCarWash);
                            intent.putExtra("username", userName);
                            intent.putExtra("selected_date", selectedDate);
                            intent.putExtra("selected_hour", selectedHour);
                            intent.putExtra("location_carWash", selectedRegion);
                            intent.putExtra("source", source);
                            // intent.putExtra("btnCarwash",source);
                            startActivity(intent);
                        }
                    });
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }

    }

}
