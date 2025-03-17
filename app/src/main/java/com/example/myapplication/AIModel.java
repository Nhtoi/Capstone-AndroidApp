package com.example.myapplication;


// Model class to represent an AI model
public class AIModel {
    private String name;
    private String description;
    private int imageResourceId;

    public AIModel(String name, String description, int imageResourceId) {
        this.name = name;
        this.description = description;
        this.imageResourceId = imageResourceId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}

