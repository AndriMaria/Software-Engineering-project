package com.example.pedri;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Route extends CentralMenuUser {
    private AutoCompleteTextView startLocationAutoCompleteTextView, endLocationAutoCompleteTextView;
    private Button datePickerButton;
    private EditText passengerCountEditText, luggageCountEditText;
    private DatePickerDialog datePickerDialog;
    private String userName;

    String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);

        startLocationAutoCompleteTextView = findViewById(R.id.startLocationAutoCompleteTextView);
        endLocationAutoCompleteTextView = findViewById(R.id.endLocationAutoCompleteTextView);
        datePickerButton = findViewById(R.id.datePickerButton);
        passengerCountEditText = findViewById(R.id.passengerCountEditText);
        luggageCountEditText = findViewById(R.id.luggageCountEditText);

        userName = getIntent().getStringExtra("username");

        initDatePicker();
        datePickerButton.setText(getTodaysDate());

        startLocationAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (query.length() >= 1) {
                    new SearchLocationTask().execute(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        endLocationAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (query.length() >= 1) {
                    new SearchEndLocationTask().execute(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void describeRoute(View view) {
        String startLocation = startLocationAutoCompleteTextView.getText().toString();
        String endLocation = endLocationAutoCompleteTextView.getText().toString();
        String date = datePickerButton.getText().toString();
        String passengerCount = passengerCountEditText.getText().toString();
        String luggageCount = luggageCountEditText.getText().toString();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            ArrayList<String> carList = new ArrayList<>();
            try {
                Connection con = ConnectionClass.CONN();
                String[] dateParts = selectedDate.split(" ");
                String day = dateParts[1];
                String month = dateParts[0];
                String year = dateParts[2];

                String sql = "SELECT car_name, max_number_passenger FROM CarPoolDriver WHERE starting_point = ? AND final_destination = ? AND date_route = ? AND month_route = ? AND year_route = ?";
                PreparedStatement stmt = con.prepareStatement(sql);

                stmt.setString(1, startLocation);
                stmt.setString(2, endLocation);
                stmt.setString(3, day);
                stmt.setString(4, month);
                stmt.setString(5, year);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String carInfo = rs.getString("car_name");
                    int maxPassengers = rs.getInt("max_number_passenger");
                    if (Integer.parseInt(passengerCount) <= maxPassengers) {
                        carList.add(carInfo);
                    }
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                Intent intent = new Intent(Route.this, Vehicle.class);
                intent.putExtra("username", userName);
                intent.putExtra("passengerCount", passengerCount);
                intent.putExtra("luggageCount", luggageCount);
                intent.putExtra("date", date); // Πρόσθεσε την ημερομηνία
                intent.putStringArrayListExtra("carList", carList);
                startActivity(intent);
            });
        });
    }


    public void ButtonBack(View view) {
        Intent intent = new Intent(Route.this, CentralMenuUser.class);
        intent.putExtra("username", userName);
        startActivity(intent);
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
                datePickerButton.setText(selectedDate);
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

    private class SearchLocationTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            String query = params[0];
            ArrayList<String> locations = new ArrayList<>();

            try {
                Connection connection = ConnectionClass.CONN();
                String normalizedQuery = Normalizer.normalize(query, Normalizer.Form.NFD)
                        .replaceAll("\\p{M}", "").toLowerCase();

                String sql = "SELECT starting_point FROM CarPoolDriver WHERE LOWER(CONVERT(starting_point USING utf8)) LIKE ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, "%" + normalizedQuery + "%");

                ResultSet resultSet = statement.executeQuery();

                Set<String> uniqueLocations = new HashSet<>();
                while (resultSet.next()) {
                    String location = resultSet.getString("starting_point");
                    uniqueLocations.add(location);
                }

                locations.addAll(uniqueLocations);
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return locations;
        }

        @Override
        protected void onPostExecute(ArrayList<String> locations) {
            if (!locations.isEmpty()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Route.this, android.R.layout.simple_dropdown_item_1line, locations);
                startLocationAutoCompleteTextView.setAdapter(adapter);
            }
        }
    }

    private class SearchEndLocationTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            String query = params[0];
            ArrayList<String> locations = new ArrayList<>();

            try {
                Connection connection = ConnectionClass.CONN();
                String normalizedQuery = Normalizer.normalize(query, Normalizer.Form.NFD)
                        .replaceAll("\\p{M}", "").toLowerCase();

                String sql = "SELECT final_destination FROM CarPoolDriver WHERE LOWER(CONVERT(final_destination USING utf8)) LIKE ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, "%" + normalizedQuery + "%");

                ResultSet resultSet = statement.executeQuery();

                Set<String> uniqueLocations = new HashSet<>();
                while (resultSet.next()) {
                    String location = resultSet.getString("final_destination");
                    uniqueLocations.add(location);
                }

                locations.addAll(uniqueLocations);
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return locations;
        }

        @Override
        protected void onPostExecute(ArrayList<String> locations) {
            if (!locations.isEmpty()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Route.this, android.R.layout.simple_dropdown_item_1line, locations);
                endLocationAutoCompleteTextView.setAdapter(adapter);
            }
        }
    }
}
