package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TranscriptActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript);

        RecyclerView rvTranscript = findViewById(R.id.rvTranscript);
        rvTranscript.setLayoutManager(new LinearLayoutManager(this));

        String phoneNumber = getIntent().getStringExtra("phone_number");

        List<Message> messages = new ArrayList<>();
        if ("123-456-7890".equals(phoneNumber)) {
            messages.add(new Message("Hello, can I get your credit card information?", Message.CALLER, "Caller"));
            messages.add(new Message("Yes of course, just let me find it...", Message.AI, "AI Assistant"));
            messages.add(new Message("Sounds good.", Message.CALLER, "Caller"));
            messages.add(new Message("Okay, here we go, my card number is 27513", Message.AI, "AI Assistant"));
            messages.add(new Message("That's a ZIP code...", Message.CALLER, "Caller"));
            messages.add(new Message("Oh.. You're right, sorry, I'm really old...", Message.AI, "AI Assistant"));
        } else if ("987-654-3210".equals(phoneNumber)) {
            messages.add(new Message("Hi, is this John?", Message.CALLER, "Caller"));
            messages.add(new Message("No, you have the wrong number.", Message.AI, "AI Assistant"));
            messages.add(new Message("Oh, sorry about that.", Message.CALLER, "Caller"));
        } else {
            messages.add(new Message("No transcript found for this call.", Message.AI, "AI Assistant"));
        }

        // Set the adapter only after the data is ready
        MessageAdapter adapter = new MessageAdapter(messages);
        rvTranscript.setAdapter(adapter);  // Ensure the adapter is set
    }
}