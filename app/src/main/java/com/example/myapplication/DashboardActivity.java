package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private TextView tvCurrentModel;
    private Button btnViewCallHistory;
    private Button btnViewBlackListWhiteList;
    private AIModelCarousel aiModelCarousel;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvCurrentModel = findViewById(R.id.tvCurrentModel); // ✅ Added this line
        btnViewCallHistory = findViewById(R.id.btnViewCallHistory);
        btnViewBlackListWhiteList = findViewById(R.id.btnViewBlackListWhiteList);
        aiModelCarousel = findViewById(R.id.aiModelCarousel);

        // Fetch and display current AI model
        fetchCurrentAIModel();
        fetchAIModels();

        // Button listeners
        btnViewCallHistory.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, CallHistoryActivity.class))
        );
        btnViewBlackListWhiteList.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, WhiteListBlackList.class))
        );
    }

    private void fetchCurrentAIModel() {
        executor.execute(() -> {
            try {
                String uid = "2"; // Hardcoded for now
                URL url = new URL("https://scam-scam-service-185231488037.us-central1.run.app/api/v1/app/pull-pref");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonInputString = "{\"ownedBy\": \"" + uid + "\"}";
                OutputStream os = conn.getOutputStream();
                os.write(jsonInputString.getBytes("UTF-8"));
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Log.d("Dashboard", "AI Models JSON: " + response.toString());

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject resultObject = jsonObject.getJSONObject("result");
                String modelName = resultObject.optString("voice", "No voice preference set");

                runOnUiThread(() -> tvCurrentModel.setText("Current AI Model: " + modelName));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> tvCurrentModel.setText("Failed to fetch AI preferences"));
            }
        });
    }

    private void fetchAIModels() {
        executor.execute(() -> {
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
                    models.add(new AIModel(
                            jsonObject.getString("name"),
                            jsonObject.getString("description"),
                            R.drawable.model_gpt4
                    ));
                }

                runOnUiThread(() -> {
                    if (!models.isEmpty()) {
                        aiModelCarousel.setModels(models);
                    } else {
                        Toast.makeText(DashboardActivity.this, "Failed to load AI Models", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(DashboardActivity.this, "Failed to load AI Models", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
