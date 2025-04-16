package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {
    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fullName.isEmpty() || !fullName.contains(" ")) {
                Toast.makeText(SignUpActivity.this, "Enter full name (first and last)", Toast.LENGTH_SHORT).show();
                return;
            }

            String firstName = fullName.substring(0, fullName.indexOf(" "));
            String lastName = fullName.substring(fullName.indexOf(" ") + 1);

            new SignUpTask(SignUpActivity.this).execute(email, password, confirmPassword, firstName, lastName);
        });
    }

    private static class SignUpTask extends AsyncTask<String, Void, Boolean> {
        private final Context context;

        public SignUpTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL("https://scam-scam-service-185231488037.us-central1.run.app/api/v1/auth/signup");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", params[0]);
                jsonParam.put("password", params[1]);
                jsonParam.put("confirmPassword", params[2]);
                jsonParam.put("firstName", params[3]);
                jsonParam.put("lastName", params[4]);
                jsonParam.put("userType", "1");

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();

                InputStream is = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream(); // read the error response

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Log.d("SignUpResponse", "Code: " + responseCode + ", Body: " + response.toString());
                conn.disconnect();
                return responseCode == 201 || responseCode == 200;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(context, "Account created successfully", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, MainActivity.class));
            } else {
                Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
