package com.example.myapplication;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock database implementation to simulate database operations
 * This will be replaced with a real PostgreSQL implementation later
 */
public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";

    // Singleton instance
    private static DatabaseHelper instance;

    // Mock tables
    private Map<String, User> users;
    private List<String> whitelistNumbers;
    private List<String> blacklistNumbers;
    private List<CallRecord> callHistory;


    // Private constructor for singleton pattern
    private DatabaseHelper() {
        // Initialize mock tables
        users = new HashMap<>();
        whitelistNumbers = new ArrayList<>();
        blacklistNumbers = new ArrayList<>();
        callHistory = new ArrayList<>();

        // Add some demo data
        users.put("demo", new User("demo", "password123", "Demo User"));
        users.put("admin", new User("admin", "admin123", "Administrator"));

        whitelistNumbers.add("+1234567890");
        whitelistNumbers.add("+9876543210");

        blacklistNumbers.add("+1122334455");
        blacklistNumbers.add("+5566778899");

        callHistory.add(new CallRecord("+123456789", "2025-03-15 14:30:22", "Spam detected", 120));
        callHistory.add(new CallRecord("+987654321", "2025-03-16 09:45:10", "Call allowed", 180));
        callHistory.add(new CallRecord("+112233445", "2025-03-17 11:22:33", "Call blocked", 0));
    }

    // Get singleton instance
    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    // User authentication methods
    public boolean authenticateUser(String username, String password) {
        Log.d(TAG, "Authenticating user: " + username);
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }

    public User getUserByUsername(String username) {
        return users.get(username);
    }

    public boolean registerUser(String username, String password, String displayName) {
        if (users.containsKey(username)) {
            return false; // Username already exists
        }
        users.put(username, new User(username, password, displayName));
        return true;
    }

    // Whitelist methods
    public List<String> getWhitelistNumbers() {
        return new ArrayList<>(whitelistNumbers);
    }

    public boolean addToWhitelist(String phoneNumber) {
        if (!whitelistNumbers.contains(phoneNumber)) {
            whitelistNumbers.add(phoneNumber);
            return true;
        }
        return false;
    }

    public boolean removeFromWhitelist(String phoneNumber) {
        return whitelistNumbers.remove(phoneNumber);
    }

    // Blacklist methods
    public List<String> getBlacklistNumbers() {
        return new ArrayList<>(blacklistNumbers);
    }

    public boolean addToBlacklist(String phoneNumber) {
        if (!blacklistNumbers.contains(phoneNumber)) {
            blacklistNumbers.add(phoneNumber);
            return true;
        }
        return false;
    }

    public boolean removeFromBlacklist(String phoneNumber) {
        return blacklistNumbers.remove(phoneNumber);
    }

    // Call history methods
    public List<CallRecord> getCallHistory() {
        return new ArrayList<>(callHistory);
    }

    public void addCallRecord(CallRecord record) {
        callHistory.add(record);
    }

    public List<CallRecord> getCallHistoryByNumber(String phoneNumber) {
        List<CallRecord> records = new ArrayList<>();
        for (CallRecord record : callHistory) {
            if (record.getPhoneNumber().equals(phoneNumber)) {
                records.add(record);
            }
        }
        return records;
    }

    // Model classes for data storage
    public static class User {
        private String username;
        private String password;
        private String displayName;

        public User(String username, String password, String displayName) {
            this.username = username;
            this.password = password;
            this.displayName = displayName;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static class CallRecord {
        private String phoneNumber;
        private String timestamp;
        private String status;
        private int duration; // in seconds

        public CallRecord(String phoneNumber, String timestamp, String status, int duration) {
            this.phoneNumber = phoneNumber;
            this.timestamp = timestamp;
            this.status = status;
            this.duration = duration;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getStatus() {
            return status;
        }

        public int getDuration() {
            return duration;
        }
    }

    private Map<String, String> callTranscripts = new HashMap<>();

    public void addCallTranscript(String phoneNumber, String transcript) {
        callTranscripts.put(phoneNumber, transcript);
    }

    public String getCallTranscript(String phoneNumber) {
        return callTranscripts.getOrDefault(phoneNumber, "Transcript not found.");
    }


}