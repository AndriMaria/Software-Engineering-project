package com.example.pedri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ServicesPage extends CentralMenuUser {

    private String userName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_page);

        userName=getIntent().getStringExtra("username");


    }




    public void ButtonBack(View view) {
        Intent intent=new Intent(ServicesPage.this, CentralMenuUser.class);
        intent.putExtra("username",userName);
        startActivity(intent);
    }



    public void btnParking(View view) {
        Intent intent=new Intent(ServicesPage.this, InformationInputPage.class);
        intent.putExtra("username",userName);
        intent.putExtra("source", "btnParking");
        startActivity(intent);
    }

    public void btnCarwash(View view) {
        Intent intent=new Intent(ServicesPage.this, InformationInputPage.class);
        intent.putExtra("username",userName);
        intent.putExtra("source", "btnCarwash");
        startActivity(intent);
    }

}
