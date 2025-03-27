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
    private RecyclerView rvWhitelist, rvBlacklist;
    private NumberAdapter whitelistAdapter, blacklistAdapter;
    private List<String> whitelistNumbers = new ArrayList<>();
    private List<String> blacklistNumbers = new ArrayList<>();
    private AIModelCarousel aiModelCarousel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        etNumber = findViewById(R.id.etNumber);
        btnAddWhitelist = findViewById(R.id.btnAddWhitelist);
        btnAddBlacklist = findViewById(R.id.btnAddBlacklist);
        btnViewCallHistory = findViewById(R.id.btnViewCallHistory);
        rvWhitelist = findViewById(R.id.rvWhitelist);
        rvBlacklist = findViewById(R.id.rvBlacklist);
        aiModelCarousel = findViewById(R.id.aiModelCarousel);

        // Get email from intent
        String email = getIntent().getStringExtra("email");
        tvWelcome.setText("Welcome, " + (email != null ? email : "User") + "!");

        // Set up RecyclerViews
        rvWhitelist.setLayoutManager(new LinearLayoutManager(this));
        rvBlacklist.setLayoutManager(new LinearLayoutManager(this));

        whitelistAdapter = new NumberAdapter(whitelistNumbers);
        blacklistAdapter = new NumberAdapter(blacklistNumbers);

        rvWhitelist.setAdapter(whitelistAdapter);
        rvBlacklist.setAdapter(blacklistAdapter);

        // Button listeners
        btnAddWhitelist.setOnClickListener(v -> addNumberToList(whitelistNumbers, whitelistAdapter, "Whitelist"));
        btnAddBlacklist.setOnClickListener(v -> addNumberToList(blacklistNumbers, blacklistAdapter, "Blacklist"));

        // View Call History button listener
        btnViewCallHistory.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, CallHistoryActivity.class)));

        // Set up AI models
        setupAIModels();
    }

    private void addNumberToList(List<String> list, NumberAdapter adapter, String listName) {
        String number = etNumber.getText().toString().trim();
        if (!number.isEmpty() && !list.contains(number)) {
            list.add(number);
            adapter.notifyDataSetChanged();
            etNumber.setText("");
            Toast.makeText(this, "Number added to " + listName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Invalid or duplicate entry", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAIModels() {
        List<AIModel> models = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            models.add(new AIModel("Placeholder", "Placeholder Description", R.drawable.model_gpt4));
        }

        aiModelCarousel.setModels(models);
        aiModelCarousel.setOnModelSelectedListener(model ->
                Toast.makeText(DashboardActivity.this, "Selected: " + model.getName(), Toast.LENGTH_SHORT).show()
        );
    }
}
