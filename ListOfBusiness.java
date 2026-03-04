package com.example.pedri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.util.ArrayList;

public class ListOfBusiness extends InformationInputPage {

    private ListView ChooseBusiness;
    private String userName,userNameOwner,selectedRegion;
    private String source;

    private Connection con;
    private ArrayList<String[]> availableParkings;
    private ArrayList<String[]> availableCarWash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_bussiness);

        ChooseBusiness = findViewById(R.id.list_view);
        userName = getIntent().getStringExtra("username");
        //userNameOwner=getIntent().getStringExtra("username_owner");
        source = getIntent().getStringExtra("source");





        availableParkings = (ArrayList<String[]>) getIntent().getSerializableExtra("availableParkings");
        availableCarWash = (ArrayList<String[]>) getIntent().getSerializableExtra("availableCarWash");

        if ("btnParking".equals(source)) {
            // Λογική για το btnParking


        if (availableParkings != null) {
            //Λογική για Parking
            selectedRegion = getIntent().getStringExtra("location_parking");
            ArrayList<String> parkingNames = new ArrayList<>();
            for (String[] parkingInfo : availableParkings) {
                parkingNames.add(parkingInfo[0]);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, parkingNames);
            ChooseBusiness.setAdapter(adapter);

            ChooseBusiness.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String[] parkingInfo = availableParkings.get(position);
                    Intent intent = new Intent(ListOfBusiness.this, BusinessInfo.class);
                    intent.putExtra("name_parking", parkingInfo[0]);
                    intent.putExtra("telephone", parkingInfo[1]);
                    intent.putExtra("address", parkingInfo[2]);
                    intent.putExtra("parking_cost_per_hour", parkingInfo[3]);
                    intent.putExtra("username_owner",parkingInfo[4]);
                    //intent.putExtra("username_owner",userNameOwner);
                    intent.putExtra("username", userName);
                    intent.putExtra("availableParkings", availableParkings);
                    intent.putExtra("location_parking", selectedRegion);

                    intent.putExtra("selected_date", getIntent().getStringExtra("selected_date")); // Περάστε την ημερομηνία
                    intent.putExtra("selected_hour", getIntent().getStringExtra("selected_hour")); // Περάστε την ώρα
                    //intent.putExtra("btnParking",source);
                    intent.putExtra("source", source);

                    startActivity(intent);
                }
            });

        }} else if ("btnCarwash".equals(source)) {
            // Λογική για το btnCarwash
            if (availableCarWash != null) {
                //Λογική για CarWash
                selectedRegion = getIntent().getStringExtra("location_carWash");
                ArrayList<String> CarWashNames = new ArrayList<>();
                for (String[] CarWashInfo : availableCarWash) {
                    CarWashNames.add(CarWashInfo[0]);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, CarWashNames);
                ChooseBusiness.setAdapter(adapter);

                ChooseBusiness.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String[] CarWashInfo = availableCarWash.get(position);
                        Intent intent = new Intent(ListOfBusiness.this, BusinessInfo.class);
                        intent.putExtra("name_carWash", CarWashInfo[0]);
                        intent.putExtra("telephone", CarWashInfo[1]);
                        intent.putExtra("address", CarWashInfo[2]);
                        intent.putExtra("carWash_cost_per_hour", CarWashInfo[3]);
                        intent.putExtra("username_owner",CarWashInfo[4]);
                        //intent.putExtra("username_owner", userNameOwner);

                        intent.putExtra("username", userName);
                        intent.putExtra("availableCarWash", availableCarWash);
                        intent.putExtra("location_carWash", selectedRegion);

                        intent.putExtra("selected_date", getIntent().getStringExtra("selected_date")); // Περάστε την ημερομηνία
                        intent.putExtra("selected_hour", getIntent().getStringExtra("selected_hour")); // Περάστε την ώρα
                        intent.putExtra("source", source);

                        //intent.putExtra("btnCarwash",source);
                        startActivity(intent);
                    }
                });

            }

        }



    }

    // Έξοδος προς την προηγούμενη δραστηριότητα
    public void ButtonBack(View view) {
        Intent intent = new Intent(ListOfBusiness.this, InformationInputPage.class);
        intent.putExtra("username", userName);
        intent.putExtra("username_owner", userNameOwner);
        //intent.putExtra("btnCarwash",source);
        //intent.putExtra("btnParking",source);
        intent.putExtra("source", source);

        startActivity(intent);
    }
}