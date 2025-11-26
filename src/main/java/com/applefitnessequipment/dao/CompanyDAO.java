package com.applefitnessequipment.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.Company;

public class CompanyDAO {

    // SQL Scripts for CRUD Operations
    private static final String SELECT_COMPANY =
        "SELECT CompanyID, CompanyName, StreetAddress, City, County, State, ZIPCode, " +
        "Country, Phone, Fax, Email, WebsiteURL FROM Company WHERE CompanyID = 1";

    private static final String UPDATE_COMPANY =
        "UPDATE Company SET CompanyName = ?, StreetAddress = ?, City = ?, County = ?, " +
        "State = ?, ZIPCode = ?, Country = ?, Phone = ?, Fax = ?, Email = ?, WebsiteURL = ? " +
        "WHERE CompanyID = 1";

    private static final String INSERT_COMPANY =
        "INSERT INTO Company (CompanyID, CompanyName, StreetAddress, City, County, State, " +
        "ZIPCode, Country, Phone, Fax, Email, WebsiteURL) " +
        "VALUES (1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Get the company record (there's always only one with CompanyID = 1)
     */
    public Company getCompany() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_COMPANY)) {

            if (rs.next()) {
                return extractCompanyFromResultSet(rs);
            }
        }
        return null;
    }

    /**
     * Update the company record
     */
    public boolean updateCompany(Company company) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_COMPANY)) {

            pstmt.setString(1, company.getCompanyName());
            pstmt.setString(2, company.getStreetAddress());
            pstmt.setString(3, company.getCity());
            pstmt.setString(4, company.getCounty());
            pstmt.setString(5, company.getState());
            pstmt.setString(6, company.getZipCode());
            pstmt.setString(7, company.getCountry());
            pstmt.setString(8, company.getPhone());
            pstmt.setString(9, company.getFax());
            pstmt.setString(10, company.getEmail());
            pstmt.setString(11, company.getWebsiteURL());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Insert the initial company record (only called if the table is empty)
     */
    public boolean insertCompany(Company company) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_COMPANY)) {

            pstmt.setString(1, company.getCompanyName());
            pstmt.setString(2, company.getStreetAddress());
            pstmt.setString(3, company.getCity());
            pstmt.setString(4, company.getCounty());
            pstmt.setString(5, company.getState());
            pstmt.setString(6, company.getZipCode());
            pstmt.setString(7, company.getCountry());
            pstmt.setString(8, company.getPhone());
            pstmt.setString(9, company.getFax());
            pstmt.setString(10, company.getEmail());
            pstmt.setString(11, company.getWebsiteURL());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Helper method to extract a Company object from a ResultSet
     */
    private Company extractCompanyFromResultSet(ResultSet rs) throws SQLException {
        Company company = new Company();
        company.setCompanyId(rs.getInt("CompanyID"));
        company.setCompanyName(rs.getString("CompanyName"));
        company.setStreetAddress(rs.getString("StreetAddress"));
        company.setCity(rs.getString("City"));
        company.setCounty(rs.getString("County"));
        company.setState(rs.getString("State"));
        company.setZipCode(rs.getString("ZIPCode"));
        company.setCountry(rs.getString("Country"));
        company.setPhone(rs.getString("Phone"));
        company.setFax(rs.getString("Fax"));
        company.setEmail(rs.getString("Email"));
        company.setWebsiteURL(rs.getString("WebsiteURL"));

        return company;
    }
}
