package com.example.pedri;





import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import  java.sql.Statement;
import java.sql.SQLException;
import java.sql.SQLException.*;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.sql.*;
import javax.sql.*;
import javax.xml.transform.Result;

public class LogIn extends AppCompatActivity {


    String name, str;
    ResultSet rs;
    Connection con;
    ConnectionClass connectionClass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_menu);

        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);

        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);
        connectionClass = new ConnectionClass();
        connect();



    //admin and admin

        loginbtn.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick (View v){
           login();
        }

        });
    }





    public void connect() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                con = ConnectionClass.CONN();
                if (con == null)
                    str = "Error in connection in MySQL Server";
                else
                    str = "Connected with MySql Server";
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            runOnUiThread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
            });
        });

    }
    public void login() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                TextView username = findViewById(R.id.username);
                TextView password = findViewById(R.id.password);
                con = ConnectionClass.CONN();
                String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
                String sql1 = "SELECT * FROM owner WHERE username_owner = ? AND password_owner = ?";


                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, username.getText().toString());
                stmt.setString(2, password.getText().toString());
                ResultSet rs = stmt.executeQuery();

                PreparedStatement stmt1 = con.prepareStatement(sql1);
                stmt1.setString(1, username.getText().toString());
                stmt1.setString(2, password.getText().toString());
                ResultSet rs1 = stmt1.executeQuery();

                String pass_word = null;
                String user_name = null;
                String password_owner = null;
                String user_owner = null;

                while (rs.next()) {
                    user_name = rs.getString("username");
                    pass_word = rs.getString("password");
                }
                while (rs1.next()) {
                    user_owner = rs1.getString("username_owner");
                    password_owner = rs1.getString("password_owner");
                }
                String finalUser_name = user_name;
                String finalPass_word = pass_word;
                String finalUsername_owner = user_owner;
                String finalPassword_owner = password_owner;

                runOnUiThread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (username.getText().toString().equals(finalUser_name) &&
                            password.getText().toString().equals(finalPass_word)) {
                        Toast.makeText(LogIn.this, "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LogIn.this, CentralMenuUser.class);
                        intent.putExtra("username", username.getText().toString());
                        startActivity(intent);



                    } else if (username.getText().toString().equals(finalUsername_owner) &&
                            password.getText().toString().equals(finalPassword_owner)) {
                        Toast.makeText(LogIn.this, "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LogIn.this, CentralMenuOwner.class);
                        intent.putExtra("username_owner", finalUsername_owner);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LogIn.this, "LOGIN FAILED !!!", Toast.LENGTH_SHORT).show();
                    }
                });
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}