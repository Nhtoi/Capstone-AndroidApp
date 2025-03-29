package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WhiteListBlackList extends AppCompatActivity {
    private EditText etNumber;
    private Button btnAdd, btnSort;
    private RecyclerView rvNumbers;
    private NumberAdapter numberAdapter;
    private List<NumberItem> numberList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    }

    private void addNumber() {
        String number = etNumber.getText().toString().trim();
        if (!number.isEmpty() && !isNumberInList(number)) {
            numberList.add(new NumberItem(number, "Whitelist"));
            numberAdapter.notifyDataSetChanged();
            etNumber.setText("");
            Toast.makeText(this, "Number added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Invalid or duplicate entry", Toast.LENGTH_SHORT).show();
        }
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
        Toast.makeText(this, "Numbers sorted by area code", Toast.LENGTH_SHORT).show();
    }
}
