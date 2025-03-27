package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize the views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        // Set click listener for the sign up button
        btnSignUp.setOnClickListener(v -> {
            String username = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            // Execute the sign-up task
            new SignUpTask(SignUpActivity.this).execute(username, password);
        });
    }

    // AsyncTask to handle the sign-up process in the background
    private static class SignUpTask extends AsyncTask<String, Void, Boolean> {
        private final Context context;

        // Constructor to pass the context to the AsyncTask
        public SignUpTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // Set up the HTTP connection
                URL url = new URL("http://35.222.77.247:5000/signup");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Create the JSON object for the parameters
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", params[0]);
                jsonParam.put("password", params[1]);

                // Write the parameters to the output stream
                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes());
                os.flush();
                os.close();

                // Get the response code
                int responseCode = conn.getResponseCode();
                conn.disconnect();
                return responseCode == 201; // Check if the response is 201 (Created)
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            // Show a toast based on whether the sign-up was successful or not
            if (success) {
                Toast.makeText(context, "Account created successfully", Toast.LENGTH_SHORT).show();
                // Navigate to the login screen (MainActivity)
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
