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
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
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

    private Button btnLogout;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String userId;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        userId = getIntent().getStringExtra("USER_ID");
        btnLogout = findViewById(R.id.btnLogout);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvCurrentModel = findViewById(R.id.tvCurrentModel);
        btnViewCallHistory = findViewById(R.id.btnViewCallHistory);
        btnViewBlackListWhiteList = findViewById(R.id.btnViewBlackListWhiteList);
        aiModelCarousel = findViewById(R.id.aiModelCarousel);
        aiModelCarousel.setOnModelSelectedListener(selectedModel -> {
            setAI(selectedModel.getName());
        });

        fetchCurrentAIModel();
        fetchAIModels();

        btnViewCallHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CallHistoryActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        btnViewBlackListWhiteList.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, WhiteListBlackList.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            File file = new File(getFilesDir(), "userid.txt");
            if (file.exists()) file.delete();

            // Clear SharedPreferences
            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();

            // Redirect to MainActivity
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear back stack
            startActivity(intent);
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        });

    }


    private void fetchCurrentAIModel() {
        executor.execute(() -> {
            try {
                URL url = new URL("https://scam-scam-service-185231488037.us-central1.run.app/api/v1/app/pull-pref");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonInputString = "{\"ownedBy\": \"" + userId + "\"}";
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
            String basePersonaUrl = "https://aivoice-chatbox-185231488037.us-central1.run.app/incoming-call?persona=";

            String[][] personas = {
                    {"genZ", "Energetic Gen Z persona"},
                    {"texanDude", "Southern drawl, Texan vibes"},
                    {"shaggy", "Zoinks! It's Shaggy from Scooby-Doo"},
                    {"jackSparrow", "Savvy? It’s the Captain himself!"}
            };

            for (String[] persona : personas) {
                String personaKey = persona[0];
                String description = persona[1];
                String fullUrl = basePersonaUrl + personaKey;

                try {
                    URL personaUrl = new URL(fullUrl);
                    HttpURLConnection pConn = (HttpURLConnection) personaUrl.openConnection();
                    pConn.setRequestMethod("GET");

                    int responseCode = pConn.getResponseCode();

                    if (responseCode == 200) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(pConn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        models.add(new AIModel(personaKey, description, R.drawable.model_gpt4));
                    }

                    pConn.disconnect();
                } catch (Exception e) {
                    Log.e("PersonaFetch", "Error loading persona " + personaKey, e);
                }
            }

            runOnUiThread(() -> {
                if (!models.isEmpty()) {
                    aiModelCarousel.setModels(models);
                } else {
                    Toast.makeText(DashboardActivity.this, "Failed to load AI Models", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setAI(String personaName) {
        executor.execute(() -> {
            try {
                URL url = new URL("https://scam-scam-service-185231488037.us-central1.run.app/api/v1/users/set-preferences");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonInputString = String.format(
                        "{\"ownedBy\": \"%s\", \"voice\": \"%s\", \"prompt\": \"\"}",
                        userId, personaName
                );

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

                runOnUiThread(() -> {
                    Toast.makeText(this, "AI model updated to: " + personaName, Toast.LENGTH_SHORT).show();
                    tvCurrentModel.setText("Current AI Model: " + personaName);
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Failed to update AI model", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
