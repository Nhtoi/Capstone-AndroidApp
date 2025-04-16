package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.concurrent.Executors;

public class CallHistoryActivity extends AppCompatActivity {
    private RecyclerView rvCallHistory;
    private List<CallRecord> callRecords = new ArrayList<>();
    private static final String TAG = "CallHistoryActivity";
    private static final String USER_ID = "6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);

        rvCallHistory = findViewById(R.id.rvCallHistory);
        rvCallHistory.setLayoutManager(new LinearLayoutManager(this));

        fetchCallHistory();
    }

    private void fetchCallHistory() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CallRecord> records = new ArrayList<>();
            try {
                URL url = new URL("https://scam-scam-service-185231488037.us-central1.run.app/api/v1/users/pull-call");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("user", USER_ID);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes());
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    conn.disconnect();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray callLogs = jsonResponse.getJSONArray("result");

                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());

                    for (int i = 0; i < callLogs.length(); i++) {
                        JSONObject call = callLogs.getJSONObject(i);
                        int callId = call.getInt("id");
                        String callStartStr = call.getString("callStart");
                        String callEndStr = call.getString("callEnd");

                        Date callStart = isoFormat.parse(callStartStr);
                        Date callEnd = isoFormat.parse(callEndStr);

                        long durationSeconds = (callEnd.getTime() - callStart.getTime()) / 1000;
                        String timestamp = displayFormat.format(callStart);
                        String fakePhone = "+1-000-000-000" + i;

                        records.add(new CallRecord(fakePhone, timestamp, (int) durationSeconds, callId));
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching call history", e);
            }

            if (records.isEmpty()) {
                records.add(new CallRecord("+1-111-222-3333", "2025-04-15 12:34 PM", 180, 999));
            }

            runOnUiThread(() -> {
                callRecords.clear();
                callRecords.addAll(records);
                rvCallHistory.setAdapter(new CallHistoryAdapter(callRecords));
            });
        });
    }

    private class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder> {
        private final List<CallRecord> callRecords;

        public CallHistoryAdapter(List<CallRecord> callRecords) {
            this.callRecords = callRecords;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CallRecord record = callRecords.get(position);

            holder.tvPhoneNumber.setText(record.getPhoneNumber());
            holder.tvTimestamp.setText(record.getTimestamp());

            int minutes = record.getDuration() / 60;
            int seconds = record.getDuration() % 60;
            holder.tvDuration.setText(String.format(Locale.getDefault(), "%d:%02d", minutes, seconds));

            View.OnClickListener clickListener = v -> {
                Intent intent = new Intent(CallHistoryActivity.this, TranscriptActivity.class);
                intent.putExtra("user_id", USER_ID);
                intent.putExtra("call_id", String.valueOf(record.getCallId())); // ✅ pass as String
                startActivity(intent);
            };

            holder.itemView.setOnClickListener(clickListener);
            holder.btnViewTranscript.setOnClickListener(clickListener);
        }

        @Override
        public int getItemCount() {
            return callRecords.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View btnViewTranscript;
            TextView tvPhoneNumber, tvTimestamp, tvDuration;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
                tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
                tvDuration = itemView.findViewById(R.id.tvDuration);
                btnViewTranscript = itemView.findViewById(R.id.btnViewTranscript);
            }
        }
    }

    private static class CallRecord {
        private final String phoneNumber;
        private final String timestamp;
        private final int duration;
        private final int callId;

        public CallRecord(String phoneNumber, String timestamp, int duration, int callId) {
            this.phoneNumber = phoneNumber;
            this.timestamp = timestamp;
            this.duration = duration;
            this.callId = callId;
        }

        public String getPhoneNumber() { return phoneNumber; }
        public String getTimestamp() { return timestamp; }
        public int getDuration() { return duration; }
        public int getCallId() { return callId; }
    }
}
