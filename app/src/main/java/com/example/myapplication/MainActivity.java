package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityMainBinding;

import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

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
            new LoginTask(MainActivity.this).execute(email, password);
        });

        tvSignUp.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SignUpActivity.class)));
    }

    private static class LoginTask extends AsyncTask<String, Void, Boolean> {
        private final MainActivity activity;

        LoginTask(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL("http://35.222.77.247:5000/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", params[0]);  // Changed "username" to "email"
                jsonParam.put("password", params[1]);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                conn.disconnect();
                return responseCode == 200;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(activity, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, DashboardActivity.class);
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
