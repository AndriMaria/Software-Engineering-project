package com.example.pedri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Profile extends AppCompatActivity {
    private TextView nameTextView,ageTextView, contactTextView, addressTextView, idTextView;
    private String userName,userNameOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);


        // Λήψη του username και userNameOwner από το Intent
        userName = getIntent().getStringExtra("username");
        userNameOwner=getIntent().getStringExtra("username_owner");


        ageTextView=findViewById(R.id.ageTextView);
        nameTextView = findViewById(R.id.nameTextView);
        contactTextView = findViewById(R.id.contactTextView);
        addressTextView = findViewById(R.id.addressTextView);
        idTextView = findViewById(R.id.idTextView);



        // Έλεγχος αν το username έχει ληφθεί σωστά
        if (userName != null) {
            fetchProfileDetailsUser();
        } else if(userNameOwner!= null){
            fetchProfileDetailsOwner();
        }
    }

    private void fetchProfileDetailsUser() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                String sql = "SELECT * FROM user WHERE username = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, userName);
                ResultSet rs = stmt.executeQuery();


                if (rs.next()) {
                    String name = rs.getString("name");
                    String age = rs.getString("age");
                    String contact=rs.getString("contact_number");
                    String address=rs.getString("address");
                    String id=rs.getString("identity");


                    runOnUiThread(() -> {
                        nameTextView.setText("Ονοματεπώνυμο: " + name);
                        ageTextView.setText("Ηλικία: "+age);
                        contactTextView.setText("Αριθμός Επικοινωνίας: " + contact);
                        addressTextView.setText("Διεύθυνση: " + address);
                        idTextView.setText("Ταυτότητα: " + id);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void fetchProfileDetailsOwner() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                String sql = "SELECT * FROM owner WHERE username_owner = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, userNameOwner);
                ResultSet rs = stmt.executeQuery();


                if (rs.next()) {
                    String name = rs.getString("name");
                    String age = rs.getString("age");
                    String contact=rs.getString("contact_number");
                    String address=rs.getString("address");
                    String id=rs.getString("identity");


                    runOnUiThread(() -> {
                        nameTextView.setText("Ονοματεπώνυμο: " + name);
                        ageTextView.setText("Ηλικία: "+age);
                        contactTextView.setText("Αριθμός Επικοινωνίας: " + contact);
                        addressTextView.setText("Διεύθυνση: " + address);
                        idTextView.setText("Ταυτότητα: " + id);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void ChangeInfo(View view) {
        if(userName!=null) {
            String userName = getIntent().getStringExtra("username");
            Intent intent = new Intent(Profile.this, PasswordInput.class);
            intent.putExtra("username", userName);
            startActivity(intent);
        } else if (userNameOwner!= null) {
            Intent intent = new Intent(Profile.this, PasswordInput.class);
            intent.putExtra("username_owner", userNameOwner);
            startActivity(intent);
            finish();
        }
    }


    public void ButtonBack(View view) {
        if(userName!=null) {
            Intent intent = new Intent(Profile.this, CentralMenuUser.class);
            intent.putExtra("username", userName);
            startActivity(intent);
            //finish();
        }else if(userNameOwner!= null){
            Intent intent = new Intent(Profile.this, CentralMenuOwner.class);
            intent.putExtra("username_owner", userNameOwner);
            startActivity(intent);
            //finish();
        }
    }


}
