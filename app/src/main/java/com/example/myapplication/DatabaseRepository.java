package com.example.myapplication;

import android.util.Log;
import java.util.List;

/**
 * Repository class that provides methods to interact with the database
 * Works with both mock database and future PostgreSQL implementation
 */
public class DatabaseRepository {
    private static final String TAG = "DatabaseRepository";
    private DatabaseHelper dbHelper;

    public DatabaseRepository() {
        // Get the appropriate database helper based on configuration
        Object helper = DatabaseConfig.getDatabaseHelper();

        if (helper instanceof DatabaseHelper) {
            this.dbHelper = (DatabaseHelper) helper;
        } else {
            // This will be updated when PostgreSQL is implemented
            throw new UnsupportedOperationException("PostgreSQL connection not yet implemented");
        }
    }

   // Authentication methods this should communicate with Jaskaran's end not Davids

    public boolean authenticateUser(String username, String password) {
        Log.d(TAG, "Authenticating user: " + username);
        return dbHelper.authenticateUser(username, password);
    }

    public boolean registerUser(String username, String password, String displayName) {
        Log.d(TAG, "Registering new user: " + username);
        return dbHelper.registerUser(username, password, displayName);
    }

    public DatabaseHelper.User getUserByUsername(String username) {
        return dbHelper.getUserByUsername(username);
    }

    // Whitelist methods
    public List<String> getWhitelistNumbers() {
        return dbHelper.getWhitelistNumbers();
    }

    public boolean addToWhitelist(String phoneNumber) {
        Log.d(TAG, "Adding to whitelist: " + phoneNumber);
        return dbHelper.addToWhitelist(phoneNumber);
    }

    public boolean removeFromWhitelist(String phoneNumber) {
        Log.d(TAG, "Removing from whitelist: " + phoneNumber);
        return dbHelper.removeFromWhitelist(phoneNumber);
    }

    // Blacklist methods
    public List<String> getBlacklistNumbers() {
        return dbHelper.getBlacklistNumbers();
    }

    public boolean addToBlacklist(String phoneNumber) {
        Log.d(TAG, "Adding to blacklist: " + phoneNumber);
        return dbHelper.addToBlacklist(phoneNumber);
    }

    public boolean removeFromBlacklist(String phoneNumber) {
        Log.d(TAG, "Removing from blacklist: " + phoneNumber);
        return dbHelper.removeFromBlacklist(phoneNumber);
    }

    // Call history methods
    public List<DatabaseHelper.CallRecord> getCallHistory() {
        return dbHelper.getCallHistory();
    }

    public void addCallRecord(String phoneNumber, String timestamp, String status, int duration) {
        Log.d(TAG, "Adding call record for: " + phoneNumber);
        DatabaseHelper.CallRecord record =
                new DatabaseHelper.CallRecord(phoneNumber, timestamp, status, duration);
        dbHelper.addCallRecord(record);
    }

    public List<DatabaseHelper.CallRecord> getCallHistoryByNumber(String phoneNumber) {
        return dbHelper.getCallHistoryByNumber(phoneNumber);
    }
}