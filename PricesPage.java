package com.example.pedri;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PricesPage extends CentralMenuOwner {

    private String userNameOwner,location;
    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private PricesAdapter pricesAdapter;
    private List<Price> allPrices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prices_page);

        userNameOwner = getIntent().getStringExtra("username_owner");

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        recyclerView = findViewById(R.id.pricesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allPrices = new ArrayList<>();
        pricesAdapter = new PricesAdapter(allPrices);
        recyclerView.setAdapter(pricesAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString().trim();
                searchPrices(query);
            }
        });

        // Fetch prices from database
        new FetchPricesTask().execute();
    }

    private void searchPrices(String query) {
        // Remove leading and trailing spaces and replace all spaces with no space
        query = query.trim().replaceAll("\\s+", "");
        List<Price> filteredPrices = new ArrayList<>();
        for (Price price : allPrices) {
            // Remove leading and trailing spaces of product name and replace all spaces with no space
            String productName = price.getProductName().trim().replaceAll("\\s+", "");
            // Check if product name contains the search query
            if (productName.toLowerCase().contains(query.toLowerCase())) {
                filteredPrices.add(price);
            }
        }
        pricesAdapter.setPrices(filteredPrices);
    }

    public void backButton(View view) {
        Intent intent = new Intent(PricesPage.this, CentralMenuOwner.class);
        intent.putExtra("username_owner", userNameOwner);
        startActivity(intent);

    }

    private class FetchPricesTask extends AsyncTask<Void, Void, List<Price>> {
        @Override
        protected List<Price> doInBackground(Void... voids) {
            List<Price> pricesList = new ArrayList<>();
            try {
                Connection con = ConnectionClass.CONN(); // Use your connection class here
                if (con != null) {
                    String query = "SELECT service_name, current_price,area FROM service_price WHERE username_owner=?";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setString(1, userNameOwner);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        String productName = rs.getString("service_name");
                        String price = rs.getString("current_price"); // Get price as String
                        location = rs.getString("area");
                        pricesList.add(new Price(productName, price));
                    }
                    rs.close();
                    stmt.close();
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return pricesList;
        }

        @Override
        protected void onPostExecute(List<Price> prices) {
            if (prices != null && !prices.isEmpty()) {
                allPrices.clear();
                allPrices.addAll(prices);
                pricesAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(PricesPage.this, "No prices found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class Price {
        private String productName;
        private String price; // Changed to String

        public Price(String productName, String price) {
            this.productName = productName;
            this.price = price;
        }

        public String getProductName() {
            return productName;
        }

        public String getPrice() {
            return price;
        }
    }

    private class PricesAdapter extends RecyclerView.Adapter<PricesAdapter.PricesViewHolder> {
        private List<Price> prices;

        public PricesAdapter(List<Price> prices) {
            this.prices = prices;
        }

        @NonNull
        @Override
        public PricesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_price, parent, false);
            return new PricesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PricesViewHolder holder, int position) {
            Price price = prices.get(position);
            holder.productNameTextView.setText(price.getProductName());
            double priceValue = Double.parseDouble(price.getPrice()); // Parse String price to double
            holder.priceTextView.setText(String.valueOf(priceValue));
        }

        private void ShowNewPricePage(String selectedServiceName, String serv_price) {
            Intent intent = new Intent(PricesPage.this, PriceUpdatePage.class);
            intent.putExtra("service_name", selectedServiceName);
            intent.putExtra("current_price", serv_price);
            intent.putExtra("username_owner", userNameOwner);
            intent.putExtra("area",location);
            Log.d("PricesAdapter", "Starting PriceUpdatePage with service: " + selectedServiceName + ", price: " + serv_price);
            startActivity(intent);
        }

        @Override
        public int getItemCount() {
            return prices.size();
        }

        public void setPrices(List<Price> prices) {
            this.prices = prices;
            notifyDataSetChanged();
        }

        public class PricesViewHolder extends RecyclerView.ViewHolder {
            private TextView productNameTextView;
            private TextView priceTextView;

            public PricesViewHolder(@NonNull View itemView) {
                super(itemView);
                productNameTextView = itemView.findViewById(R.id.productNameTextView);
                priceTextView = itemView.findViewById(R.id.priceTextView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Price selectedPrice = prices.get(position);
                            Log.d("PricesViewHolder", "Item clicked at position: " + position);
                            ShowNewPricePage(selectedPrice.getProductName(), selectedPrice.getPrice());
                        }
                    }
                });
            }
        }
    }
}
