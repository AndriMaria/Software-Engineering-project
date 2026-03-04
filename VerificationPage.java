package com.example.pedri;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VerificationPage extends ChangeDataProfile {
    private EditText nameEditText, contactEditText, addressEditText, idEditText;
    private Button Verify, cancelButton;
    private String userName,userNameOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification_page);

        nameEditText = findViewById(R.id.nameEditText);
        contactEditText = findViewById(R.id.contactEditText);
        addressEditText = findViewById(R.id.addressEditText);
        idEditText = findViewById(R.id.idEditText);
        Verify = findViewById(R.id.Verify);
        cancelButton = findViewById(R.id.cancelButton);
        userName = getIntent().getStringExtra("username");
        userNameOwner = getIntent().getStringExtra("username_owner");
        // Λήψη των δεδομένων από το Intent
        if(userName!=null) {
            //Για τον Απλο Χρηστη
            //userName = getIntent().getStringExtra("username");
            nameEditText.setText(getIntent().getStringExtra("name"));
            contactEditText.setText(getIntent().getStringExtra("contact"));
            addressEditText.setText(getIntent().getStringExtra("address"));
            idEditText.setText(getIntent().getStringExtra("identity"));
        } else if (userNameOwner!=null) {
            //Για τον ΙΔΙΟΚΤΗΤΗ
            //userNameOwner = getIntent().getStringExtra("username_owner");
            nameEditText.setText(getIntent().getStringExtra("name"));
            contactEditText.setText(getIntent().getStringExtra("contact"));
            addressEditText.setText(getIntent().getStringExtra("address"));
            idEditText.setText(getIntent().getStringExtra("identity"));
        }

        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmChanges();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void confirmChanges() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Είστε σίγουροι ότι θέλετε να αποθηκεύσετε τις αλλαγές;");
        builder.setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveInfo();
            }
        });
        builder.setNegativeButton("Όχι", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void SaveInfo() {
        if(userName!=null) {
            //Για τον Απλο Χρηστη
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    Connection con = ConnectionClass.CONN();
                    String sql = "UPDATE user SET name = ?, contact_number = ?, address = ?, identity = ? WHERE username = ?";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, nameEditText.getText().toString());
                    stmt.setString(2, contactEditText.getText().toString());
                    stmt.setString(3, addressEditText.getText().toString());
                    stmt.setString(4, idEditText.getText().toString());
                    stmt.setString(5, userName);

                    int rowsUpdated = stmt.executeUpdate();

                    runOnUiThread(() -> {
                        if (rowsUpdated > 0) {
                            Toast.makeText(VerificationPage.this, "Τα στοιχεία ενημερώθηκαν επιτυχώς", Toast.LENGTH_SHORT).show();

                            GoProfile();
                        } else {
                            Toast.makeText(VerificationPage.this, "Αποτυχία ενημέρωσης στοιχείων", Toast.LENGTH_SHORT).show();
                        }
                    });

                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(VerificationPage.this, "Σφάλμα κατά την ενημέρωση των στοιχείων", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else if (userNameOwner!=null) {
            //Για τον ιδιοκτητη
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    Connection con = ConnectionClass.CONN();
                    String sql = "UPDATE owner SET name = ?, contact_number = ?, address = ?, identity = ? WHERE username_owner = ?";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, nameEditText.getText().toString());
                    stmt.setString(2, contactEditText.getText().toString());
                    stmt.setString(3, addressEditText.getText().toString());
                    stmt.setString(4, idEditText.getText().toString());
                    stmt.setString(5, userNameOwner);

                    int rowsUpdated = stmt.executeUpdate();

                    runOnUiThread(() -> {
                        if (rowsUpdated > 0) {
                            Toast.makeText(VerificationPage.this, "Τα στοιχεία ενημερώθηκαν επιτυχώς", Toast.LENGTH_SHORT).show();

                            GoProfile();
                        } else {
                            Toast.makeText(VerificationPage.this, "Αποτυχία ενημέρωσης στοιχείων", Toast.LENGTH_SHORT).show();
                        }
                    });

                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(VerificationPage.this, "Σφάλμα κατά την ενημέρωση των στοιχείων", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }
    public void GoProfile(){
        if(userName!=null) {
            Intent intent = new Intent(VerificationPage.this, Profile.class);
            intent.putExtra("username", userName);
            startActivity(intent);
            finish();
        } else if (userNameOwner!=null) {
            Intent intent = new Intent(VerificationPage.this, Profile.class);
            intent.putExtra("username_owner", userNameOwner);
            startActivity(intent);
            finish();
        }
    }

}
