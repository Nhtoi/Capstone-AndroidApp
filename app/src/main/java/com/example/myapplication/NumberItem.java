package com.example.myapplication;

public class NumberItem {
    private String number;
    private String status; // "Whitelist" or "Blacklist"

    public NumberItem(String number, String status) {
        this.number = number;
        this.status = status;
    }

    public String getNumber() {
        return number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
