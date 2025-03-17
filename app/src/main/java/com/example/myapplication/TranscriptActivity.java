package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TranscriptActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript);

        TextView tvTranscript = findViewById(R.id.tvTranscript);
        String phoneNumber = getIntent().getStringExtra("phone_number");

        DatabaseHelper db = DatabaseHelper.getInstance();
        String transcript = db.getCallTranscript(phoneNumber);

        if (transcript.equals("Transcript not found.")) {
            transcript = "Transcript for " + phoneNumber + "\n\n" +
                    "Scammer: Hello, can I get your credit card information.\n" +
                    "AI: Yes of course just let me find it?\n" +
                    "Scammer: Sounds good.\n" +
                    "AI: Okay, here we go, my card number IS 27513 \n" +
                    "Scammer: That's a zipcode...\n" +
                    "AI: Oh.. You're right sorry im really old..";
            db.addCallTranscript(phoneNumber, transcript);
        }

        tvTranscript.setText(transcript);
    }}
