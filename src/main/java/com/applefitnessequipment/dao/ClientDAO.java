package com.applefitnessequipment.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.Client;

public class ClientDAO {

    // SQL Scripts for CRUD Operations
    private static final String INSERT_CLIENT = 
        "INSERT INTO clients (ClientType, FirstName, LastName, CompanyName, PhoneNumber, Email, Notes) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_CLIENTS = 
        "SELECT ClientID, ClientType, FirstName, LastName, CompanyName, PhoneNumber, Email, Notes, CreatedAt, UpdatedAt " +
        "FROM clients ORDER BY ClientID DESC";

    private static final String SELECT_CLIENT_BY_ID = 
        "SELECT ClientID, ClientType, FirstName, LastName, CompanyName, PhoneNumber, Email, Notes, CreatedAt, UpdatedAt " +
        "FROM clients WHERE ClientID = ?";

    private static final String UPDATE_CLIENT = 
        "UPDATE clients SET ClientType = ?, FirstName = ?, LastName = ?, CompanyName = ?, " +
        "PhoneNumber = ?, Email = ?, Notes = ? WHERE ClientID = ?";

    private static final String DELETE_CLIENT = 
        "DELETE FROM clients WHERE ClientID = ?";

    private static final String SEARCH_CLIENTS = 
        "SELECT ClientID, ClientType, FirstName, LastName, CompanyName, PhoneNumber, Email, Notes, CreatedAt, UpdatedAt " +
        "FROM clients WHERE FirstName LIKE ? OR LastName LIKE ? OR CompanyName LIKE ? OR Email LIKE ? " +
        "ORDER BY ClientID DESC";

    /**
     * Add a new client to the database
     */
    public boolean addClient(Client client) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_CLIENT, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, client.getClientType());
            pstmt.setString(2, client.getFirstName());
            pstmt.setString(3, client.getLastName());
            pstmt.setString(4, client.getCompanyName());
            pstmt.setString(5, client.getPhoneNumber());
            pstmt.setString(6, client.getEmail());
            pstmt.setString(7, client.getNotes());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        client.setClientId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Retrieve all clients from the database
     */
    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_CLIENTS)) {
            
            while (rs.next()) {
                clients.add(extractClientFromResultSet(rs));
            }
        }
        
        return clients;
    }

    /**
     * Retrieve a client by ID
     */
    public Client getClientById(int clientId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_CLIENT_BY_ID)) {
            
            pstmt.setInt(1, clientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractClientFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Update an existing client
     */
    public boolean updateClient(Client client) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_CLIENT)) {
            
            pstmt.setString(1, client.getClientType());
            pstmt.setString(2, client.getFirstName());
            pstmt.setString(3, client.getLastName());
            pstmt.setString(4, client.getCompanyName());
            pstmt.setString(5, client.getPhoneNumber());
            pstmt.setString(6, client.getEmail());
            pstmt.setString(7, client.getNotes());
            pstmt.setInt(8, client.getClientId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete a client by ID
     */
    public boolean deleteClient(int clientId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_CLIENT)) {
            
            pstmt.setInt(1, clientId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Search clients by name, company, or email
     */
    public List<Client> searchClients(String searchTerm) throws SQLException {
        List<Client> clients = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SEARCH_CLIENTS)) {
            
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    clients.add(extractClientFromResultSet(rs));
                }
            }
        }
        
        return clients;
    }

    /**
     * Helper method to extract a Client object from a ResultSet
     */
    private Client extractClientFromResultSet(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setClientId(rs.getInt("ClientID"));
        client.setClientType(rs.getString("ClientType"));
        client.setFirstName(rs.getString("FirstName"));
        client.setLastName(rs.getString("LastName"));
        client.setCompanyName(rs.getString("CompanyName"));
        client.setPhoneNumber(rs.getString("PhoneNumber"));
        client.setEmail(rs.getString("Email"));
        client.setNotes(rs.getString("Notes"));
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            client.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
        if (updatedAt != null) {
            client.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return client;
    }
}
