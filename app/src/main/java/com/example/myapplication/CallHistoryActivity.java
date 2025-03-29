package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CallHistoryActivity extends AppCompatActivity {
    private RecyclerView rvCallHistory;
    private List<CallRecord> callRecords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);

        rvCallHistory = findViewById(R.id.rvCallHistory);
        rvCallHistory.setLayoutManager(new LinearLayoutManager(this));

        // Dummy data for testing (replace with real data source if needed)
        callRecords.add(new CallRecord("123-456-7890", "2025-03-27 12:30 PM",125));
        callRecords.add(new CallRecord("987-654-3210", "2025-03-27 1:15 PM",0));
        callRecords.add(new CallRecord("555-555-5555", "2025-03-27 2:00 PM", 240));

        CallHistoryAdapter adapter = new CallHistoryAdapter(callRecords);
        rvCallHistory.setAdapter(adapter);
    }

    private class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder> {
        private final List<CallRecord> callRecords;

        public CallHistoryAdapter(List<CallRecord> callRecords) {
            this.callRecords = callRecords;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_call_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CallRecord record = callRecords.get(position);

            holder.tvPhoneNumber.setText(record.getPhoneNumber());
            holder.tvTimestamp.setText(record.getTimestamp());

            int minutes = record.getDuration() / 60;
            int seconds = record.getDuration() % 60;
            holder.tvDuration.setText(String.format("%d:%02d", minutes, seconds));

            View.OnClickListener clickListener = v -> {
                Intent intent = new Intent(CallHistoryActivity.this, TranscriptActivity.class);
                intent.putExtra("phone_number", record.getPhoneNumber());
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

        public CallRecord(String phoneNumber, String timestamp, int duration) {
            this.phoneNumber = phoneNumber;
            this.timestamp = timestamp;
            this.duration = duration;
        }
        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public int getDuration() {
            return duration;
        }
    }
}
