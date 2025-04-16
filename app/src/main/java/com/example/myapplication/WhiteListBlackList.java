package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WhiteListBlackList extends AppCompatActivity {

    private static final String TAG = "WhiteListActivity";
    private static final String BASE_URL = "https://scam-scam-service-185231488037.us-central1.run.app";
    private static final String FETCH_URL = BASE_URL + "/api/v1/app/pull-white";
    private static final String POST_URL = BASE_URL + "/api/v1/users/set-white";
    private static final String OWNED_BY = "6";

    private EditText etNumber;
    private Button btnAdd, btnSort;
    private RecyclerView rvNumbers;
    private NumberAdapter numberAdapter;
    private final List<NumberItem> numberList = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whitelist_blacklist);

        etNumber = findViewById(R.id.etNumber);
        btnAdd = findViewById(R.id.btnAdd);
        btnSort = findViewById(R.id.btnSort);
        rvNumbers = findViewById(R.id.rvNumbers);

        rvNumbers.setLayoutManager(new LinearLayoutManager(this));
        numberAdapter = new NumberAdapter(numberList);
        rvNumbers.setAdapter(numberAdapter);

        btnAdd.setOnClickListener(v -> addNumber());
        btnSort.setOnClickListener(v -> sortNumbers());

        fetchWhitelist();
    }

    private void fetchWhitelist() {
        executor.execute(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("ownedBy", OWNED_BY);

                HttpURLConnection conn = (HttpURLConnection) new URL(FETCH_URL).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = new BufferedOutputStream(conn.getOutputStream())) {
                    os.write(body.toString().getBytes());
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Fetch response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }

                    JSONObject responseJson = new JSONObject(responseBuilder.toString());
                    JSONArray result = responseJson.getJSONArray("result");

                    numberList.clear();
                    for (int i = 0; i < result.length(); i++) {
                        String phone = result.getJSONObject(i).getString("phoneNumber");
                        numberList.add(new NumberItem(phone, "Whitelist"));
                    }

                    mainHandler.post(() -> numberAdapter.notifyDataSetChanged());

                } else {
                    Log.e(TAG, "Fetch failed, response code: " + responseCode);
                }

                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Fetch whitelist error", e);
            }
        });
    }

    private void addNumber() {
        String number = etNumber.getText().toString().trim();
        if (number.isEmpty()) {
            Toast.makeText(this, "Number can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isNumberInList(number)) {
            Toast.makeText(this, "Number already in list", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("ownedBy", OWNED_BY);
                body.put("phoneNumber", number);

                HttpURLConnection conn = (HttpURLConnection) new URL(POST_URL).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = new BufferedOutputStream(conn.getOutputStream())) {
                    os.write(body.toString().getBytes());
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Add response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    mainHandler.post(() -> {
                        Toast.makeText(this, "Number added", Toast.LENGTH_SHORT).show();
                        finish(); // finish current instance
                        startActivity(getIntent()); // restart it
                    });
                    fetchWhitelist(); // 🔄 Refresh list from server
                } else {
                    Log.e(TAG, "Add failed, response code: " + responseCode);
                    mainHandler.post(() -> Toast.makeText(this, "Failed to add number", Toast.LENGTH_SHORT).show());
                }


                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Add number error", e);
                mainHandler.post(() -> Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show());
            }
        });
    }


    private boolean isNumberInList(String number) {
        for (NumberItem item : numberList) {
            if (item.getNumber().equals(number)) return true;
        }
        return false;
    }

    private void sortNumbers() {
        Collections.sort(numberList, Comparator.comparing(n -> n.getNumber().substring(0, 3)));
        numberAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Sorted by area code", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Sorted list: " + numberList.toString());
    }
}
