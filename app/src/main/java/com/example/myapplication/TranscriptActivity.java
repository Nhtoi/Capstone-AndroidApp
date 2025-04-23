package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;

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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TranscriptActivity extends AppCompatActivity {
    private RecyclerView rvTranscript;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private static final String TAG = "TranscriptActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript);

        rvTranscript = findViewById(R.id.rvTranscript);
        rvTranscript.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messages);
        rvTranscript.setAdapter(adapter);

        String userId = getIntent().getStringExtra("user_id");
        String selectedCallId = getIntent().getStringExtra("call_id");

        Log.d(TAG, "Launching TranscriptActivity with user_id: " + userId + ", call_id: " + selectedCallId);

        if (userId != null && selectedCallId != null) {
            fetchCallLogs(userId, selectedCallId);
        } else {
            messages.add(new Message("Missing user or call ID", Message.AI, "AI Assistant", null));
            adapter.notifyDataSetChanged();
        }
    }

    private void fetchCallLogs(String userId, String selectedCallId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<Message> resultMessages = new ArrayList<>();

            try {
                URL url = new URL("https://scam-scam-service-185231488037.us-central1.run.app/api/v1/users/pull-call");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("user", userId);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes());
                os.flush();
                os.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                conn.disconnect();

                JSONArray callLogs = new JSONObject(response.toString()).getJSONArray("result");
                List<JSONObject> callList = new ArrayList<>();

                for (int i = 0; i < callLogs.length(); i++) {
                    callList.add(callLogs.getJSONObject(i));
                }

                // Sort by call ID descending (most recent first)
                callList.sort((a, b) -> Integer.compare(b.optInt("id"), a.optInt("id")));

                int selectedCallIdInt = Integer.parseInt(selectedCallId);

                for (JSONObject call : callList) {
                    int callId = call.getInt("id");
                    if (callId != selectedCallIdInt) continue;

                    String transcript = call.optString("fullTranscript", "");
                    String[] lines = transcript.split("\\n");

                    for (String line : lines) {
                        if (line.trim().isEmpty()) continue;

                        String speaker = "Caller";
                        int messageType = Message.CALLER;

                        if (line.toLowerCase().startsWith("ai:") || line.toLowerCase().contains("ai assistant")) {
                            speaker = "AI Assistant";
                            messageType = Message.AI;
                        }

                        resultMessages.add(new Message(line.trim(), messageType, speaker, String.valueOf(callId)));
                    }

                    break; // Stop after finding the selected call
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching call logs", e);
                resultMessages.add(new Message("Failed to load transcripts.", Message.AI, "AI Assistant", null));
            }

            runOnUiThread(() -> {
                messages.clear();
                messages.addAll(resultMessages);
                adapter.notifyDataSetChanged();
            });
        });
    }

}
