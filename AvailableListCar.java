package com.example.pedri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class AvailableListCar extends CarRental{
    private ListView listViewCars;
    private TextView noCarsTextView;
    private String userName,companyName;
    ArrayList<String> carList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_list_car);


        carList = getIntent().getStringArrayListExtra("carList");
        companyName=getIntent().getStringExtra("cars_rental_company");
        userName=getIntent().getStringExtra("username");
        SelectCar();
    }

    public void ButtonBack(View view) {
        Intent intent =new Intent(AvailableListCar.this,CarRental.class);
        intent.putExtra("username",userName);
        startActivity(intent);
    }

    public void SelectCar(){
        listViewCars = findViewById(R.id.listViewCars);
        noCarsTextView = findViewById(R.id.noCarsTextView);

         carList = getIntent().getStringArrayListExtra("carList");

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
                    Intent intent = new Intent(AvailableListCar.this, InformationOfCars.class);
                    intent.putExtra("carName", selectedCar);
                    intent.putStringArrayListExtra("carList",carList);
                    intent.putExtra("cars_rental_company",companyName);
                    intent.putExtra("username",userName);
                    startActivity(intent);
                }
            });
        }
    }
}
