package com.example.pedri;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarRental extends CentralMenuUser {
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private ListView listViewCars;

    Connection con;
    Spinner spinner_location, spinner_classification;
    String selectedDate, selectedLocation, selectedClassification;
    private String userName,companyName;
    EditText lockedEditText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_rental);

        // Παίρνουμε το username από το UserSession
        userName = getIntent().getStringExtra("username");

        //Eisagwgi stoixeiwn
        insertData();
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                selectedDate = makeDateString(day, month, year);
                dateButton.setText(selectedDate);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1) return "JAN";
        if (month == 2) return "FEB";
        if (month == 3) return "MAR";
        if (month == 4) return "APR";
        if (month == 5) return "MAY";
        if (month == 6) return "JUN";
        if (month == 7) return "JUL";
        if (month == 8) return "AUG";
        if (month == 9) return "SEP";
        if (month == 10) return "OCT";
        if (month == 11) return "NOV";
        if (month == 12) return "DEC";
        return "JAN";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    public void openLocationPicker() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                con = ConnectionClass.CONN();
                String sql = "select distinct location_car from cars";
                PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                ArrayList<String> data = new ArrayList<>();
                while (rs.next()) {
                    String location = rs.getString("location_car");
                    data.add(location);
                }
                runOnUiThread(() -> {
                    ArrayAdapter<String> array = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
                    spinner_location.setAdapter(array);

                    spinner_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedLocation = parent.getItemAtPosition(position).toString();

                            // Καλείτε η μέθοδος για να ανακτήσετε το όνομα της εταιρείας και να το εμφανίσετε
                            retrieveCompanyName(selectedLocation);
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

    //  ανάκτηση του ονόματος της εταιρείας από τη βάση δεδομένων
    private void retrieveCompanyName(String location) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                con = ConnectionClass.CONN();
                String sql = "SELECT cars_rental_company FROM cars WHERE location_car = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, location);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                     companyName = rs.getString("cars_rental_company");
                    runOnUiThread(() -> {
                        lockedEditText.setText(companyName);
                    });
                }
                con.close();
            } catch
            (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void ButtonBack(View view) {
        Intent intent =new Intent(CarRental.this, CentralMenuUser.class);
        intent.putExtra("username",userName);
        startActivity(intent);
    }

    public void AreThereCars(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            ArrayList<String> carList = new ArrayList<>();
            try {
                con = ConnectionClass.CONN();
                // Ανάκτηση ημερομηνίας, μήνα, και έτους από το selectedDate
                String[] dateParts = selectedDate.split(" ");
                String day = dateParts[1];
                String month = dateParts[0];
                String year = dateParts[2];

                // Ανάλογα με την επιλογή ταξινόμησης, δημιουργία του SQL query
                String orderBy = selectedClassification.equals("Αύξουσα") ? "ASC" : "DESC";

                String sql = "SELECT * FROM cars WHERE location_car = ? AND date_car = ? AND month_car = ? AND year_car = ? ORDER BY car_cost_per_day " + orderBy;
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, selectedLocation);
                stmt.setString(2, day);
                stmt.setString(3, month);
                stmt.setString(4, year);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String carInfo = rs.getString("name_car") + " - " + rs.getString("car_cost_per_day") + "€";
                    carList.add(carInfo);
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                Intent intent = new Intent(CarRental.this, AvailableListCar.class);
                intent.putStringArrayListExtra("carList", carList);
                intent.putExtra("username",userName);
                intent.putExtra("cars_rental_company",companyName);

                startActivity(intent);
            });
        });
    }

    public void insertData(){
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());
        listViewCars = findViewById(R.id.listViewCars);
        spinner_location = findViewById(R.id.spinner_loc);
        spinner_classification = findViewById(R.id.spinner_classifiactionOfCar);
        lockedEditText = findViewById(R.id.lockedEditText);

        openLocationPicker();

        spinner_classification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedClassification = adapterView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Φθίνουσα");
        arrayList.add("Αύξουσα");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_classification.setAdapter(adapter);
    }
}
