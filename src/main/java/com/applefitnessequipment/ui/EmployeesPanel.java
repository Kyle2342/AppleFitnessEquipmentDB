package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.applefitnessequipment.dao.EmployeeDAO;
import com.applefitnessequipment.model.Employee;

public class EmployeesPanel extends JPanel {
    private EmployeeDAO employeeDAO;
    private JTable employeesTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField firstNameField, lastNameField, middleInitialField;
    private JTextField dobField, workEmailField, personalEmailField;
    private JTextField workPhoneField, mobilePhoneField;
    private JTextField streetAddressField, cityField, stateField, zipField, countryField;
    private JTextField positionField, hireDateField, usernameField;
    private JPasswordField passwordField;
    private JTextField payRateField;
    private JComboBox<String> genderCombo, employmentTypeCombo, payTypeCombo;
    private JCheckBox activeCheckBox;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private Employee selectedEmployee;
    private List<Employee> allEmployees;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public EmployeesPanel() {
        employeeDAO = new EmployeeDAO();
        initComponents();
        loadEmployees();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Search with auto-search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(35);
        searchField.setFont(ModernUIHelper.NORMAL_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        // Auto-search as user types
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterEmployees(); }
            public void removeUpdate(DocumentEvent e) { filterEmployees(); }
            public void insertUpdate(DocumentEvent e) { filterEmployees(); }
        });
        searchPanel.add(searchField);

        add(searchPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"ID", "Name", "Position", "Type", "Active", "Username", "Hire Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeesTable = new JTable(tableModel);
        employeesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Apply modern styling
        ModernUIHelper.styleTable(employeesTable);
        
        // Hide the ID column from the user view
        employeesTable.getColumnModel().getColumn(0).setMinWidth(0);
        employeesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        employeesTable.getColumnModel().getColumn(0).setWidth(0);
        
        employeesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = employeesTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadSelectedEmployee();
                }
            }
        });
        
        // Allow deselection by clicking on empty space
        employeesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = employeesTable.rowAtPoint(e.getPoint());
                if (row == -1) {
                    clearForm();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(employeesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Right Panel - Form (Scrollable)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(ModernUIHelper.createModernBorder("Employee Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // First Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("First Name:*"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(firstNameField);
        formPanel.add(firstNameField, gbc);
        row++;

        // Last Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Last Name:*"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(lastNameField);
        formPanel.add(lastNameField, gbc);
        row++;

        // Middle Initial
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Middle Initial:"), gbc);
        gbc.gridx = 1;
        middleInitialField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(middleInitialField);
        formPanel.add(middleInitialField, gbc);
        row++;

        // Date of Birth
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("DOB (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        dobField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(dobField);
        formPanel.add(dobField, gbc);
        row++;

        // Gender
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
        ModernUIHelper.styleComboBox(genderCombo);
        formPanel.add(genderCombo, gbc);
        row++;

        // Work Email
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Work Email:"), gbc);
        gbc.gridx = 1;
        workEmailField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(workEmailField);
        formPanel.add(workEmailField, gbc);
        row++;

        // Personal Email
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Personal Email:"), gbc);
        gbc.gridx = 1;
        personalEmailField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(personalEmailField);
        formPanel.add(personalEmailField, gbc);
        row++;

        // Work Phone
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Work Phone:"), gbc);
        gbc.gridx = 1;
        workPhoneField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(workPhoneField);
        formPanel.add(workPhoneField, gbc);
        row++;

        // Mobile Phone
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Mobile Phone:"), gbc);
        gbc.gridx = 1;
        mobilePhoneField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(mobilePhoneField);
        formPanel.add(mobilePhoneField, gbc);
        row++;

        // Street Address
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Street Address:*"), gbc);
        gbc.gridx = 1;
        streetAddressField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(streetAddressField);
        formPanel.add(streetAddressField, gbc);
        row++;

        // City
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("City:*"), gbc);
        gbc.gridx = 1;
        cityField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(cityField);
        formPanel.add(cityField, gbc);
        row++;

        // State
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("State:*"), gbc);
        gbc.gridx = 1;
        stateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        stateField.setText("PA");
        ModernUIHelper.styleTextField(stateField);
        formPanel.add(stateField, gbc);
        row++;

        // ZIP
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("ZIP Code:*"), gbc);
        gbc.gridx = 1;
        zipField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(zipField);
        formPanel.add(zipField, gbc);
        row++;

        // Country
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Country:*"), gbc);
        gbc.gridx = 1;
        countryField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        countryField.setText("USA");
        ModernUIHelper.styleTextField(countryField);
        formPanel.add(countryField, gbc);
        row++;

        // Position Title
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Position:*"), gbc);
        gbc.gridx = 1;
        positionField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(positionField);
        formPanel.add(positionField, gbc);
        row++;

        // Employment Type
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Employment Type:*"), gbc);
        gbc.gridx = 1;
        employmentTypeCombo = new JComboBox<>(new String[]{"Full-Time", "Part-Time"});
        ModernUIHelper.styleComboBox(employmentTypeCombo);
        formPanel.add(employmentTypeCombo, gbc);
        row++;

        // Hire Date
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Hire Date:* (yyyy-MM-dd)"), gbc);
        gbc.gridx = 1;
        hireDateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        hireDateField.setText(LocalDate.now().format(dateFormatter));
        ModernUIHelper.styleTextField(hireDateField);
        formPanel.add(hireDateField, gbc);
        row++;

        // Active Status
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Active:"), gbc);
        gbc.gridx = 1;
        activeCheckBox = new JCheckBox();
        activeCheckBox.setSelected(true);
        formPanel.add(activeCheckBox, gbc);
        row++;

        // Username
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Username:*"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(usernameField);
        formPanel.add(usernameField, gbc);
        row++;

        // Password
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Password:*"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        formPanel.add(passwordField, gbc);
        row++;

        // Pay Type
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Pay Type:*"), gbc);
        gbc.gridx = 1;
        payTypeCombo = new JComboBox<>(new String[]{"Hourly", "Salary"});
        ModernUIHelper.styleComboBox(payTypeCombo);
        formPanel.add(payTypeCombo, gbc);
        row++;

        // Pay Rate
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Pay Rate:*"), gbc);
        gbc.gridx = 1;
        payRateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(payRateField);
        formPanel.add(payRateField, gbc);
        row++;

        // Buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        addButton = new JButton("Add");
        addButton.addActionListener(e -> addEmployee());
        ModernUIHelper.styleButton(addButton, "success");
        buttonPanel.add(addButton);

        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateEmployee());
        updateButton.setEnabled(false);
        ModernUIHelper.styleButton(updateButton, "warning");
        buttonPanel.add(updateButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteEmployee());
        deleteButton.setEnabled(false);
        ModernUIHelper.styleButton(deleteButton, "danger");
        buttonPanel.add(deleteButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());
        ModernUIHelper.styleButton(clearButton, "secondary");
        buttonPanel.add(clearButton);

        formPanel.add(buttonPanel, gbc);

        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(formScrollPane, BorderLayout.EAST);
    }

    private void loadEmployees() {
        try {
            allEmployees = employeeDAO.getAllEmployees();
            filterEmployees();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading employees: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterEmployees() {
        if (allEmployees == null) return;

        String searchTerm = searchField.getText().trim().toLowerCase();

        tableModel.setRowCount(0);
        for (Employee emp : allEmployees) {
            // Apply search filter
            boolean searchMatches = searchTerm.isEmpty() ||
                (emp.getFirstName() != null && emp.getFirstName().toLowerCase().contains(searchTerm)) ||
                (emp.getLastName() != null && emp.getLastName().toLowerCase().contains(searchTerm)) ||
                (emp.getPositionTitle() != null && emp.getPositionTitle().toLowerCase().contains(searchTerm)) ||
                (emp.getUsername() != null && emp.getUsername().toLowerCase().contains(searchTerm)) ||
                (emp.getWorkEmail() != null && emp.getWorkEmail().toLowerCase().contains(searchTerm));

            if (searchMatches) {
                tableModel.addRow(new Object[]{
                    emp.getEmployeeId(),
                    emp.getFirstName() + " " + emp.getLastName(),
                    emp.getPositionTitle(),
                    emp.getEmploymentType(),
                    emp.getActiveStatus() ? "Yes" : "No",
                    emp.getUsername(),
                    emp.getHireDate()
                });
            }
        }
    }

    private void loadSelectedEmployee() {
        int selectedRow = employeesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int employeeId = (Integer) tableModel.getValueAt(selectedRow, 0);
            try {
                selectedEmployee = employeeDAO.getEmployeeById(employeeId);
                if (selectedEmployee != null) {
                    firstNameField.setText(selectedEmployee.getFirstName());
                    lastNameField.setText(selectedEmployee.getLastName());
                    middleInitialField.setText(selectedEmployee.getMiddleInitial());
                    dobField.setText(selectedEmployee.getDateOfBirth() != null ? 
                        selectedEmployee.getDateOfBirth().format(dateFormatter) : "");
                    genderCombo.setSelectedItem(selectedEmployee.getGender());
                    workEmailField.setText(selectedEmployee.getWorkEmail());
                    personalEmailField.setText(selectedEmployee.getPersonalEmail());
                    workPhoneField.setText(selectedEmployee.getWorkPhone());
                    mobilePhoneField.setText(selectedEmployee.getMobilePhone());
                    streetAddressField.setText(selectedEmployee.getHomeStreetAddress());
                    cityField.setText(selectedEmployee.getHomeCity());
                    stateField.setText(selectedEmployee.getHomeState());
                    zipField.setText(selectedEmployee.getHomeZIPCode());
                    countryField.setText(selectedEmployee.getHomeCountry());
                    positionField.setText(selectedEmployee.getPositionTitle());
                    employmentTypeCombo.setSelectedItem(selectedEmployee.getEmploymentType());
                    hireDateField.setText(selectedEmployee.getHireDate().format(dateFormatter));
                    activeCheckBox.setSelected(selectedEmployee.getActiveStatus());
                    usernameField.setText(selectedEmployee.getUsername());
                    passwordField.setText(""); // Don't show password
                    payTypeCombo.setSelectedItem(selectedEmployee.getPayType());
                    payRateField.setText(selectedEmployee.getPayRate().toString());
                    
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading employee details: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addEmployee() {
        if (!validateForm(true)) return;

        Employee employee = new Employee();
        populateEmployeeFromForm(employee);

        try {
            String password = new String(passwordField.getPassword());
            if (employeeDAO.addEmployee(employee, password)) {
                JOptionPane.showMessageDialog(this, "Employee added successfully!");
                clearForm();
                loadEmployees();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add employee.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding employee: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        if (selectedEmployee == null) return;
        if (!validateForm(false)) return;

        populateEmployeeFromForm(selectedEmployee);

        try {
            if (employeeDAO.updateEmployee(selectedEmployee)) {
                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
                clearForm();
                loadEmployees();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update employee.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating employee: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        if (selectedEmployee == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this employee?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (employeeDAO.deleteEmployee(selectedEmployee.getEmployeeId())) {
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                    clearForm();
                    loadEmployees();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete employee.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting employee: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void populateEmployeeFromForm(Employee employee) {
        employee.setFirstName(firstNameField.getText().trim());
        employee.setLastName(lastNameField.getText().trim());
        employee.setMiddleInitial(middleInitialField.getText().trim());
        
        try {
            if (!dobField.getText().trim().isEmpty()) {
                employee.setDateOfBirth(LocalDate.parse(dobField.getText().trim(), dateFormatter));
            }
        } catch (DateTimeParseException e) {
            employee.setDateOfBirth(null);
        }
        
        employee.setGender((String) genderCombo.getSelectedItem());
        employee.setWorkEmail(workEmailField.getText().trim());
        employee.setPersonalEmail(personalEmailField.getText().trim());
        employee.setWorkPhone(workPhoneField.getText().trim());
        employee.setMobilePhone(mobilePhoneField.getText().trim());
        employee.setHomeStreetAddress(streetAddressField.getText().trim());
        employee.setHomeCity(cityField.getText().trim());
        employee.setHomeState(stateField.getText().trim());
        employee.setHomeZIPCode(zipField.getText().trim());
        employee.setHomeCountry(countryField.getText().trim());
        employee.setPositionTitle(positionField.getText().trim());
        employee.setEmploymentType((String) employmentTypeCombo.getSelectedItem());
        employee.setHireDate(LocalDate.parse(hireDateField.getText().trim(), dateFormatter));
        employee.setActiveStatus(activeCheckBox.isSelected());
        employee.setUsername(usernameField.getText().trim());
        employee.setPayType((String) payTypeCombo.getSelectedItem());
        employee.setPayRate(new BigDecimal(payRateField.getText().trim()));
    }

    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        middleInitialField.setText("");
        dobField.setText("");
        genderCombo.setSelectedIndex(0);
        workEmailField.setText("");
        personalEmailField.setText("");
        workPhoneField.setText("");
        mobilePhoneField.setText("");
        streetAddressField.setText("");
        cityField.setText("");
        stateField.setText("PA");
        zipField.setText("");
        countryField.setText("USA");
        positionField.setText("");
        employmentTypeCombo.setSelectedIndex(0);
        hireDateField.setText(LocalDate.now().format(dateFormatter));
        activeCheckBox.setSelected(true);
        usernameField.setText("");
        passwordField.setText("");
        payTypeCombo.setSelectedIndex(0);
        payRateField.setText("");
        selectedEmployee = null;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        employeesTable.clearSelection();
    }

    private boolean validateForm(boolean isNew) {
        if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name and last name are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (streetAddressField.getText().trim().isEmpty() || cityField.getText().trim().isEmpty() ||
            stateField.getText().trim().isEmpty() || zipField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete address is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (positionField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Position title is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            LocalDate.parse(hireDateField.getText().trim(), dateFormatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid hire date format. Use yyyy-MM-dd.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (isNew && passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Password is required for new employees.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            new BigDecimal(payRateField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid pay rate. Please enter a valid number.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }
}
