package com.example.pedri;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Vehicle extends Route {

    private ListView listViewCars;
    private TextView noCarsTextView;
    private String userName,carName,passengerCount,luggageCount,date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle);

        luggageCount=getIntent().getStringExtra("luggageCount");
        passengerCount=getIntent().getStringExtra("passengerCount");
        userName = getIntent().getStringExtra("username");
        carName = getIntent().getStringExtra("carName");
        ArrayList<String> carList = getIntent().getStringArrayListExtra("carList");

        date = getIntent().getStringExtra("date");


        SelectCar(carList);
    }

    public void ButtonBack(View view) {
        Intent intent = new Intent(Vehicle.this, Route.class);
        intent.putExtra("username", userName);
        startActivity(intent);
    }

    public void SelectCar(ArrayList<String> carList) {
        listViewCars = findViewById(R.id.listViewCars);
        noCarsTextView = findViewById(R.id.noCarsTextView);

        if (carList == null || carList.isEmpty()) {
            noCarsTextView.setVisibility(View.VISIBLE);
        } else {
            listViewCars.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carList);
            listViewCars.setAdapter(adapter);

            listViewCars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCar = carList.get(position).split(" - ")[0];
                    Intent intent = new Intent(Vehicle.this, Driver.class);
                    intent.putExtra("carName", selectedCar);
                    intent.putExtra("username", userName);
                    intent.putExtra("passengerCount", passengerCount);
                    intent.putExtra("luggageCount", luggageCount);
                    intent.putExtra("date", getIntent().getStringExtra("date")); // Πρόσθεσε την ημερομηνία
                    intent.putStringArrayListExtra("carList", carList);
                    startActivity(intent);
                }
            });
        }
    }

}

