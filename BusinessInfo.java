package com.example.pedri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BusinessInfo extends ListOfBusiness {

    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView addressTextView;
    private TextView costTextView;
    private String userName,userNameOwner, costPerHour,source,costPerHourCarWash,selectedRegion;

    private String name,phone,address,nameCarWash,phoneCarWash,addressCarWash;
    private ArrayList<String[]> availableParkings,availableCarWash;
    private double moneyAccount,moneyAccountOwner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_info);


        nameTextView = findViewById(R.id.name_text_view);
        phoneTextView = findViewById(R.id.phone_text_view);
        addressTextView = findViewById(R.id.address_text_view);
        costTextView = findViewById(R.id.cost_text_view);

        userName = getIntent().getStringExtra("username");
        userNameOwner = getIntent().getStringExtra("username_owner");
        source = getIntent().getStringExtra("source");

        //source=getIntent().getStringExtra("btnParking");
        //source=getIntent().getStringExtra("btnCarwash");


        if ("btnParking".equals(source)) {
            // Λογική για το btnParking

            availableParkings = (ArrayList<String[]>) getIntent().getSerializableExtra("availableParkings");

            // Λήψη των παραμέτρων από το Intent και εμφάνισή τους στα αντίστοιχα TextView
         name = getIntent().getStringExtra("name_parking");
         phone = getIntent().getStringExtra("telephone");
         address = getIntent().getStringExtra("address");
         costPerHour = getIntent().getStringExtra("parking_cost_per_hour");

        nameTextView.setText(name);
        phoneTextView.setText(phone);
        addressTextView.setText(address);
        costTextView.setText(String.valueOf(costPerHour));
        } else if ("btnCarwash".equals(source)) {
            // Λογική για το btnCarwash

             availableCarWash = (ArrayList<String[]>) getIntent().getSerializableExtra("availableCarWash");

            // Λήψη των παραμέτρων από το Intent και εμφάνισή τους στα αντίστοιχα TextView
             nameCarWash = getIntent().getStringExtra("name_carWash");
             phoneCarWash = getIntent().getStringExtra("telephone");
             addressCarWash = getIntent().getStringExtra("address");
             costPerHourCarWash = getIntent().getStringExtra("carWash_cost_per_hour");

            nameTextView.setText(nameCarWash);
            phoneTextView.setText(phoneCarWash);
            addressTextView.setText(addressCarWash);
            costTextView.setText(String.valueOf(costPerHourCarWash));

        }


    }

    public void ButtonBack(View view) {
        // Επιστροφή στην προηγούμενη δραστηριότητα
        Intent intent = new Intent(BusinessInfo.this, ListOfBusiness.class);
        intent.putExtra("availableParkings", availableParkings);
        intent.putExtra("availableCarWash", availableCarWash);
        intent.putExtra("source", source);

        // intent.putExtra("btnParking",source);
       // intent.putExtra("btnCarwash",source);

        intent.putExtra("username", userName);
        startActivity(intent);
    }

    public void ReservatonAndPayment(View view) {

        if ("btnParking".equals(source)) {
            // Λογική για το btnParking

            selectedRegion = getIntent().getStringExtra("location_parking");

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                String sql = "SELECT * FROM WalletProfileUser WHERE username_wallet = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, userName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    moneyAccount = rs.getDouble("money_account");
                    double totalCost = Double.parseDouble(costPerHour);

                    if (moneyAccount >= totalCost) {
                        double newBalance = moneyAccount - totalCost;

                        // Προσθήκη της ενοικίασης ως συναλλαγή στο ιστορικό του χρήστη
                        String insertSql = "INSERT INTO HistoryTransaction (username_wallet, transaction_date, amount, description,name_business) VALUES (?, NOW(), ?, ?,?)";
                        PreparedStatement insertStmt = con.prepareStatement(insertSql);
                        insertStmt.setString(1, userName);
                        insertStmt.setDouble(2, totalCost);
                        insertStmt.setString(3, "Αίτηση Parking");
                        insertStmt.setString(4, name);

                        insertStmt.executeUpdate();



                        // Ενημέρωση του υπολοίπου του χρήστη
                        String updateSql = "UPDATE WalletProfileUser SET money_account = ? WHERE username_wallet = ?";
                        PreparedStatement updateStmt = con.prepareStatement(updateSql);
                        updateStmt.setDouble(1, newBalance);
                        updateStmt.setString(2, userName);
                        updateStmt.executeUpdate();





                        // Ανάκτηση επιλεγμένης ημερομηνίας και ώρας
                        String selectedDate = getIntent().getStringExtra("selected_date");
                        String selectedHour = getIntent().getStringExtra("selected_hour");



                        // Εισαγωγή νέας κράτησης στον πίνακα ParkingReservations
                        String parkingName = getIntent().getStringExtra("name_parking");
                        String reservationSql = "INSERT INTO ParkingReservations (username, name_parking, reservation_date, reservation_time, cost,location_parking,username_owner) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement reservationStmt = con.prepareStatement(reservationSql);
                        reservationStmt.setString(1, userName);
                        reservationStmt.setString(2, parkingName);
                        reservationStmt.setString(3, selectedDate);
                        reservationStmt.setString(4, selectedHour);
                        reservationStmt.setDouble(5, totalCost);
                        reservationStmt.setString(6, selectedRegion);
                        reservationStmt.setString(7, userNameOwner);

                        reservationStmt.executeUpdate();

                        /*
                        //Ανάκτηση προφιλ του Owner
                        String sql2 = "SELECT * FROM WalletProfileUser WHERE username_wallet = ?";
                        PreparedStatement stmt2 = con.prepareStatement(sql2);
                        stmt2.setString(1, userNameOwner);
                        ResultSet rs2 = stmt2.executeQuery();
                        if (rs2.next()) {
                            moneyAccountOwner = rs2.getDouble("money_account");

                            //Προσθήκη χρημάτων στο πορτοφόλι του Owner
                            double newBalanceOwner = moneyAccountOwner + totalCost;
                            String updateSqlOwner = "UPDATE WalletProfileUser SET money_account = ? WHERE username_wallet = ?";
                            PreparedStatement stmtOwner = con.prepareStatement(updateSqlOwner);
                            stmtOwner.setDouble(1, newBalanceOwner);
                            stmtOwner.setString(2, userNameOwner);
                            stmtOwner.executeUpdate();
                        }
                        // Προσθήκη της ενοικίασης ως συναλλαγή στο ιστορικό Ιδιοκτητη
                        String insertSqlOwner = "INSERT INTO HistoryTransaction (username_wallet, transaction_date, amount, description) VALUES (?, NOW(), ?, ?)";
                        PreparedStatement insertStmtOwner = con.prepareStatement(insertSqlOwner);
                        insertStmtOwner.setString(1, userNameOwner);
                        insertStmtOwner.setDouble(2, totalCost);
                        insertStmtOwner.setString(3, "Αίτηση Parking");
                        insertStmtOwner.executeUpdate();*/



                       /* // Μείωση διαθέσιμων θέσεων στον πίνακα Parking κατά 1 για την επιλεγμένη ημερομηνία και ώρα
                        String reduceSpotsSql = "UPDATE Parking SET remaining_parking_spaces = remaining_parking_spaces - 1 WHERE name_parking = ? AND location_parking = ? AND reservation_date = ? AND reservation_time = ?";
                        PreparedStatement reduceSpotsStmt = con.prepareStatement(reduceSpotsSql);
                        reduceSpotsStmt.setString(1, parkingName);
                        reduceSpotsStmt.setString(2, getIntent().getStringExtra("location_parking"));
                        //reduceSpotsStmt.setString(3, selectedDate);
                        reduceSpotsStmt.setString(4, selectedHour);
                        reduceSpotsStmt.executeUpdate();*/

                        // Εμφάνιση μηνύματος επιτυχίας και μετάβαση στη σελίδα WalletProfile
                        runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BusinessInfo.this);
                            builder.setTitle("Επιτυχής Πληρωμή")
                                    .setMessage("Η κράτηση και η πληρωμή σας έγινε με επιτυχία.")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        dialog.dismiss();
                                        Intent intent = new Intent(BusinessInfo.this, WalletProfile.class);

                                        intent.putExtra("money_account", newBalance);
                                        intent.putExtra("username", userName);
                                        startActivity(intent);

                                    })
                                    .show();
                        });
                    } else {
                        // Εμφάνιση μηνύματος σφάλματος αν υπάρχει έλλειψη χρημάτων
                        runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BusinessInfo.this);
                            builder.setTitle("Μη επαρκές ποσό")
                                    .setMessage("Δεν έχετε αρκετά χρήματα στο ψηφιακό πορτοφόλι σας για αυτήν την ενοικίαση.")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        dialog.dismiss();
                                        Intent intent = new Intent(BusinessInfo.this, WalletProfile.class);
                                        intent.putExtra("username", userName);
                                        startActivity(intent);
                                    })
                                    .show();
                        });
                    }
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        } else if ("btnCarwash".equals(source)) {
            // Λογική για το btnCarwash

                selectedRegion = getIntent().getStringExtra("location_carWash");

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    try {
                        Connection con = ConnectionClass.CONN();
                        String sql = "SELECT * FROM WalletProfileUser WHERE username_wallet = ?";
                        PreparedStatement stmt = con.prepareStatement(sql);
                        stmt.setString(1, userName);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            moneyAccount = rs.getDouble("money_account");
                            double totalCost = Double.parseDouble(costPerHourCarWash);

                            if (moneyAccount >= totalCost) {
                                double newBalance = moneyAccount - totalCost;

                                // Προσθήκη της ενοικίασης ως συναλλαγή στο ιστορικό του χρήστη
                                String insertSql = "INSERT INTO HistoryTransaction (username_wallet, transaction_date, amount, description,name_business) VALUES (?, NOW(), ?, ?,?)";
                                PreparedStatement insertStmt = con.prepareStatement(insertSql);
                                insertStmt.setString(1, userName);
                                insertStmt.setDouble(2, totalCost);
                                insertStmt.setString(3, "Αίτηση CarWash");
                                insertStmt.setString(4, nameCarWash);

                                insertStmt.executeUpdate();



                                // Ενημέρωση του υπολοίπου του χρήστη
                                String updateSql = "UPDATE WalletProfileUser SET money_account = ? WHERE username_wallet = ?";
                                PreparedStatement updateStmt = con.prepareStatement(updateSql);
                                updateStmt.setDouble(1, newBalance);
                                updateStmt.setString(2, userName);
                                updateStmt.executeUpdate();





                                // Ανάκτηση επιλεγμένης ημερομηνίας και ώρας
                                String selectedDate = getIntent().getStringExtra("selected_date");
                                String selectedHour = getIntent().getStringExtra("selected_hour");



                                // Εισαγωγή νέας κράτησης στον πίνακα ParkingReservations
                                String CarWashname = getIntent().getStringExtra("name_carWash");
                                String reservationSql = "INSERT INTO CarWashReservations (username, name_carWash, reservation_date, reservation_time, cost,location_carWash,username_owner) VALUES (?, ?, ?, ?, ?, ?, ?)";
                                PreparedStatement reservationStmt = con.prepareStatement(reservationSql);
                                reservationStmt.setString(1, userName);
                                reservationStmt.setString(2, CarWashname);
                                reservationStmt.setString(3, selectedDate);
                                reservationStmt.setString(4, selectedHour);
                                reservationStmt.setDouble(5, totalCost);
                                reservationStmt.setString(6, selectedRegion);
                                reservationStmt.setString(7, userNameOwner);

                                reservationStmt.executeUpdate();

                        /*
                        //Ανάκτηση προφιλ του Owner
                        String sql2 = "SELECT * FROM WalletProfileUser WHERE username_wallet = ?";
                        PreparedStatement stmt2 = con.prepareStatement(sql2);
                        stmt2.setString(1, userNameOwner);
                        ResultSet rs2 = stmt2.executeQuery();
                        if (rs2.next()) {
                            moneyAccountOwner = rs2.getDouble("money_account");

                            //Προσθήκη χρημάτων στο πορτοφόλι του Owner
                            double newBalanceOwner = moneyAccountOwner + totalCost;
                            String updateSqlOwner = "UPDATE WalletProfileUser SET money_account = ? WHERE username_wallet = ?";
                            PreparedStatement stmtOwner = con.prepareStatement(updateSqlOwner);
                            stmtOwner.setDouble(1, newBalanceOwner);
                            stmtOwner.setString(2, userNameOwner);
                            stmtOwner.executeUpdate();
                        }
                        // Προσθήκη της ενοικίασης ως συναλλαγή στο ιστορικό Ιδιοκτητη
                        String insertSqlOwner = "INSERT INTO HistoryTransaction (username_wallet, transaction_date, amount, description) VALUES (?, NOW(), ?, ?)";
                        PreparedStatement insertStmtOwner = con.prepareStatement(insertSqlOwner);
                        insertStmtOwner.setString(1, userNameOwner);
                        insertStmtOwner.setDouble(2, totalCost);
                        insertStmtOwner.setString(3, "Αίτηση Parking");
                        insertStmtOwner.executeUpdate();*/



                       /* // Μείωση διαθέσιμων θέσεων στον πίνακα Parking κατά 1 για την επιλεγμένη ημερομηνία και ώρα
                        String reduceSpotsSql = "UPDATE Parking SET remaining_parking_spaces = remaining_parking_spaces - 1 WHERE name_parking = ? AND location_parking = ? AND reservation_date = ? AND reservation_time = ?";
                        PreparedStatement reduceSpotsStmt = con.prepareStatement(reduceSpotsSql);
                        reduceSpotsStmt.setString(1, parkingName);
                        reduceSpotsStmt.setString(2, getIntent().getStringExtra("location_parking"));
                        //reduceSpotsStmt.setString(3, selectedDate);
                        reduceSpotsStmt.setString(4, selectedHour);
                        reduceSpotsStmt.executeUpdate();*/

                                // Εμφάνιση μηνύματος επιτυχίας και μετάβαση στη σελίδα WalletProfile
                                runOnUiThread(() -> {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(BusinessInfo.this);
                                    builder.setTitle("Επιτυχής Πληρωμή")
                                            .setMessage("Η κράτηση και η πληρωμή σας έγινε με επιτυχία.")
                                            .setPositiveButton("OK", (dialog, which) -> {
                                                dialog.dismiss();
                                                Intent intent = new Intent(BusinessInfo.this, WalletProfile.class);

                                                intent.putExtra("money_account", newBalance);
                                                intent.putExtra("username", userName);
                                                startActivity(intent);

                                            })
                                            .show();
                                });
                            } else {
                                // Εμφάνιση μηνύματος σφάλματος αν υπάρχει έλλειψη χρημάτων
                                runOnUiThread(() -> {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(BusinessInfo.this);
                                    builder.setTitle("Μη επαρκές ποσό")
                                            .setMessage("Δεν έχετε αρκετά χρήματα στο ψηφιακό πορτοφόλι σας για αυτήν την ενοικίαση.")
                                            .setPositiveButton("OK", (dialog, which) -> {
                                                dialog.dismiss();
                                                Intent intent = new Intent(BusinessInfo.this, WalletProfile.class);
                                                intent.putExtra("username", userName);
                                                startActivity(intent);
                                            })
                                            .show();
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
}
