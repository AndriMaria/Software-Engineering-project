package com.example.pedri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InformationOfCars extends AvailableListCar {
    private TextView carNameTextView,FuelTextView, consumptionTextView, emissionsTextView,
            maxSpeedTextView,numberSeatsTextView;
    private String carName,userName,companyName;
    ArrayList<String> carList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_of_cars);

        FuelTextView=findViewById(R.id.FuelTextView);
        carNameTextView = findViewById(R.id.carNameTextView);
        consumptionTextView = findViewById(R.id.consumptionTextView);
        emissionsTextView = findViewById(R.id.emissionsTextView);
        maxSpeedTextView = findViewById(R.id.maxSpeedTextView);
        numberSeatsTextView=findViewById(R.id.numberSeatsTextView);


        carList=getIntent().getStringArrayListExtra("carList");
        companyName=getIntent().getStringExtra("cars_rental_company");
        userName = getIntent().getStringExtra("username");
        carName = getIntent().getStringExtra("carName");


        carNameTextView.setText(carName);

        AskCarsInfo();
    }

    private void AskCarsInfo() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                String sql = "SELECT * FROM cars WHERE name_car = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, carName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String fuel=rs.getString("fuel");
                    String consumption = rs.getString("consumption_per_100km");
                    String emissions = rs.getString("emissions");
                    String maxSpeed = rs.getString("max_speed");
                    String numberSeats = rs.getString("number_of_seats");

                    runOnUiThread(() -> {
                        FuelTextView.setText(fuel);
                        consumptionTextView.setText(consumption);
                        emissionsTextView.setText(emissions);
                        maxSpeedTextView.setText(maxSpeed);
                        numberSeatsTextView.setText(numberSeats);

                    });
                }

                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void Rent(View view) {
        Intent intent=new Intent(InformationOfCars.this,RentalInfoForm.class);
        intent.putExtra("username",userName);
        intent.putExtra("carName", carName);
        intent.putExtra("cars_rental_company",companyName);
        intent.putStringArrayListExtra("carList",carList);
        startActivity(intent);
    }

    public void ButtonBack(View view) {
        Intent intent =new Intent(InformationOfCars.this,AvailableListCar.class);
        intent.putStringArrayListExtra("carList",carList);
        intent.putExtra("username",userName);
        intent.putExtra("carName", carName);

        startActivity(intent);
    }
}
