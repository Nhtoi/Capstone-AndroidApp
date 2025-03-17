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

import java.util.List;

public class CallHistoryActivity extends AppCompatActivity {
    private RecyclerView rvCallHistory;
    private DatabaseRepository dbRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);

        // Initialize database repository
        dbRepository = new DatabaseRepository();

        rvCallHistory = findViewById(R.id.rvCallHistory);
        rvCallHistory.setLayoutManager(new LinearLayoutManager(this));

        List<DatabaseHelper.CallRecord> callRecords = dbRepository.getCallHistory();
        CallHistoryAdapter adapter = new CallHistoryAdapter(callRecords);
        rvCallHistory.setAdapter(adapter);
    }

    private class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder> {
        private List<DatabaseHelper.CallRecord> callRecords;

        public CallHistoryAdapter(List<DatabaseHelper.CallRecord> callRecords) {
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
            DatabaseHelper.CallRecord record = callRecords.get(position);

            holder.tvPhoneNumber.setText(record.getPhoneNumber());
            holder.tvTimestamp.setText(record.getTimestamp());
            holder.tvStatus.setText(record.getStatus());

            int minutes = record.getDuration() / 60;
            int seconds = record.getDuration() % 60;
            holder.tvDuration.setText(String.format("%d:%02d", minutes, seconds));

            View.OnClickListener clickListener = v -> {
                Intent intent = new Intent(CallHistoryActivity.this, TranscriptActivity.class);
                intent.putExtra("phone_number", record.getPhoneNumber());
                startActivity(intent);
            };

            // Set click listener on both the item view and the button
            holder.itemView.setOnClickListener(clickListener);
            holder.btnViewTranscript.setOnClickListener(clickListener);
        }

        @Override
        public int getItemCount() {
            return callRecords.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public View btnViewTranscript;
            TextView tvPhoneNumber, tvTimestamp, tvStatus, tvDuration;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
                tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvDuration = itemView.findViewById(R.id.tvDuration);
                btnViewTranscript = itemView.findViewById(R.id.btnViewTranscript); // Add this line
            }
        }

    }
}