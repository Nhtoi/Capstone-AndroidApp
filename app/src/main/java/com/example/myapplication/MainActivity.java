package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    public static String USERID;

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

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
                new LoginTask(MainActivity.this).execute(email, password);
            }
        });

        tvSignUp.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SignUpActivity.class)));
    }

    private static class LoginTask extends AsyncTask<String, Void, Boolean> {
        private final MainActivity activity;
        private String token = null;

        LoginTask(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL("https://scam-scam-service-185231488037.us-central1.run.app/api/v1/auth/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", params[0]);
                jsonParam.put("password", params[1]);

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
                    token = responseJson.optString("token");

                    Log.d("LoginTask", "Token from response: " + token);
                    return true;
                }

                conn.disconnect();
                return false;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success && token != null) {
                try {
                    String[] parts = token.split("\\.");
                    if (parts.length == 3) {
                        String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE));
                        JSONObject payload = new JSONObject(payloadJson);
                        String userId = payload.optString("id");
                        Log.d("LoginTask", "Extracted user ID from token payload: " + userId);

                        MainActivity.USERID = userId;

                        Toast.makeText(activity, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, DashboardActivity.class);
                        intent.putExtra("USER_ID", userId);
                        activity.startActivity(intent);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("LoginTask", "Failed to parse token payload");
                }
            }

            Toast.makeText(activity, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
