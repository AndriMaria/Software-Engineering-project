package com.example.pedri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeDataProfile extends AppCompatActivity {
    private EditText nameEditText, contactEditText, addressEditText, idEditText;
    private Button saveInfoButton;
    private String userName, userNameOwner;

    private String name, contact, address, identity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_data_profile);

        nameEditText = findViewById(R.id.nameEditText);
        contactEditText = findViewById(R.id.contactEditText);
        addressEditText = findViewById(R.id.addressEditText);
        idEditText = findViewById(R.id.idEditText);
        saveInfoButton = findViewById(R.id.SaveInfo);

        userName = getIntent().getStringExtra("username");
        userNameOwner = getIntent().getStringExtra("username_owner");

        // Receive the data from the Intent
        if (userName != null) {
            nameEditText.setText(getIntent().getStringExtra("name"));
            contactEditText.setText(getIntent().getStringExtra("contact"));
            addressEditText.setText(getIntent().getStringExtra("address"));
            idEditText.setText(getIntent().getStringExtra("identity"));
        } else if (userNameOwner != null) {
            nameEditText.setText(getIntent().getStringExtra("name"));
            contactEditText.setText(getIntent().getStringExtra("contact"));
            addressEditText.setText(getIntent().getStringExtra("address"));
            idEditText.setText(getIntent().getStringExtra("identity"));
        }

        saveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndProceed();
            }
        });
    }

    private void checkAndProceed() {
        name = nameEditText.getText().toString().trim();
        contact = contactEditText.getText().toString().trim();
        address = addressEditText.getText().toString().trim();
        identity = idEditText.getText().toString().trim();

        if (name.isEmpty() || contact.isEmpty() || address.isEmpty() || identity.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show();
        } else {
            goVerificationPage();
        }
    }

    private void goVerificationPage() {
        Intent intent = new Intent(ChangeDataProfile.this, VerificationPage.class);
        if (userName != null) {
            intent.putExtra("username", userName);
        } else if (userNameOwner != null) {
            intent.putExtra("username_owner", userNameOwner);
        }
        intent.putExtra("name", name);
        intent.putExtra("contact", contact);
        intent.putExtra("address", address);
        intent.putExtra("identity", identity);
        startActivity(intent);
    }
}