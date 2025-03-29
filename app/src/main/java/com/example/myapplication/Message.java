package com.example.myapplication;

public class Message {
    public static final int CALLER = 0;
    public static final int AI = 1;

    private String text;
    private int sender;
    private String senderName;

    public Message(String text, int sender, String senderName) {
        this.text = text;
        this.sender = sender;
        this.senderName = senderName;
    }

    public String getText() {
        return text;
    }

    public int getSender() {
        return sender;
    }

    public String getSenderName() {
        return senderName;
    }
}
