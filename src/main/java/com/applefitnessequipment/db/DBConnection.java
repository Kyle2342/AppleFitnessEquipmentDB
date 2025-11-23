package com.applefitnessequipment.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Connection Information
    private static final String URL = "jdbc:mysql://localhost:3306/applefitnessequipmentdb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "ultra";

    private DBConnection() {} // prevent instantiation

    // Connection Function
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}