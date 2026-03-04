package com.example.pedri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PasswordInput extends Profile {
    private EditText passwordEditText;
    private String userName, userNameOwner;
    private String name, contact, identity, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_input);

        passwordEditText = findViewById(R.id.passwordEditText);

        // Receive the username from the Intent
        userName = getIntent().getStringExtra("username");
        userNameOwner = getIntent().getStringExtra("username_owner");
    }

    public void InsertPassword(View view) {
        if (userName != null) {
            // For User
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                String enteredPassword = passwordEditText.getText().toString();
                try {
                    Connection con = ConnectionClass.CONN();
                    String sql = "SELECT password, name, contact_number, address, identity FROM user WHERE username = ?";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, userName);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String correctPassword = rs.getString("password");
                        if (enteredPassword.equals(correctPassword)) {
                            name = rs.getString("name");
                            contact = rs.getString("contact_number");
                            address = rs.getString("address");
                            identity = rs.getString("identity");

                            runOnUiThread(() -> {
                                goChangeData();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(PasswordInput.this, "Λάθος κωδικός", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else if (userNameOwner != null) {
            // For Owner
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                String enteredPassword = passwordEditText.getText().toString();
                try {
                    Connection con = ConnectionClass.CONN();
                    String sql = "SELECT password_owner, name, contact_number, address, identity FROM owner WHERE username_owner = ?";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, userNameOwner);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String correctPassword = rs.getString("password_owner");
                        if (enteredPassword.equals(correctPassword)) {
                            name = rs.getString("name");
                            contact = rs.getString("contact_number");
                            address = rs.getString("address");
                            identity = rs.getString("identity");

                            runOnUiThread(() -> {
                                goChangeData();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(PasswordInput.this, "Λάθος κωδικός", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void goChangeData() {
        if (userName != null) {
            Intent intent = new Intent(PasswordInput.this, ChangeDataProfile.class);
            intent.putExtra("username", userName);
            intent.putExtra("name", name);
            intent.putExtra("contact", contact);
            intent.putExtra("address", address);
            intent.putExtra("identity", identity);
            startActivity(intent);
        } else if (userNameOwner != null) {
            Intent intent = new Intent(PasswordInput.this, ChangeDataProfile.class);
            intent.putExtra("username_owner", userNameOwner);
            intent.putExtra("name", name);
            intent.putExtra("contact", contact);
            intent.putExtra("address", address);
            intent.putExtra("identity", identity);
            startActivity(intent);
        }
    }
}
