package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder> {
    private List<String> callHistory;

    public CallHistoryAdapter(List<String> callHistory) {
        this.callHistory = callHistory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String number = callHistory.get(position);
        holder.tvNumber.setText(number);

        holder.btnViewTranscript.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, TranscriptActivity.class);
            intent.putExtra("phone_number", number);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return callHistory.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber;
        Button btnViewTranscript;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            btnViewTranscript = itemView.findViewById(R.id.btnViewTranscript);
        }
    }
}
