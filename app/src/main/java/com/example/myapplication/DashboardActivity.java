package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private EditText etNumber;
    private Button btnAddWhitelist, btnAddBlacklist, btnViewCallHistory;
    private RecyclerView rvWhitelist, rvBlacklist, rvCallHistory;
    private NumberAdapter whitelistAdapter, blacklistAdapter, callHistoryAdapter;
    private List<String> whitelistNumbers = new ArrayList<>();
    private List<String> blacklistNumbers = new ArrayList<>();
    private List<String> callHistoryNumbers = new ArrayList<>();
    private AIModelCarousel aiModelCarousel;

    // Database repository
    private DatabaseRepository dbRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize database repository
        dbRepository = new DatabaseRepository();

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        etNumber = findViewById(R.id.etNumber);
        btnAddWhitelist = findViewById(R.id.btnAddWhitelist);
        btnAddBlacklist = findViewById(R.id.btnAddBlacklist);
        btnViewCallHistory = findViewById(R.id.btnViewCallHistory);
        rvWhitelist = findViewById(R.id.rvWhitelist);
        rvBlacklist = findViewById(R.id.rvBlacklist);
        rvCallHistory = findViewById(R.id.rvCallHistory);
        aiModelCarousel = findViewById(R.id.aiModelCarousel);

        // Get username from intent
        String username = getIntent().getStringExtra("username");
        DatabaseHelper.User user = dbRepository.getUserByUsername(username);
        if (user != null) {
            tvWelcome.setText("Welcome, " + user.getDisplayName() + "!");
        } else {
            tvWelcome.setText("Welcome, " + username + "!");
        }

        // Set up AI model carousel
        setupAIModels();

        // Set up RecyclerViews
        rvWhitelist.setLayoutManager(new LinearLayoutManager(this));
        rvBlacklist.setLayoutManager(new LinearLayoutManager(this));
        rvCallHistory.setLayoutManager(new LinearLayoutManager(this));

        // Load data from database
        whitelistNumbers.addAll(dbRepository.getWhitelistNumbers());
        blacklistNumbers.addAll(dbRepository.getBlacklistNumbers());

        // Extract phone numbers from call history
        List<DatabaseHelper.CallRecord> callRecords = dbRepository.getCallHistory();
        for (DatabaseHelper.CallRecord record : callRecords) {
            callHistoryNumbers.add(record.getPhoneNumber());
        }

        whitelistAdapter = new NumberAdapter(whitelistNumbers);
        blacklistAdapter = new NumberAdapter(blacklistNumbers);
        callHistoryAdapter = new NumberAdapter(callHistoryNumbers);

        rvWhitelist.setAdapter(whitelistAdapter);
        rvBlacklist.setAdapter(blacklistAdapter);
        rvCallHistory.setAdapter(callHistoryAdapter);

        // Button listeners
        btnAddWhitelist.setOnClickListener(v -> {
            String number = etNumber.getText().toString().trim();
            if (!number.isEmpty() && !whitelistNumbers.contains(number)) {
                if (dbRepository.addToWhitelist(number)) {
                    whitelistNumbers.add(number);
                    whitelistAdapter.notifyDataSetChanged();
                    etNumber.setText("");
                    Toast.makeText(DashboardActivity.this, "Number added to whitelist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DashboardActivity.this, "Invalid or duplicate entry", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddBlacklist.setOnClickListener(v -> {
            String number = etNumber.getText().toString().trim();
            if (!number.isEmpty() && !blacklistNumbers.contains(number)) {
                if (dbRepository.addToBlacklist(number)) {
                    blacklistNumbers.add(number);
                    blacklistAdapter.notifyDataSetChanged();
                    etNumber.setText("");
                    Toast.makeText(DashboardActivity.this, "Number added to blacklist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DashboardActivity.this, "Invalid or duplicate entry", Toast.LENGTH_SHORT).show();
            }
        });

        btnViewCallHistory = findViewById(R.id.btnViewCallHistory);
        btnViewCallHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CallHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void setupAIModels() {
        // Hardcoded AI models for demonstration
        List<AIModel> models = new ArrayList<>();
        models.add(new AIModel("Place Holder", "Place Holder", R.drawable.model_gpt4));
        models.add(new AIModel("Place Holder", "Place Holder", R.drawable.model_gpt4));
        models.add(new AIModel("Place Holder", "Place Holder", R.drawable.model_gpt4));
        models.add(new AIModel("Place Holder", "Place Holder", R.drawable.model_gpt4));
        models.add(new AIModel("Place Holder", "Place Holder", R.drawable.model_gpt4));

        aiModelCarousel.setModels(models);
        aiModelCarousel.setOnModelSelectedListener(model ->
                Toast.makeText(DashboardActivity.this, "Selected: " + model.getName(), Toast.LENGTH_SHORT).show()
        );
    }
}