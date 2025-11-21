package com.applefitnessequipment.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.Employee;

public class EmployeeDAO {

    // SQL Scripts for CRUD Operations
    private static final String INSERT_EMPLOYEE = 
        "INSERT INTO employees (FirstName, LastName, MiddleInitial, DateOfBirth, Gender, " +
        "WorkEmail, PersonalEmail, WorkPhone, MobilePhone, HomeBuildingName, HomeSuiteNumber, " +
        "HomeStreetAddress, HomeCity, HomeState, HomeZIPCode, HomeCountry, PositionTitle, " +
        "EmploymentType, HireDate, TerminationDate, ActiveStatus, EmergencyContactName, " +
        "EmergencyContactPhone, EmergencyContactRelationship, Username, PasswordHash, PayType, PayRate) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_EMPLOYEES = 
        "SELECT EmployeeID, FirstName, LastName, MiddleInitial, DateOfBirth, Gender, WorkEmail, " +
        "PersonalEmail, WorkPhone, MobilePhone, HomeBuildingName, HomeSuiteNumber, HomeStreetAddress, " +
        "HomeCity, HomeState, HomeZIPCode, HomeCountry, PositionTitle, EmploymentType, HireDate, " +
        "TerminationDate, ActiveStatus, EmergencyContactName, EmergencyContactPhone, " +
        "EmergencyContactRelationship, Username, PasswordHash, PayType, PayRate, CreatedAt, UpdatedAt " +
        "FROM employees ORDER BY EmployeeID DESC";

    private static final String SELECT_EMPLOYEE_BY_ID = 
        "SELECT EmployeeID, FirstName, LastName, MiddleInitial, DateOfBirth, Gender, WorkEmail, " +
        "PersonalEmail, WorkPhone, MobilePhone, HomeBuildingName, HomeSuiteNumber, HomeStreetAddress, " +
        "HomeCity, HomeState, HomeZIPCode, HomeCountry, PositionTitle, EmploymentType, HireDate, " +
        "TerminationDate, ActiveStatus, EmergencyContactName, EmergencyContactPhone, " +
        "EmergencyContactRelationship, Username, PasswordHash, PayType, PayRate, CreatedAt, UpdatedAt " +
        "FROM employees WHERE EmployeeID = ?";

    private static final String UPDATE_EMPLOYEE = 
        "UPDATE employees SET FirstName = ?, LastName = ?, MiddleInitial = ?, DateOfBirth = ?, " +
        "Gender = ?, WorkEmail = ?, PersonalEmail = ?, WorkPhone = ?, MobilePhone = ?, " +
        "HomeBuildingName = ?, HomeSuiteNumber = ?, HomeStreetAddress = ?, HomeCity = ?, " +
        "HomeState = ?, HomeZIPCode = ?, HomeCountry = ?, PositionTitle = ?, EmploymentType = ?, " +
        "HireDate = ?, TerminationDate = ?, ActiveStatus = ?, EmergencyContactName = ?, " +
        "EmergencyContactPhone = ?, EmergencyContactRelationship = ?, Username = ?, PayType = ?, " +
        "PayRate = ? WHERE EmployeeID = ?";

    private static final String DELETE_EMPLOYEE = 
        "DELETE FROM employees WHERE EmployeeID = ?";

    private static final String SEARCH_EMPLOYEES = 
        "SELECT EmployeeID, FirstName, LastName, MiddleInitial, DateOfBirth, Gender, WorkEmail, " +
        "PersonalEmail, WorkPhone, MobilePhone, HomeBuildingName, HomeSuiteNumber, HomeStreetAddress, " +
        "HomeCity, HomeState, HomeZIPCode, HomeCountry, PositionTitle, EmploymentType, HireDate, " +
        "TerminationDate, ActiveStatus, EmergencyContactName, EmergencyContactPhone, " +
        "EmergencyContactRelationship, Username, PasswordHash, PayType, PayRate, CreatedAt, UpdatedAt " +
        "FROM employees WHERE FirstName LIKE ? OR LastName LIKE ? OR Username LIKE ? OR PositionTitle LIKE ? " +
        "ORDER BY EmployeeID DESC";

    /**
     * Add a new employee to the database
     */
    public boolean addEmployee(Employee employee, String plainPassword) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_EMPLOYEE, Statement.RETURN_GENERATED_KEYS)) {
            
            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
            
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getMiddleInitial());
            pstmt.setDate(4, employee.getDateOfBirth() != null ? Date.valueOf(employee.getDateOfBirth()) : null);
            pstmt.setString(5, employee.getGender());
            pstmt.setString(6, employee.getWorkEmail());
            pstmt.setString(7, employee.getPersonalEmail());
            pstmt.setString(8, employee.getWorkPhone());
            pstmt.setString(9, employee.getMobilePhone());
            pstmt.setString(10, employee.getHomeBuildingName());
            pstmt.setString(11, employee.getHomeSuiteNumber());
            pstmt.setString(12, employee.getHomeStreetAddress());
            pstmt.setString(13, employee.getHomeCity());
            pstmt.setString(14, employee.getHomeState());
            pstmt.setString(15, employee.getHomeZIPCode());
            pstmt.setString(16, employee.getHomeCountry());
            pstmt.setString(17, employee.getPositionTitle());
            pstmt.setString(18, employee.getEmploymentType());
            pstmt.setDate(19, Date.valueOf(employee.getHireDate()));
            pstmt.setDate(20, employee.getTerminationDate() != null ? Date.valueOf(employee.getTerminationDate()) : null);
            pstmt.setBoolean(21, employee.getActiveStatus());
            pstmt.setString(22, employee.getEmergencyContactName());
            pstmt.setString(23, employee.getEmergencyContactPhone());
            pstmt.setString(24, employee.getEmergencyContactRelationship());
            pstmt.setString(25, employee.getUsername());
            pstmt.setString(26, hashedPassword);
            pstmt.setString(27, employee.getPayType());
            pstmt.setBigDecimal(28, employee.getPayRate());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        employee.setEmployeeId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Retrieve all employees from the database
     */
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_EMPLOYEES)) {
            
            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
        }
        
        return employees;
    }

    /**
     * Retrieve an employee by ID
     */
    public Employee getEmployeeById(int employeeId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_EMPLOYEE_BY_ID)) {
            
            pstmt.setInt(1, employeeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractEmployeeFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Update an existing employee (without changing password)
     */
    public boolean updateEmployee(Employee employee) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_EMPLOYEE)) {
            
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getMiddleInitial());
            pstmt.setDate(4, employee.getDateOfBirth() != null ? Date.valueOf(employee.getDateOfBirth()) : null);
            pstmt.setString(5, employee.getGender());
            pstmt.setString(6, employee.getWorkEmail());
            pstmt.setString(7, employee.getPersonalEmail());
            pstmt.setString(8, employee.getWorkPhone());
            pstmt.setString(9, employee.getMobilePhone());
            pstmt.setString(10, employee.getHomeBuildingName());
            pstmt.setString(11, employee.getHomeSuiteNumber());
            pstmt.setString(12, employee.getHomeStreetAddress());
            pstmt.setString(13, employee.getHomeCity());
            pstmt.setString(14, employee.getHomeState());
            pstmt.setString(15, employee.getHomeZIPCode());
            pstmt.setString(16, employee.getHomeCountry());
            pstmt.setString(17, employee.getPositionTitle());
            pstmt.setString(18, employee.getEmploymentType());
            pstmt.setDate(19, Date.valueOf(employee.getHireDate()));
            pstmt.setDate(20, employee.getTerminationDate() != null ? Date.valueOf(employee.getTerminationDate()) : null);
            pstmt.setBoolean(21, employee.getActiveStatus());
            pstmt.setString(22, employee.getEmergencyContactName());
            pstmt.setString(23, employee.getEmergencyContactPhone());
            pstmt.setString(24, employee.getEmergencyContactRelationship());
            pstmt.setString(25, employee.getUsername());
            pstmt.setString(26, employee.getPayType());
            pstmt.setBigDecimal(27, employee.getPayRate());
            pstmt.setInt(28, employee.getEmployeeId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete an employee by ID
     */
    public boolean deleteEmployee(int employeeId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_EMPLOYEE)) {
            
            pstmt.setInt(1, employeeId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Search employees by name, username, or position
     */
    public List<Employee> searchEmployees(String searchTerm) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SEARCH_EMPLOYEES)) {
            
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(extractEmployeeFromResultSet(rs));
                }
            }
        }
        
        return employees;
    }

    /**
     * Helper method to extract an Employee object from a ResultSet
     */
    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("EmployeeID"));
        employee.setFirstName(rs.getString("FirstName"));
        employee.setLastName(rs.getString("LastName"));
        employee.setMiddleInitial(rs.getString("MiddleInitial"));
        
        Date dob = rs.getDate("DateOfBirth");
        if (dob != null) employee.setDateOfBirth(dob.toLocalDate());
        
        employee.setGender(rs.getString("Gender"));
        employee.setWorkEmail(rs.getString("WorkEmail"));
        employee.setPersonalEmail(rs.getString("PersonalEmail"));
        employee.setWorkPhone(rs.getString("WorkPhone"));
        employee.setMobilePhone(rs.getString("MobilePhone"));
        employee.setHomeBuildingName(rs.getString("HomeBuildingName"));
        employee.setHomeSuiteNumber(rs.getString("HomeSuiteNumber"));
        employee.setHomeStreetAddress(rs.getString("HomeStreetAddress"));
        employee.setHomeCity(rs.getString("HomeCity"));
        employee.setHomeState(rs.getString("HomeState"));
        employee.setHomeZIPCode(rs.getString("HomeZIPCode"));
        employee.setHomeCountry(rs.getString("HomeCountry"));
        employee.setPositionTitle(rs.getString("PositionTitle"));
        employee.setEmploymentType(rs.getString("EmploymentType"));
        
        Date hireDate = rs.getDate("HireDate");
        if (hireDate != null) employee.setHireDate(hireDate.toLocalDate());
        
        Date termDate = rs.getDate("TerminationDate");
        if (termDate != null) employee.setTerminationDate(termDate.toLocalDate());
        
        employee.setActiveStatus(rs.getBoolean("ActiveStatus"));
        employee.setEmergencyContactName(rs.getString("EmergencyContactName"));
        employee.setEmergencyContactPhone(rs.getString("EmergencyContactPhone"));
        employee.setEmergencyContactRelationship(rs.getString("EmergencyContactRelationship"));
        employee.setUsername(rs.getString("Username"));
        employee.setPasswordHash(rs.getString("PasswordHash"));
        employee.setPayType(rs.getString("PayType"));
        employee.setPayRate(rs.getBigDecimal("PayRate"));
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) employee.setCreatedAt(createdAt.toLocalDateTime());
        
        Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
        if (updatedAt != null) employee.setUpdatedAt(updatedAt.toLocalDateTime());
        
        return employee;
    }
}
