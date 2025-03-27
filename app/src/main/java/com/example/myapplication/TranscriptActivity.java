package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class TranscriptActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript);

        TextView tvTranscript = findViewById(R.id.tvTranscript);
        String phoneNumber = getIntent().getStringExtra("phone_number");

        // Simulated call transcripts
        Map<String, String> transcripts = new HashMap<>();
        transcripts.put("123-456-7890", "Transcript for 123-456-7890\n\nScammer: Hello, can I get your credit card information?\nAI: Yes of course, just let me find it...\nScammer: Sounds good.\nAI: Okay, here we go, my card number is 27513\nScammer: That's a ZIP code...\nAI: Oh.. You're right, sorry, I'm really old...");
        transcripts.put("987-654-3210", "Transcript for 987-654-3210\n\nCaller: Hi, is this John?\nAI: No, you have the wrong number.\nCaller: Oh, sorry about that.");
        transcripts.put("555-555-5555", "Transcript for 555-555-5555\n\nTelemarketer: We'd love to offer you an extended warranty...\nAI: Sorry, I'm not interested.\nTelemarketer: But wait, this is a special offer!\nAI: No thanks, goodbye.");

        // Get transcript or use a default message
        String transcript = transcripts.getOrDefault(phoneNumber, "No transcript found for this call.");

        tvTranscript.setText(transcript);
    }
}
