package com.applefitnessequipment.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Towson DB credentials
    private static final String ID = "kmattox2";   // NetID
    private static final String USER = ID;         // Username = NetID

    private static final String PASSWORD = "COSC*rjils";

    // Towson JDBC URL
    private static final String URL =
        "jdbc:mysql://triton.towson.edu:3360/" + ID + "db"
        + "?serverTimezone=America/New_York"
        + "&useSSL=false"
        + "&allowPublicKeyRetrieval=true";

    private DBConnection() {} // Prevent instantiation

    // Connection function
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}