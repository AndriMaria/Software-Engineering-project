package com.example.pedri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import java.sql.Connection;


public class CentralMenuUser extends AppCompatActivity {

    private String userName;

    protected void onCreate (Bundle savedInstanceState) {
        Connection con = ConnectionClass.CONN();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.central_menu_user);

        String name = getIntent().getStringExtra("name");
        String contact = getIntent().getStringExtra("contact");
        String address = getIntent().getStringExtra("address");
        String id = getIntent().getStringExtra("id");

        //Παίρνει τις ενημερωμένα στοιχεία
        userName = getIntent().getStringExtra("username");
    }
    //Κουμπί Κράτησης
    public void Reservation(View view) {
        showServices();
    }
    public void showServices(){
        Intent intent=new Intent(CentralMenuUser.this,ServicesPage.class);
        intent.putExtra("username",userName);
        startActivity(intent);
    }


    public void btn_Profil(View view) {
        //String userName = getIntent().getStringExtra("username");
        Intent intent = new Intent(CentralMenuUser.this, Profile.class);
        intent.putExtra("username", userName);
        //intent.putExtra("username_owner", userNameOwner);
        startActivity(intent);
    }

    public void carRental(View view) {
        Intent intent=new Intent(CentralMenuUser.this,CarRental.class);
        intent.putExtra("username",userName);
        startActivity(intent);
    }

    public void showForm(View view) {
        Intent intent=new Intent(CentralMenuUser.this,ProfileConfirmation.class);
        intent.putExtra("source", "passenger");
        intent.putExtra("username",userName);
        startActivity(intent);
    }

    public void ChooseCarPoolDriver(View view) {
        Intent intent=new Intent(CentralMenuUser.this,ProfileConfirmation.class);
        intent.putExtra("source", "driver");
        intent.putExtra("username",userName);
        startActivity(intent);
    }

    public void chooseWallet(View view) {
        show_wp();
    }
    public void show_wp(){
        Intent intent=new Intent(CentralMenuUser.this,WalletProfile.class);
        intent.putExtra("username",userName);
        startActivity(intent);
    }
}

