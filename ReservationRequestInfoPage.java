package com.example.pedri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservationRequestInfoPage extends SortingReservationRequest {

    private TextView dateTextView, timeTextView, costTextView, userNameTextView, phoneNumberTextView;
    private String userNameOwner, userName, business, name_business, name_businessCarWash;
    private String date, time, cost, name, phoneNumber;
    private ArrayList<String[]> reservationDetailsList;

    double moneyAccount,moneyAccountOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_request_info_page);

        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        costTextView = findViewById(R.id.costTextView);
        userNameTextView = findViewById(R.id.userNameTextView);
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView);

        // Λήψη δεδομένων από το Intent
        reservationDetailsList = new ArrayList<>();
        userName = getIntent().getStringExtra("username");
        userNameOwner = getIntent().getStringExtra("username_owner");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        cost = getIntent().getStringExtra("cost");
        name = getIntent().getStringExtra("name");
        phoneNumber = getIntent().getStringExtra("phoneNumber");

        // Εμφάνιση δεδομένων
        dateTextView.setText("Ημερομηνία: " + date);
        timeTextView.setText("Ώρα: " + time);
        costTextView.setText("Κόστος: " + cost);
        userNameTextView.setText("Όνομα Χρήστη: " + name);
        phoneNumberTextView.setText("Τηλέφωνο: " + phoneNumber);
    }

    // Κουμπί Πίσω
    public void ButtonBack(View view) {
        Intent intent = new Intent(ReservationRequestInfoPage.this, SortingReservationRequest.class);
        intent.putExtra("username", userName);
        intent.putExtra("username_owner", userNameOwner);
        startActivity(intent);
    }

    // Κουμπί Αποδοχής
    public void ChooseAcceptance(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                String sqlOwner = "SELECT * FROM owner WHERE username_owner = ?";
                PreparedStatement stmtOwner1 = con.prepareStatement(sqlOwner);
                stmtOwner1.setString(1,userNameOwner );
                ResultSet rsOwner1 = stmtOwner1.executeQuery();
                //Ανάκτηση είδος επιχείρησης
                if (rsOwner1.next()) {
                    business = rsOwner1.getString("business");
                }

                if(business.equals("Parking")) {
                    try {

                        // Διαγραφή της εγγραφής από τον πίνακα ParkingReservations
                        String deleteSql = "DELETE FROM ParkingReservations WHERE reservation_date = ? AND reservation_time = ? AND username = ? AND username_owner = ?";
                        PreparedStatement deleteStmt = con.prepareStatement(deleteSql);
                        deleteStmt.setString(1, date);
                        deleteStmt.setString(2, time);
                        deleteStmt.setString(3, userName);
                        deleteStmt.setString(4, userNameOwner);
                        deleteStmt.executeUpdate();

                        String sql = "SELECT * FROM WalletProfileUser WHERE username_wallet = ?";
                        PreparedStatement stmt = con.prepareStatement(sql);
                        stmt.setString(1, userNameOwner);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            double moneyAccountOwner = rs.getDouble("money_account");
                            double totalCost = Double.parseDouble(cost);

                            String sql2 = "SELECT * FROM Parking WHERE username_owner = ?";
                            PreparedStatement stmt2 = con.prepareStatement(sql2);
                            stmt2.setString(1, userNameOwner);
                            ResultSet rs2 = stmt2.executeQuery();
                            if (rs2.next()) {
                                name_business = rs2.getString("name_parking");
                            }

                            // Προσθήκη της επιβεβαίωσης ενοικίασης Parking ως συναλλαγή στο ιστορικό του χρήστη
                            String insertSql = "INSERT INTO HistoryTransaction (username_wallet, transaction_date, description,name_business) VALUES (?, NOW(), ?, ?)";
                            PreparedStatement insertStmt = con.prepareStatement(insertSql);
                            insertStmt.setString(1, userName);
                            insertStmt.setString(2, "Επιβεβαίωση Parking");
                            insertStmt.setString(3, name_business);
                            insertStmt.executeUpdate();

                            // Προσθήκη χρημάτων στο πορτοφόλι του Owner
                            double newBalanceOwner = moneyAccountOwner + totalCost;
                            String updateSqlOwner = "UPDATE WalletProfileUser SET money_account = ? WHERE username_wallet = ?";
                            PreparedStatement stmtOwner = con.prepareStatement(updateSqlOwner);
                            stmtOwner.setDouble(1, newBalanceOwner);
                            stmtOwner.setString(2, userNameOwner);
                            stmtOwner.executeUpdate();

                            // Προσθήκη της ενοικίασης ως συναλλαγή στο ιστορικό Ιδιοκτήτη
                            String insertSqlOwner = "INSERT INTO HistoryTransaction (username_wallet, transaction_date, amount, description,name_business) VALUES (?, NOW(), ?, ?,?)";
                            PreparedStatement insertStmtOwner = con.prepareStatement(insertSqlOwner);
                            insertStmtOwner.setString(1, userNameOwner);
                            insertStmtOwner.setDouble(2, totalCost);
                            insertStmtOwner.setString(3, "Αίτηση Parking");
                            insertStmtOwner.setString(4, name_business);
                            insertStmtOwner.executeUpdate();



                            // Εμφάνιση μηνύματος επιτυχίας και μετάβαση στη σελίδα WalletProfile
                            runOnUiThread(() -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ReservationRequestInfoPage.this);
                                builder.setTitle("Επιτυχής Πληρωμή")
                                        .setMessage("Η αποδοχή κράτησης και πληρωμής σας έγινε με επιτυχία.")
                                        .
                                        setPositiveButton("OK", (dialog, which) -> {
                                            dialog.dismiss();
                                            Intent intent = new Intent(ReservationRequestInfoPage.this, WalletProfile.class);
                                            intent.putExtra("money_account", newBalanceOwner);
                                            intent.putExtra("username_owner", userNameOwner);

                                            // Διαγραφή από την λίστα
                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
                                            setResult(RESULT_OK, resultIntent);

                                            startActivity(intent);
                                        }).show();
                            });
                        }

                        con.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (business.equals("CarWash")) {
                    try {



                        String sql = "SELECT * FROM WalletProfileUser WHERE username_wallet = ?";
                        PreparedStatement stmt = con.prepareStatement(sql);
                        stmt.setString(1, userNameOwner);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            double moneyAccountOwner = rs.getDouble("money_account");
                            double totalCost = Double.parseDouble(cost);

                            String sql3 = "SELECT * FROM CarWash WHERE username_owner = ?";
                            PreparedStatement stmt3 = con.prepareStatement(sql3);
                            stmt3.setString(1, userNameOwner);
                            ResultSet rs3 = stmt3.executeQuery();
                            if (rs3.next()) {
                                name_businessCarWash = rs3.getString("name_carWash");
                            }

                            // Προσθήκη της επιβεβαίωσης ενοικίασης CarWash ως συναλλαγή στο ιστορικό του χρήστη
                            String insertSql = "INSERT INTO HistoryTransaction (username_wallet, transaction_date, description,name_business) VALUES (?, NOW(), ?, ?)";
                            PreparedStatement insertStmt = con.prepareStatement(insertSql);
                            insertStmt.setString(1, userName);
                            insertStmt.setString(2, "Επιβεβαίωση CarWash");
                            insertStmt.setString(3, name_businessCarWash);
                            insertStmt.executeUpdate();

                            // Προσθήκη χρημάτων στο πορτοφόλι του Owner
                            double newBalanceOwner = moneyAccountOwner + totalCost;
                            String updateSqlOwner = "UPDATE WalletProfileUser SET money_account = ? WHERE username_wallet = ?";
                            PreparedStatement stmtOwner = con.prepareStatement(updateSqlOwner);
                            stmtOwner.setDouble(1, newBalanceOwner);
                            stmtOwner.setString(2, userNameOwner);
                            stmtOwner.executeUpdate();

                            // Προσθήκη της ενοικίασης ως συναλλαγή στο ιστορικό Ιδιοκτήτη
                            String insertSqlOwner = "INSERT INTO HistoryTransaction (username_wallet, transaction_date, amount, description,name_business) VALUES (?, NOW(), ?, ?,?)";
                            PreparedStatement insertStmtOwner = con.prepareStatement(insertSqlOwner);
                            insertStmtOwner.setString(1, userNameOwner);
                            insertStmtOwner.setDouble(2, totalCost);
                            insertStmtOwner.setString(3, "Αίτηση CarWash");
                            insertStmtOwner.setString(4, name_businessCarWash);
                            insertStmtOwner.executeUpdate();

                            // Διαγραφή της εγγραφής από τον πίνακα CarWashReservations
                            String deleteSql = "DELETE FROM CarWashReservations WHERE reservation_date = ? AND reservation_time = ? AND username = ? AND username_owner = ?";
                            PreparedStatement deleteStmt = con.prepareStatement(deleteSql);
                            deleteStmt.setString(1, date);
                            deleteStmt.setString(2, time);
                            deleteStmt.setString(3, userName);
                            deleteStmt.setString(4, userNameOwner);
                            deleteStmt.executeUpdate();

                            // Εμφάνιση μηνύματος επιτυχίας και μετάβαση στη σελίδα WalletProfile
                            // Εμφάνιση μηνύματος επιτυχίας
                            runOnUiThread(() -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ReservationRequestInfoPage.this);
                                builder.setTitle("Επιτυχής Αποδοχή")
                                        .setMessage("Η αποδοχή της κράτησης CarWash ολοκληρώθηκε.")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            dialog.dismiss();
                                            // Ενημέρωση της λίστας με τις κρατήσεις
                                            //updateReservationList();
                                            // Διαγραφή από την λίστα
                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
                                            setResult(RESULT_OK, resultIntent);
                                            finish();
                                        }).show();
                            });
                        }

                        con.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //Κουμπί Απορριψης
    public void ChooseDecline(View view) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection con = ConnectionClass.CONN();
                String sqlOwner = "SELECT * FROM owner WHERE username_owner = ?";
                PreparedStatement stmtOwner1 = con.prepareStatement(sqlOwner);
                stmtOwner1.setString(1,userNameOwner );
                ResultSet rsOwner1 = stmtOwner1.executeQuery();
                //Ανάκτηση είδος επιχείρησης
                if (rsOwner1.next()) {
                    business = rsOwner1.getString("business");
                }


                if(business.equals("Parking")) {
                    //Λογικη για Ιδιοκτήτη Parking

                    try {



                        //Connection con = ConnectionClass.CONN();
                        String sql = "SELECT * FROM WalletProfileUser WHERE username_wallet = ?";
                        PreparedStatement stmt = con.prepareStatement(sql);
                        stmt.setString(1, userName);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            moneyAccount = rs.getDouble("money_account");
                            double totalCost = Double.parseDouble(cost);


                            String sql2 = "SELECT * FROM Parking WHERE username_owner = ?";
                            PreparedStatement stmt2 = con.prepareStatement(sql2);
                            stmt2.setString(1, userNameOwner);
                            ResultSet rs2 = stmt2.executeQuery();
                            if (rs2.next()) {
                                name_business = rs2.getString("name_parking");
                            }

                            // Προσθήκη της απόρριψης ενοικίασης  στο ιστορικό του χρήστη
                            String insertSql = "INSERT INTO HistoryTransaction (username_wallet, transaction_date, description,name_business) VALUES (?, NOW(), ?,?)";
                            PreparedStatement insertStmt = con.prepareStatement(insertSql);
                            insertStmt.setString(1, userName);
                            //insertStmt.setDouble(2, totalCost);
                            insertStmt.setString(2, "Απορριψη ενοικίασης Parking");
                            insertStmt.setString(3, name_business);
                            insertStmt.executeUpdate();


                            // Προσθήκη χρημάτων στο πορτοφόλι του Owner
                            double newBalance = moneyAccount + totalCost;
                            String updateSql = "UPDATE WalletProfileUser SET money_account = ? WHERE username_wallet = ?";
                            PreparedStatement stmtUser = con.prepareStatement(updateSql);
                            stmtUser.setDouble(1, newBalance);
                            stmtUser.setString(2, userName);
                            stmtUser.executeUpdate();

                            /*// Προσθήκη της ενοικίασης ως συναλλαγή στο ιστορικό Ιδιοκτητη
                            String insertSqlOwner = "INSERT INTO HistoryTransaction (username_wallet, transaction_date, description) VALUES (?, NOW(), ?)";
                            PreparedStatement insertStmtOwner = con.prepareStatement(insertSqlOwner);
                            insertStmtOwner.setString(1, userNameOwner);
                            //insertStmtOwner.setDouble(2, totalCost);
                            insertStmtOwner.setString(2, "Απόρριψη αίτησης Parking");
                            insertStmtOwner.executeUpdate();*/

                            // Διαγραφή της εγγραφής από τον πίνακα ParkingReservations
                            String deleteSql = "DELETE FROM ParkingReservations WHERE reservation_date = ? AND reservation_time = ? AND username = ? AND username_owner = ?";
                            PreparedStatement deleteStmt = con.prepareStatement(deleteSql);
                            deleteStmt.setString(1, date);
                            deleteStmt.setString(2, time);
                            deleteStmt.setString(3, userName);
                            deleteStmt.setString(4, userNameOwner);
                            deleteStmt.executeUpdate();

                            // Εμφάνιση μηνύματος επιτυχίας και μετάβαση στη σελίδα WalletProfile
                            runOnUiThread(() -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ReservationRequestInfoPage.this);
                                builder.setTitle("Επιτυχής Απόρριψη")
                                        .setMessage("Η απόρριψη κράτησης και πληρωμής  έγινε με επιτυχία.")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            dialog.dismiss();
                                            Intent intent = new Intent(ReservationRequestInfoPage.this, WalletProfile.class);
                                            intent.putExtra("money_account", newBalance);
                                            intent.putExtra("username_owner", userNameOwner);

                                            // Διαγραφή από την λίστα
                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
                                            setResult(RESULT_OK, resultIntent);

                                            startActivity(intent);
                                        })
                                        .show();
                            });
                        }
                        con.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (business.equals("CarWash")) {
                    //Λογικη για Ιδιοκτητη CarWash

                    try {



                        //Connection con = ConnectionClass.CONN();
                        String sql = "SELECT * FROM WalletProfileUser WHERE username_wallet = ?";
                        PreparedStatement stmt = con.prepareStatement(sql);
                        stmt.setString(1, userName);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            moneyAccount = rs.getDouble("money_account");
                            double totalCost = Double.parseDouble(cost);


                            String sql2 = "SELECT * FROM CarWash WHERE username_owner = ?";
                            PreparedStatement stmt2 = con.prepareStatement(sql2);
                            stmt2.setString(1, userNameOwner);
                            ResultSet rs2 = stmt2.executeQuery();
                            if (rs2.next()) {
                                name_business = rs2.getString("name_carWash");
                            }

                            // Προσθήκη της απόρριψης ενοικίασης  στο ιστορικό του χρήστη
                            String insertSql = "INSERT INTO HistoryTransaction (username_wallet, transaction_date, description,name_business) VALUES (?, NOW(), ?,?)";
                            PreparedStatement insertStmt = con.prepareStatement(insertSql);
                            insertStmt.setString(1, userName);
                            //insertStmt.setDouble(2, totalCost);
                            insertStmt.setString(2, "Απορριψη ενοικίασης CarWash");
                            insertStmt.setString(3, name_business);
                            insertStmt.executeUpdate();


                            // Προσθήκη χρημάτων στο πορτοφόλι του Owner
                            double newBalance = moneyAccount + totalCost;
                            String updateSql = "UPDATE WalletProfileUser SET money_account = ? WHERE username_wallet = ?";
                            PreparedStatement stmtUser = con.prepareStatement(updateSql);
                            stmtUser.setDouble(1, newBalance);
                            stmtUser.setString(2, userName);
                            stmtUser.executeUpdate();

                            /*// Προσθήκη της ενοικίασης ως συναλλαγή στο ιστορικό Ιδιοκτητη
                            String insertSqlOwner = "INSERT INTO HistoryTransaction (username_wallet, transaction_date, description) VALUES (?, NOW(), ?)";
                            PreparedStatement insertStmtOwner = con.prepareStatement(insertSqlOwner);
                            insertStmtOwner.setString(1, userNameOwner);
                            //insertStmtOwner.setDouble(2, totalCost);
                            insertStmtOwner.setString(2, "Απόρριψη αίτησης CarWash");
                            insertStmtOwner.executeUpdate();*/

                            // Διαγραφή της εγγραφής από τον πίνακα CarWashReservations
                            String deleteSql = "DELETE FROM CarWashReservations WHERE reservation_date = ? AND reservation_time = ? AND username = ? AND username_owner = ?";
                            PreparedStatement deleteStmt = con.prepareStatement(deleteSql);
                            deleteStmt.setString(1, date);
                            deleteStmt.setString(2, time);
                            deleteStmt.setString(3, userName);
                            deleteStmt.setString(4, userNameOwner);
                            deleteStmt.executeUpdate();

                            // Εμφάνιση μηνύματος επιτυχίας και μετάβαση στη σελίδα WalletProfile
                            runOnUiThread(() -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ReservationRequestInfoPage.this);
                                builder.setTitle("Επιτυχής Απόρριψη")
                                        .setMessage("Η απόρριψη κράτησης και πληρωμής  έγινε με επιτυχία.")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            dialog.dismiss();
                                            Intent intent = new Intent(ReservationRequestInfoPage.this, WalletProfile.class);
                                            intent.putExtra("money_account", newBalance);
                                            intent.putExtra("username_owner", userNameOwner);

                                            // Διαγραφή από την λίστα
                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
                                            setResult(RESULT_OK, resultIntent);

                                            startActivity(intent);
                                        })
                                        .show();
                            });
                        }
                        con.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }
}
