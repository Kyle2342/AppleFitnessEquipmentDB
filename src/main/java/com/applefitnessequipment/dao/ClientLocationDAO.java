package com.applefitnessequipment.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.ClientLocation;

public class ClientLocationDAO {

    // SQL Scripts for CRUD Operations
private static final String INSERT_LOCATION =
    "INSERT INTO ClientsLocations (ClientID, LocationType, " +
    "BuildingName, RoomNumber, StreetAddress, City, State, ZIPCode, Country, Phone, Fax, Email) " +
    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

private static final String SELECT_ALL_LOCATIONS =
    "SELECT ClientLocationID, ClientID, LocationType, " +
    "BuildingName, RoomNumber, StreetAddress, City, State, ZIPCode, Country, Phone, Fax, Email " +
    "FROM ClientsLocations ORDER BY ClientLocationID DESC";

private static final String SELECT_LOCATION_BY_ID =
    "SELECT ClientLocationID, ClientID, LocationType, " +
    "BuildingName, RoomNumber, StreetAddress, City, State, ZIPCode, Country, Phone, Fax, Email " +
    "FROM ClientsLocations WHERE ClientLocationID = ?";

private static final String SELECT_LOCATIONS_BY_CLIENT =
    "SELECT ClientLocationID, ClientID, LocationType, " +
    "BuildingName, RoomNumber, StreetAddress, City, State, ZIPCode, Country, Phone, Fax, Email " +
    "FROM ClientsLocations WHERE ClientID = ? ORDER BY LocationType, ClientLocationID";


private static final String UPDATE_LOCATION =
    "UPDATE ClientsLocations SET ClientID = ?, LocationType = ?, " +
    "BuildingName = ?, RoomNumber = ?, StreetAddress = ?, City = ?, State = ?, " +
    "ZIPCode = ?, Country = ?, Phone = ?, Fax = ?, Email = ? WHERE ClientLocationID = ?";


    private static final String DELETE_LOCATION =
        "DELETE FROM ClientsLocations WHERE ClientLocationID = ?";

    /**
     * Add a new client location to the database
     */
    public boolean addClientLocation(ClientLocation location) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_LOCATION, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, location.getClientId());
            pstmt.setString(2, location.getLocationType());
            pstmt.setString(3, location.getBuildingName());
            pstmt.setString(4, location.getRoomNumber());
            pstmt.setString(5, location.getStreetAddress());
            pstmt.setString(6, location.getCity());
            pstmt.setString(7, location.getState());
            pstmt.setString(8, location.getZipCode());
            pstmt.setString(9, location.getCountry());
            pstmt.setString(10, location.getPhone());
            pstmt.setString(11, location.getFax());
            pstmt.setString(12, location.getEmail());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        location.setClientLocationId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Retrieve all client locations from the database
     */
    public List<ClientLocation> getAllClientLocations() throws SQLException {
        List<ClientLocation> locations = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_LOCATIONS)) {

            while (rs.next()) {
                locations.add(extractLocationFromResultSet(rs));
            }
        }

        return locations;
    }

    /**
     * Retrieve a client location by ID
     */
    public ClientLocation getClientLocationById(int locationId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_LOCATION_BY_ID)) {

            pstmt.setInt(1, locationId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractLocationFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieve all locations for a specific client
     */
    public List<ClientLocation> getLocationsByClientId(int clientId) throws SQLException {
        List<ClientLocation> locations = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_LOCATIONS_BY_CLIENT)) {

            pstmt.setInt(1, clientId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    locations.add(extractLocationFromResultSet(rs));
                }
            }
        }

        return locations;
    }

    /**
     * Update an existing client location
     */
    public boolean updateClientLocation(ClientLocation location) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_LOCATION)) {

            pstmt.setInt(1, location.getClientId());
            pstmt.setString(2, location.getLocationType());
            pstmt.setString(3, location.getBuildingName());
            pstmt.setString(4, location.getRoomNumber());
            pstmt.setString(5, location.getStreetAddress());
            pstmt.setString(6, location.getCity());
            pstmt.setString(7, location.getState());
            pstmt.setString(8, location.getZipCode());
            pstmt.setString(9, location.getCountry());
            pstmt.setString(10, location.getPhone());
            pstmt.setString(11, location.getFax());
            pstmt.setString(12, location.getEmail());
            pstmt.setInt(13, location.getClientLocationId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete a client location by ID
     */
    public boolean deleteClientLocation(int locationId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_LOCATION)) {

            pstmt.setInt(1, locationId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Helper method to extract a ClientLocation object from a ResultSet
     */
    private ClientLocation extractLocationFromResultSet(ResultSet rs) throws SQLException {
        ClientLocation location = new ClientLocation();
        location.setClientLocationId(rs.getInt("ClientLocationID"));
        location.setClientId(rs.getInt("ClientID"));
        location.setLocationType(rs.getString("LocationType"));
        location.setBuildingName(rs.getString("BuildingName"));
        location.setRoomNumber(rs.getString("RoomNumber"));
        location.setStreetAddress(rs.getString("StreetAddress"));
        location.setCity(rs.getString("City"));
        location.setState(rs.getString("State"));
        location.setZipCode(rs.getString("ZIPCode"));
        location.setCountry(rs.getString("Country"));
        location.setPhone(rs.getString("Phone"));
        location.setFax(rs.getString("Fax"));
        location.setEmail(rs.getString("Email"));

        return location;
    }
}
