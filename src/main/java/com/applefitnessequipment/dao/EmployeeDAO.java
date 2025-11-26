package com.applefitnessequipment.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.Employee;

public class EmployeeDAO {
    // Column order matches table definition (minus EmployeeID)
    private static final String INSERT_EMPLOYEE =
        "INSERT INTO Employees (" +
            "FirstName, LastName, DateOfBirth, Gender, Email, PhoneNumber, " +
            "BuildingName, SuiteNumber, StreetAddress, City, State, ZIPCode, Country, " +
            "PositionTitle, EmploymentType, PayType, PayRate, " +
            "HireDate, TerminationDate, ActiveStatus" +
        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String BASE_SELECT_EMPLOYEE =
        "SELECT " +
            "EmployeeID, FirstName, LastName, DateOfBirth, Gender, Email, PhoneNumber, " +
            "BuildingName, SuiteNumber, StreetAddress, City, State, ZIPCode, Country, " +
            "PositionTitle, EmploymentType, PayType, PayRate, " +
            "HireDate, TerminationDate, ActiveStatus " +
        "FROM Employees ";

    private static final String SELECT_ALL_EMPLOYEES =
        BASE_SELECT_EMPLOYEE + "ORDER BY EmployeeID DESC";

    private static final String SELECT_EMPLOYEE_BY_ID =
        BASE_SELECT_EMPLOYEE + "WHERE EmployeeID = ?";

    private static final String UPDATE_EMPLOYEE =
        "UPDATE Employees SET " +
            "FirstName = ?, LastName = ?, DateOfBirth = ?, Gender = ?, Email = ?, PhoneNumber = ?, " +
            "BuildingName = ?, SuiteNumber = ?, StreetAddress = ?, City = ?, State = ?, ZIPCode = ?, Country = ?, " +
            "PositionTitle = ?, EmploymentType = ?, PayType = ?, PayRate = ?, " +
            "HireDate = ?, TerminationDate = ?, ActiveStatus = ? " +
        "WHERE EmployeeID = ?";

    private static final String DELETE_EMPLOYEE =
        "DELETE FROM Employees WHERE EmployeeID = ?";

    private static final String SEARCH_EMPLOYEES =
        BASE_SELECT_EMPLOYEE +
        "WHERE FirstName LIKE ? " +
        "   OR LastName LIKE ? " +
        "   OR PositionTitle LIKE ? " +
        "ORDER BY EmployeeID DESC";

    // =========================================================
    // PUBLIC METHODS
    // =========================================================

    /**
     * Add a new employee to the database.
     */
    public boolean addEmployee(Employee employee) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_EMPLOYEE, Statement.RETURN_GENERATED_KEYS)) {

            int idx = 1;

            // Personal info
            pstmt.setString(idx++, employee.getFirstName());
            pstmt.setString(idx++, employee.getLastName());
            setLocalDateOrNull(pstmt, idx++, employee.getDateOfBirth());
            pstmt.setString(idx++, employee.getGender());
            pstmt.setString(idx++, employee.getEmail());
            pstmt.setString(idx++, employee.getPhoneNumber());

            // Address
            pstmt.setString(idx++, employee.getBuildingName());
            pstmt.setString(idx++, employee.getSuiteNumber());
            pstmt.setString(idx++, employee.getStreetAddress());
            pstmt.setString(idx++, employee.getCity());
            pstmt.setString(idx++, employee.getState());
            pstmt.setString(idx++, employee.getZipCode());
            pstmt.setString(idx++, employee.getCountry());

            // Employment details
            pstmt.setString(idx++, employee.getPositionTitle());
            pstmt.setString(idx++, employee.getEmploymentType());
            pstmt.setString(idx++, employee.getPayType());
            pstmt.setBigDecimal(idx++, employee.getPayRate());
            setLocalDateOrNull(pstmt, idx++, employee.getHireDate());
            setLocalDateOrNull(pstmt, idx++, employee.getTerminationDate());
            pstmt.setBoolean(idx++, employee.getActiveStatus() != null ? employee.getActiveStatus() : true);

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
     * Retrieve all employees.
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
     * Retrieve an employee by ID.
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
     * Update an existing employee (does NOT change password).
     */
    public boolean updateEmployee(Employee employee) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_EMPLOYEE)) {

            int idx = 1;

            // Personal info
            pstmt.setString(idx++, employee.getFirstName());
            pstmt.setString(idx++, employee.getLastName());
            setLocalDateOrNull(pstmt, idx++, employee.getDateOfBirth());
            pstmt.setString(idx++, employee.getGender());
            pstmt.setString(idx++, employee.getEmail());
            pstmt.setString(idx++, employee.getPhoneNumber());

            // Address
            pstmt.setString(idx++, employee.getBuildingName());
            pstmt.setString(idx++, employee.getSuiteNumber());
            pstmt.setString(idx++, employee.getStreetAddress());
            pstmt.setString(idx++, employee.getCity());
            pstmt.setString(idx++, employee.getState());
            pstmt.setString(idx++, employee.getZipCode());
            pstmt.setString(idx++, employee.getCountry());

            // Employment details
            pstmt.setString(idx++, employee.getPositionTitle());
            pstmt.setString(idx++, employee.getEmploymentType());
            pstmt.setString(idx++, employee.getPayType());
            pstmt.setBigDecimal(idx++, employee.getPayRate());
            setLocalDateOrNull(pstmt, idx++, employee.getHireDate());
            setLocalDateOrNull(pstmt, idx++, employee.getTerminationDate());
            pstmt.setBoolean(idx++, employee.getActiveStatus() != null ? employee.getActiveStatus() : true);

            // WHERE EmployeeID = ?
            pstmt.setInt(idx, employee.getEmployeeId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete an employee by ID.
     */
    public boolean deleteEmployee(int employeeId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_EMPLOYEE)) {

            pstmt.setInt(1, employeeId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Search employees by first name, last name, or position title.
     */
    public List<Employee> searchEmployees(String searchTerm) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String pattern = "%" + searchTerm + "%";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SEARCH_EMPLOYEES)) {

            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(extractEmployeeFromResultSet(rs));
                }
            }
        }

        return employees;
    }


    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    private void setLocalDateOrNull(PreparedStatement ps, int index, LocalDate date) throws SQLException {
        if (date == null) {
            ps.setDate(index, null);
        } else {
            ps.setDate(index, Date.valueOf(date));
        }
    }

    /**
     * Build an Employee object from a ResultSet row.
     */
    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee employee = new Employee();

        employee.setEmployeeId(rs.getInt("EmployeeID"));

        // Personal info
        employee.setFirstName(rs.getString("FirstName"));
        employee.setLastName(rs.getString("LastName"));

        Date dob = rs.getDate("DateOfBirth");
        if (dob != null) {
            employee.setDateOfBirth(dob.toLocalDate());
        }

        employee.setGender(rs.getString("Gender"));

        // Contact info
        employee.setEmail(rs.getString("Email"));
        employee.setPhoneNumber(rs.getString("PhoneNumber"));

        // Address
        employee.setBuildingName(rs.getString("BuildingName"));
        employee.setSuiteNumber(rs.getString("SuiteNumber"));
        employee.setStreetAddress(rs.getString("StreetAddress"));
        employee.setCity(rs.getString("City"));
        employee.setState(rs.getString("State"));
        employee.setZipCode(rs.getString("ZIPCode"));
        employee.setCountry(rs.getString("Country"));

        // Employment details
        employee.setPositionTitle(rs.getString("PositionTitle"));
        employee.setEmploymentType(rs.getString("EmploymentType"));
        employee.setPayType(rs.getString("PayType"));
        employee.setPayRate(rs.getBigDecimal("PayRate"));

        Date hireDate = rs.getDate("HireDate");
        if (hireDate != null) {
            employee.setHireDate(hireDate.toLocalDate());
        }

        Date termDate = rs.getDate("TerminationDate");
        if (termDate != null) {
            employee.setTerminationDate(termDate.toLocalDate());
        }

        employee.setActiveStatus(rs.getBoolean("ActiveStatus"));

        return employee;
    }
}