package com.example.myapplication;

/**
 * configuration class for database connection
 * just have to add the postgresql from David later
 */
public class DatabaseConfig {
    // PostgreSQL connection details (to be used later) could use django
    private static final String POSTGRESQL_HOST = "your-postgresql-host";
    private static final String POSTGRESQL_PORT = "5432";
    private static final String POSTGRESQL_DATABASE = "your-database-name";
    private static final String POSTGRESQL_USERNAME = "your-username";
    private static final String POSTGRESQL_PASSWORD = "your-password";

    // Database mode: "mock" or "postgresql"
    private static final String DB_MODE = "mock";

    // Get database helper based on mode
    public static Object getDatabaseHelper() {
        if (DB_MODE.equals("mock")) {
            return DatabaseHelper.getInstance();
        } else if (DB_MODE.equals("postgresql")) {
            // return PostgreSQLHelper.getInstance(
            //     POSTGRESQL_HOST,
            //     POSTGRESQL_PORT,
            //     POSTGRESQL_DATABASE,
            //     POSTGRESQL_USERNAME,
            //     POSTGRESQL_PASSWORD
            // );
            throw new UnsupportedOperationException("PostgreSQL connection not yet implemented");
        } else {
            throw new IllegalStateException("Invalid database mode: " + DB_MODE);
        }
    }

    // Method to check if database is connected
    public static boolean isDatabaseConnected() {
        // For mock database, always return true
        if (DB_MODE.equals("mock")) {
            return true;
        } else {
            // For PostgreSQL, implement connection check here
            return false;
        }
    }
}