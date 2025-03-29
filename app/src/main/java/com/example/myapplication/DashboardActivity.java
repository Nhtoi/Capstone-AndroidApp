package com.example.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityDashboardBinding;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private TextView tvCurrentModel;
    private Button btnViewCallHistory;
    private Button btnViewBlackListWhiteList;
    private AIModelCarousel aiModelCarousel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvCurrentModel = new TextView(this);
        tvCurrentModel.setTextSize(18);
        tvCurrentModel.setPadding(16, 16, 16, 16);

        btnViewCallHistory = findViewById(R.id.btnViewCallHistory);
        aiModelCarousel = findViewById(R.id.aiModelCarousel);
        btnViewBlackListWhiteList = findViewById(R.id.btnViewBlackListWhiteList);

        // Get email from intent
        String email = getIntent().getStringExtra("email");
        tvWelcome.setText("Welcome, " + (email != null ? email : "User") + "!");

        // Fetch and display current AI model
        fetchCurrentAIModel();
        fetchAIModels();

        // View Call History button listener
        btnViewCallHistory.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, CallHistoryActivity.class)));
        btnViewBlackListWhiteList.setOnClickListener(v-> startActivity(new Intent(DashboardActivity.this, WhiteListBlackList.class)));
    }

    private void replaceActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void fetchCurrentAIModel() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL("http://35.222.77.247:5000/currentmodel");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String modelName = jsonObject.getString("model");
                        tvCurrentModel.setText("Current AI Model: " + modelName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    tvCurrentModel.setText("Failed to fetch AI Model");
                }
            }
        }.execute();
    }

    private void fetchAIModels() {
        new AsyncTask<Void, Void, List<AIModel>>() {
            @Override
            protected List<AIModel> doInBackground(Void... voids) {
                List<AIModel> models = new ArrayList<>();
                try {
                    URL url = new URL("http://35.222.77.247:5000/aimodels");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        models.add(new AIModel(jsonObject.getString("name"), jsonObject.getString("description"), R.drawable.model_gpt4));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return models;
            }

            @Override
            protected void onPostExecute(List<AIModel> models) {
                if (!models.isEmpty()) {
                    aiModelCarousel.setModels(models);
                } else {
                    Toast.makeText(DashboardActivity.this, "Failed to load AI Models", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
