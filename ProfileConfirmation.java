package com.example.pedri;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileConfirmation extends Profile {
    private TextView verificationStatusTextView;
    String userName;
    private String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_confirmation);

        verificationStatusTextView = findViewById(R.id.verificationStatusTextView);


        userName=getIntent().getStringExtra("username");

        // Παίρνουμε την πληροφορία για την πηγή από το Intent
        source = getIntent().getStringExtra("source");

        // Ελέγχουμε αν ο χρήστης είναι επιβεβαιωμένος
        new CheckUserVerificationTask().execute(userName);
    }

    private class CheckUserVerificationTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            boolean isVerified = false;

            try {
                // Στοιχεία σύνδεσης στη βάση δεδομένων
                Connection connection = ConnectionClass.CONN();
                String query = "SELECT verifProf FROM user WHERE username = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    // Ελέγχουμε αν η τιμή της στήλης verifProf είναι "Verified"
                    isVerified = "Verified".equals(resultSet.getString("verifProf"));
                }

                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return isVerified;
        }

        @Override
        protected void onPostExecute(Boolean isVerified) {
            if (isVerified) {
                Toast.makeText(ProfileConfirmation.this, "Verified", Toast.LENGTH_SHORT).show();
                if ("passenger".equals(source)) {
                    // Λογική για επιβάτη
                    Intent intent=new Intent(ProfileConfirmation.this,Route.class);
                    intent.putExtra("username", userName);
                    startActivity(intent);

                } else if ("driver".equals(source)) {
                    // Λογική για οδηγό
                    Intent intent=new Intent(ProfileConfirmation.this,DriverInfoForm.class);
                    intent.putExtra("username", userName);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(ProfileConfirmation.this, "Επιβεβαιώσε το προφίλ για να συνεχίσετε στην υπηρεσία.", Toast.LENGTH_SHORT).show();
                //verificationStatusTextView.setText("Not Verified");
                Intent intent = new Intent(ProfileConfirmation.this, Profile.class);
                intent.putExtra("username", userName);
                startActivity(intent);
            }
        }
    }
}
