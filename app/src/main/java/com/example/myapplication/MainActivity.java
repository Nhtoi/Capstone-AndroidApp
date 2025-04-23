package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static String USERID;

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        File file = new File(getFilesDir(), "userid.txt");
        if (file.exists()) {
            try {
                Scanner scanner = new Scanner(file).useDelimiter("\\A");
                String savedUserId = scanner.hasNext() ? scanner.next().trim() : null;
                scanner.close();

                if (savedUserId != null && !savedUserId.isEmpty()) {
                    USERID = savedUserId;
                    SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    prefs.edit().putString("USER_ID", USERID).apply();

                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    intent.putExtra("USER_ID", USERID);
                    startActivity(intent);
                    finish();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.equals("demo") && password.equals("demo")) {
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                intent.putExtra("USER_ID", "6");
                startActivity(intent);
            } else {
                loginUser(email, password);
            }
        });

        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        });
    }

    private void loginUser(String email, String password) {
        executor.execute(() -> {
            try {
                URL url = new URL("https://scam-scam-service-185231488037.us-central1.run.app/api/v1/auth/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", email);
                jsonParam.put("password", password);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream is = conn.getInputStream();
                    Scanner scanner = new Scanner(is).useDelimiter("\\A");
                    String response = scanner.hasNext() ? scanner.next() : "";
                    scanner.close();

                    JSONObject responseJson = new JSONObject(response);
                    String token = responseJson.optString("token");

                    Log.d("LoginExecutor", "Token: " + token);

                    if (token != null && !token.isEmpty()) {
                        String[] parts = token.split("\\.");
                        if (parts.length == 3) {
                            String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE));
                            JSONObject payload = new JSONObject(payloadJson);
                            String userId = payload.optString("id");

                            Log.d("LoginExecutor", "User ID: " + userId);
                            USERID = userId;

                            SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                            prefs.edit().putString("USER_ID", userId).apply();

                            File file = new File(getFilesDir(), "userid.txt");
                            FileWriter writer = new FileWriter(file);
                            writer.write(userId);
                            writer.close();

                            mainHandler.post(() -> {
                                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                intent.putExtra("USER_ID", userId);
                                startActivity(intent);
                                finish();
                            });
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("LoginExecutor", "Error: " + e.getMessage());
            }

            mainHandler.post(() -> Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show());
        });
    }
}
