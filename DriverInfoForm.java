package com.example.pedri;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

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

public class DriverInfoForm extends CentralMenuUser {
    private AutoCompleteTextView startLocationAutoCompleteTextView, endLocationAutoCompleteTextView;
    private Button datePickerButton, timePickerButton;
    private EditText passengerCountEditText, tripCostEditText, carNameEditText,yearsDrivingEditText;
    private EditText maxPassengersEditText, hasTraveledBeforeEditText,commentEditText;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private String userName;

    String selectedDate;
    String selectedTime;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_info_form);

        startLocationAutoCompleteTextView = findViewById(R.id.startLocationAutoCompleteTextView);
        endLocationAutoCompleteTextView = findViewById(R.id.endLocationAutoCompleteTextView);
        datePickerButton = findViewById(R.id.datePickerButton);
        timePickerButton = findViewById(R.id.timePickerButton);
        passengerCountEditText = findViewById(R.id.passengerCountEditText);
        tripCostEditText = findViewById(R.id.tripCostEditText);
        carNameEditText = findViewById(R.id.carNameEditText);
        yearsDrivingEditText = findViewById(R.id.yearsDrivingEditText);
        maxPassengersEditText = findViewById(R.id.maxPassengersEditText);
        hasTraveledBeforeEditText = findViewById(R.id.hasTraveledBeforeEditText);
        commentEditText = findViewById(R.id.commentEditText);

        userName = getIntent().getStringExtra("username");

        initDatePicker();
        datePickerButton.setText(getTodaysDate());
        initTimePicker();
        timePickerButton.setText(getCurrentTime());

        // Set onClickListeners for datePickerButton and timePickerButton
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(v);
            }
        });

        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker(v);
            }
        });

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

    public void FillOutInfo(View view) {
        String startLocation = startLocationAutoCompleteTextView.getText().toString().trim();
        String endLocation = endLocationAutoCompleteTextView.getText().toString().trim();
        String date = datePickerButton.getText().toString();
        String time = timePickerButton.getText().toString();
        String tripCost = tripCostEditText.getText().toString().trim();
        String carName = carNameEditText.getText().toString().trim();
        String years_driving=yearsDrivingEditText.getText().toString().trim();
        String maxPassengers = maxPassengersEditText.getText().toString().trim();
        String hasTraveledBefore = hasTraveledBeforeEditText.getText().toString().trim();
        String comments = commentEditText.getText().toString().trim();

        if(startLocation.isEmpty() ||endLocation.isEmpty()||tripCost.isEmpty()
                ||carName.isEmpty()||years_driving.isEmpty()||maxPassengers.isEmpty()||hasTraveledBefore.isEmpty()
        ||comments.isEmpty()){
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show();
        }else {
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

                    String sql = "Select name from user where username=?";

                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, userName);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String name = rs.getString("name");

                        //int rowsInserted = stmt.executeUpdate();


                        runOnUiThread(() -> {
                            //if (rowsInserted > 0) {
                           /* new androidx.appcompat.app.AlertDialog.Builder(FormNewRouteDriver.this)
                                    .setTitle("Επιτυχία")
                                    .setMessage("Το αίτημα επιβίβασης υποβλήθηκε επιτυχώς.")
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();*/
                            Toast.makeText(DriverInfoForm.this, "Το αίτημα επιβίβασης υποβλήθηκε επιτυχώς.", Toast.LENGTH_SHORT).show();

                            handler.post(() -> {
                                Intent intent = new Intent(DriverInfoForm.this, PreviewDriverPost.class);
                                intent.putExtra("name", name);
                                intent.putExtra("username", userName);
                                intent.putExtra("startLocation", startLocation);
                                intent.putExtra("endLocation", endLocation);
                                intent.putExtra("date", date);
                                intent.putExtra("time", time);
                                intent.putExtra("tripCost", tripCost);
                                intent.putExtra("carName", carName);
                                intent.putExtra("years_driving", years_driving);
                                intent.putExtra("maxPassengers", maxPassengers);
                                intent.putExtra("hasTraveledBefore", hasTraveledBefore);
                                intent.putExtra("comments", comments);
                                intent.putStringArrayListExtra("carList", carList);
                                startActivity(intent);
                            });

                        });
                        // } else {
                        //Toast.makeText(DriverInfoForm.this, "Ανεπιτυχής υποβολή αιτήματος επιβίβασης", Toast.LENGTH_SHORT).show();
                            /*new androidx.appcompat.app.AlertDialog.Builder(FormNewRouteDriver.this)
                                    .setTitle("Λάθος")
                                    .setMessage("Ανεπιτυχής υποβολή αιτήματος επιβίβασης")
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();*/
                        //}
                    }


                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> new androidx.appcompat.app.AlertDialog.Builder(DriverInfoForm.this)
                            .setTitle("Λάθος")
                            .setMessage("Ένα λάθος συνέβη κατά την υποβολή του αιτήματος")
                            .setPositiveButton(android.R.string.ok, null)
                            .show());
                }
                /*handler.post(() -> {
                    Intent intent = new Intent(FormNewRouteDriver.this, PreviewScreenForm.class);
                    intent.putExtra("username", userName);
                    intent.putExtra("startLocation", startLocation);
                    intent.putExtra("endLocation", endLocation);
                    intent.putExtra("date", date);
                    intent.putExtra("time", time);
                    intent.putExtra("tripCost", tripCost);
                    intent.putExtra("carName", carName);
                    intent.putExtra("maxPassengers", maxPassengers);
                    intent.putExtra("hasTraveledBefore", hasTraveledBefore);
                    intent.putStringArrayListExtra("carList", carList);
                    startActivity(intent);
                });*/
            });

            /*Intent intent = new Intent(FormNewRouteDriver.this, PreviewScreenForm.class);
            intent.putExtra("username", userName);
            intent.putExtra("startLocation", startLocation);
            intent.putExtra("endLocation", endLocation);
            intent.putExtra("date", date);
            intent.putExtra("time", time);
            intent.putExtra("tripCost", tripCost);
            intent.putExtra("carName", carName);
            intent.putExtra("maxPassengers", maxPassengers);
            intent.putExtra("hasTraveledBefore", hasTraveledBefore);
            startActivity(intent);*/
        }
    }

    public void ButtonBack(View view) {
        Intent intent = new Intent(DriverInfoForm.this, CentralMenuUser.class);
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

    private void initTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                selectedTime = makeTimeString(hour, minute);
                timePickerButton.setText(selectedTime);
            }
        };
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(this, timeSetListener, hour, minute, true);
    }

    private String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        return makeTimeString(hour, minute);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String makeTimeString(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
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

    public void openTimePicker(View view) {
        timePickerDialog.show();
    }


    //Αναζήτηση για τοποθεσίες Αφετηριων
    private class SearchLocationTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            String query = params[0];
            ArrayList<String> locations = new ArrayList<>();

            try {
                Connection connection = ConnectionClass.CONN();
                String normalizedQuery = Normalizer.normalize(query, Normalizer.Form.NFD)
                        .replaceAll("\\p{M}", "").toLowerCase();

                String sql = "SELECT starting_point FROM StartingEndPoint WHERE LOWER(CONVERT(starting_point USING utf8)) LIKE ?";
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(DriverInfoForm.this, android.R.layout.simple_dropdown_item_1line, locations);
                startLocationAutoCompleteTextView.setAdapter(adapter);
            }
        }
    }

    //Αναζήτηση για τοποθεσίες Προορισμων
    private class SearchEndLocationTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            String query = params[0];
            ArrayList<String> locations = new ArrayList<>();

            try {
                Connection connection = ConnectionClass.CONN();
                String normalizedQuery = Normalizer.normalize(query, Normalizer.Form.NFD)
                        .replaceAll("\\p{M}", "").toLowerCase();

                String sql = "SELECT final_destination FROM StartingEndPoint WHERE LOWER(CONVERT(final_destination USING utf8)) LIKE ?";
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(DriverInfoForm.this, android.R.layout.simple_dropdown_item_1line, locations);
                endLocationAutoCompleteTextView.setAdapter(adapter);
            }
        }
    }
}

