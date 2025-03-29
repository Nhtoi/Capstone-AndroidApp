package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.ViewHolder> {
    private List<NumberItem> numberList;

    public NumberAdapter(List<NumberItem> numberList) {
        this.numberList = numberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NumberItem item = numberList.get(position);
        holder.tvNumber.setText(item.getNumber());

        // Set dropdown selection
        holder.spStatus.setSelection(item.getStatus().equals("Whitelist") ? 0 : 1);

        holder.spStatus.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                item.setStatus(parent.getItemAtPosition(pos).toString());
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        holder.btnDelete.setOnClickListener(v -> {
            numberList.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return numberList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber;
        Spinner spStatus;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            spStatus = itemView.findViewById(R.id.spStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    itemView.getContext(),
                    R.array.status_options,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spStatus.setAdapter(adapter);
        }
    }
}
