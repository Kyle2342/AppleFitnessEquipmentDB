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
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.applefitnessequipment.dao.EmployeeDAO;
import com.applefitnessequipment.model.Employee;

public class EmployeesPanel extends JPanel {
    private static final String DATE_PATTERN = "MM/dd/yyyy";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private final EmployeeDAO employeeDAO;

    private JTable employeesTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> activeFilterCombo;
    private JComboBox<String> typeFilterCombo;
    private JComboBox<String> payTypeFilterCombo;
    private JTextField firstNameField, lastNameField;
    private JTextField dobField, emailField;
    private JTextField phoneNumberField;
    private JTextField buildingNameField, suiteNumberField;
    private JTextField streetAddressField, cityField, stateField, zipField, countryField;
    private JTextField positionField, hireDateField, terminationDateField;
    private JTextField payRateField;
    private JComboBox<String> genderCombo, employmentTypeCombo, payTypeCombo;
    private JCheckBox activeCheckBox;
    private JButton addButton, updateButton, deleteButton, clearButton;

    private Employee selectedEmployee;
    private List<Employee> allEmployees;

    // flag to prevent recursive updates in phone formatting
    private boolean isUpdatingPhone = false;
    // flag to prevent recursive updates in DOB formatting
    private boolean isUpdatingDob = false;

    public EmployeesPanel() {
        this.employeeDAO = new EmployeeDAO();
        initComponents();
        loadEmployees();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===================== TOP: SEARCH AND FILTERS =====================
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(25);
        searchField.setFont(ModernUIHelper.NORMAL_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterEmployees(); }
            public void removeUpdate(DocumentEvent e) { filterEmployees(); }
            public void insertUpdate(DocumentEvent e) { filterEmployees(); }
        });
        searchPanel.add(searchField);

        searchPanel.add(Box.createHorizontalStrut(20));

        // Filter by Active
        searchPanel.add(new JLabel("Filter by Employee:"));
        activeFilterCombo = new JComboBox<>(new String[]{"Active Only", "Inactive Only", "Show All"});
        activeFilterCombo.setFont(ModernUIHelper.NORMAL_FONT);
        activeFilterCombo.setPreferredSize(new java.awt.Dimension(135, 38));
        activeFilterCombo.setSelectedIndex(0);  // Default to "Active Only"
        activeFilterCombo.addActionListener(e -> filterEmployees());
        searchPanel.add(activeFilterCombo);

        // Filter by Type
        searchPanel.add(new JLabel("Filter by Type:"));
        typeFilterCombo = new JComboBox<>(new String[]{"Show All", "Full-Time", "Part-Time"});
        typeFilterCombo.setFont(ModernUIHelper.NORMAL_FONT);
        typeFilterCombo.setPreferredSize(new java.awt.Dimension(135, 38));
        typeFilterCombo.addActionListener(e -> filterEmployees());
        searchPanel.add(typeFilterCombo);

        // Filter by Pay Type
        searchPanel.add(new JLabel("Filter by Pay Type:"));
        payTypeFilterCombo = new JComboBox<>(new String[]{"Show All", "Hourly", "Salary"});
        payTypeFilterCombo.setFont(ModernUIHelper.NORMAL_FONT);
        payTypeFilterCombo.setPreferredSize(new java.awt.Dimension(135, 38));
        payTypeFilterCombo.addActionListener(e -> filterEmployees());
        searchPanel.add(payTypeFilterCombo);

        add(searchPanel, BorderLayout.NORTH);

        // ===================== CENTER: TABLE =====================
        String[] columns = {"ID", "Name", "Phone", "Email", "Position", "Type", "Active", "Address", "Pay Type", "Pay Rate"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        employeesTable = new JTable(tableModel);
        employeesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ModernUIHelper.styleTable(employeesTable);
        ModernUIHelper.addTableToggleBehavior(employeesTable, this::clearForm);

        // hide ID column visually
        employeesTable.getColumnModel().getColumn(0).setMinWidth(0);
        employeesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        employeesTable.getColumnModel().getColumn(0).setWidth(0);

        // Set column widths based on content
        // Column 2: Phone - max 14 characters "(###) ###-####"
        employeesTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        employeesTable.getColumnModel().getColumn(2).setMaxWidth(130);

        // Column 5: Type - max 9 characters "Full-Time" or "Part-Time"
        employeesTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        employeesTable.getColumnModel().getColumn(5).setMaxWidth(90);

        // Column 6: Active - max 3 characters "Yes" or "No"
        employeesTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        employeesTable.getColumnModel().getColumn(6).setMaxWidth(70);

        // Column 8: Pay Type - max 6 characters "Hourly" or "Salary"
        employeesTable.getColumnModel().getColumn(8).setPreferredWidth(80);
        employeesTable.getColumnModel().getColumn(8).setMaxWidth(90);

        employeesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = employeesTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadSelectedEmployee();
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(employeesTable);

        // Optional: clicking empty space clears selection
        tableScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                // If click is outside table bounds, clear selection
                if (!employeesTable.getBounds().contains(e.getPoint())) {
                    employeesTable.clearSelection();
                    clearForm();
                }
            }
        });

        add(tableScrollPane, BorderLayout.CENTER);

        // ===================== RIGHT: FORM =====================
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

        // DOB
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("DOB (" + DATE_PATTERN + "):"), gbc);
        gbc.gridx = 1;
        dobField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(dobField);
        setupDobFormatting(dobField);
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

        // Email
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(emailField);
        formPanel.add(emailField, gbc);
        row++;

        // Phone Number
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        phoneNumberField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(phoneNumberField);
        setupPhoneFormatting(phoneNumberField);
        formPanel.add(phoneNumberField, gbc);
        row++;

        // Building Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Building Name:"), gbc);
        gbc.gridx = 1;
        buildingNameField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(buildingNameField);
        formPanel.add(buildingNameField, gbc);
        row++;

        // Suite Number
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Suite Number:"), gbc);
        gbc.gridx = 1;
        suiteNumberField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(suiteNumberField);
        formPanel.add(suiteNumberField, gbc);
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
        formPanel.add(new JLabel("Hire Date:* (" + DATE_PATTERN + ")"), gbc);
        gbc.gridx = 1;
        hireDateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        hireDateField.setText(LocalDate.now().format(dateFormatter));
        ModernUIHelper.styleTextField(hireDateField);
        setupDobFormatting(hireDateField);
        formPanel.add(hireDateField, gbc);
        row++;

        // Termination Date
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Termination Date: (" + DATE_PATTERN + ")"), gbc);
        gbc.gridx = 1;
        terminationDateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(terminationDateField);
        setupDobFormatting(terminationDateField);
        formPanel.add(terminationDateField, gbc);
        row++;

        // Active
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Active:*"), gbc);
        gbc.gridx = 1;
        activeCheckBox = new JCheckBox();
        activeCheckBox.setSelected(true);
        formPanel.add(activeCheckBox, gbc);
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

    // ===================== PHONE FORMATTING =====================

    /**
     * Setup phone number formatting: displays as (###) ###-#### but saves as digits only.
     */
    private void setupPhoneFormatting(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {

                if (isUpdatingPhone) {
                    super.replace(fb, offset, length, text, attrs);
                    return;
                }

                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text +
                                 currentText.substring(offset + length);

                String digitsOnly = newText.replaceAll("[^0-9]", "");
                if (digitsOnly.length() > 10) {
                    return;
                }

                String formatted = formatPhoneNumber(digitsOnly);

                isUpdatingPhone = true;
                fb.remove(0, fb.getDocument().getLength());
                fb.insertString(0, formatted, attrs);
                isUpdatingPhone = false;
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                if (isUpdatingPhone) {
                    super.remove(fb, offset, length);
                    return;
                }

                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) +
                                 currentText.substring(offset + length);

                String digitsOnly = newText.replaceAll("[^0-9]", "");
                String formatted = formatPhoneNumber(digitsOnly);

                isUpdatingPhone = true;
                fb.remove(0, fb.getDocument().getLength());
                fb.insertString(0, formatted, null);
                isUpdatingPhone = false;
            }
        });
    }

    /**
     * Format phone number: 1234567890 -> (123) 456-7890
     */
    private String formatPhoneNumber(String digits) {
        if (digits.length() == 0) return "";
        if (digits.length() <= 3) return "(" + digits;
        if (digits.length() <= 6) return "(" + digits.substring(0, 3) + ") " + digits.substring(3);
        return "(" + digits.substring(0, 3) + ") " + digits.substring(3, 6) + "-" + digits.substring(6);
    }

    /**
     * Extract just the digits from a formatted phone number.
     */
    private String getPhoneDigits(String formatted) {
        return formatted.replaceAll("[^0-9]", "");
    }

    // ===================== DOB FORMATTING =====================

    /**
     * Setup DOB formatting: auto-inserts slashes as MM/dd/yyyy while keeping raw value parsable.
     */
    private void setupDobFormatting(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                applyDobFormatting(fb, offset, length, text, attrs);
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                applyDobFormatting(fb, offset, length, "", null);
            }

            private void applyDobFormatting(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (isUpdatingDob) {
                    super.replace(fb, offset, length, text, attrs);
                    return;
                }

                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String next = current.substring(0, offset) + (text == null ? "" : text) +
                              current.substring(offset + length);
                String digits = next.replaceAll("[^0-9]", "");
                if (digits.length() > 8) {
                    digits = digits.substring(0, 8);
                }

                String formatted = formatDobDigits(digits);
                isUpdatingDob = true;
                fb.remove(0, fb.getDocument().getLength());
                fb.insertString(0, formatted, attrs);
                isUpdatingDob = false;
            }
        });
    }

    /**
     * Convert up to 8 digits into MM/dd/yyyy-style string.
     */
    private String formatDobDigits(String digits) {
        if (digits.isEmpty()) return "";
        if (digits.length() <= 2) return digits;
        if (digits.length() <= 4) {
            return digits.substring(0, 2) + "/" + digits.substring(2);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(digits.substring(0, 2)).append("/");
        sb.append(digits.substring(2, 4)).append("/");
        sb.append(digits.substring(4));
        return sb.toString();
    }

    // ===================== DATA LOADING / FILTERING =====================

    private void loadEmployees() {
        try {
            allEmployees = employeeDAO.getAllEmployees();
            filterEmployees();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading employees: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterEmployees() {
        if (allEmployees == null) return;

        String searchTerm = searchField.getText().trim().toLowerCase();
        String activeFilter = (String) activeFilterCombo.getSelectedItem();
        String typeFilter = (String) typeFilterCombo.getSelectedItem();
        String payTypeFilter = (String) payTypeFilterCombo.getSelectedItem();

        tableModel.setRowCount(0);

        for (Employee emp : allEmployees) {
            // Apply search filter
            boolean searchMatches =
                searchTerm.isEmpty() ||
                (emp.getFirstName() != null && emp.getFirstName().toLowerCase().contains(searchTerm)) ||
                (emp.getLastName() != null && emp.getLastName().toLowerCase().contains(searchTerm)) ||
                (emp.getPositionTitle() != null && emp.getPositionTitle().toLowerCase().contains(searchTerm)) ||
                (emp.getEmail() != null && emp.getEmail().toLowerCase().contains(searchTerm));

            // Apply active filter
            boolean activeMatches = false;
            if ("Active Only".equals(activeFilter)) {
                activeMatches = emp.isActive();
            } else if ("Inactive Only".equals(activeFilter)) {
                activeMatches = !emp.isActive();
            } else { // Show All
                activeMatches = true;
            }

            // Apply type filter
            boolean typeMatches = "Show All".equals(typeFilter) ||
                                  typeFilter.equals(emp.getEmploymentType());

            // Apply pay type filter
            boolean payTypeMatches = "Show All".equals(payTypeFilter) ||
                                     payTypeFilter.equals(emp.getPayType());

            if (searchMatches && activeMatches && typeMatches && payTypeMatches) {
                String fullName = emp.getFullName() != null
                    ? emp.getFullName()
                    : ( (emp.getFirstName() != null ? emp.getFirstName() : "") + " " +
                        (emp.getLastName() != null ? emp.getLastName() : "") ).trim();

                String phone = emp.getPhoneNumber() != null && !emp.getPhoneNumber().isEmpty()
                    ? formatPhoneNumber(emp.getPhoneNumber())
                    : "";

                String email = emp.getEmail() != null ? emp.getEmail() : "";

                String activeDisplay = emp.isActive() ? "Yes" : "No";

                // Format address as: Street Address, City, State ZIP Code, Country
                String address = formatAddress(emp);

                String payType = emp.getPayType() != null ? emp.getPayType() : "";
                String payRate = emp.getPayRate() != null ? emp.getPayRate().toString() : "";

                tableModel.addRow(new Object[]{
                    emp.getEmployeeId(),
                    fullName,
                    phone,
                    email,
                    emp.getPositionTitle(),
                    emp.getEmploymentType(),
                    activeDisplay,
                    address,
                    payType,
                    payRate
                });
            }
        }
    }

    private String formatAddress(Employee emp) {
        StringBuilder sb = new StringBuilder();

        String street = emp.getStreetAddress();
        if (street != null && !street.trim().isEmpty()) {
            sb.append(street.trim());
        }

        String city = emp.getCity();
        if (city != null && !city.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city.trim());
        }

        String state = emp.getState();
        String zip = emp.getZipCode();
        if ((state != null && !state.trim().isEmpty()) || (zip != null && !zip.trim().isEmpty())) {
            if (sb.length() > 0) sb.append(", ");
            if (state != null && !state.trim().isEmpty()) {
                sb.append(state.trim());
            }
            if (zip != null && !zip.trim().isEmpty()) {
                if (state != null && !state.trim().isEmpty()) sb.append(" ");
                sb.append(zip.trim());
            }
        }

        String country = emp.getCountry();
        if (country != null && !country.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country.trim());
        }

        return sb.toString();
    }

    private void loadSelectedEmployee() {
        int selectedRow = employeesTable.getSelectedRow();
        if (selectedRow < 0) return;

        int employeeId = (Integer) tableModel.getValueAt(selectedRow, 0);
        try {
            selectedEmployee = employeeDAO.getEmployeeById(employeeId);
            if (selectedEmployee == null) return;

            firstNameField.setText(nvl(selectedEmployee.getFirstName()));
            lastNameField.setText(nvl(selectedEmployee.getLastName()));

            LocalDate dob = selectedEmployee.getDateOfBirth();
            dobField.setText(dob != null ? dob.format(dateFormatter) : "");

            String gender = selectedEmployee.getGender();
            if (gender != null) {
                genderCombo.setSelectedItem(gender);
            } else {
                genderCombo.setSelectedIndex(0);
            }

            emailField.setText(nvl(selectedEmployee.getEmail()));

            // Phone: DB stores digits; format for display
            String phoneNumber = selectedEmployee.getPhoneNumber();
            phoneNumberField.setText(phoneNumber != null && !phoneNumber.isEmpty() ? formatPhoneNumber(phoneNumber) : "");

            buildingNameField.setText(nvl(selectedEmployee.getBuildingName()));
            suiteNumberField.setText(nvl(selectedEmployee.getSuiteNumber()));
            streetAddressField.setText(nvl(selectedEmployee.getStreetAddress()));
            cityField.setText(nvl(selectedEmployee.getCity()));
            stateField.setText(nvl(selectedEmployee.getState()));
            zipField.setText(nvl(selectedEmployee.getZipCode()));
            countryField.setText(nvl(selectedEmployee.getCountry()));

            positionField.setText(nvl(selectedEmployee.getPositionTitle()));
            employmentTypeCombo.setSelectedItem(selectedEmployee.getEmploymentType());

            LocalDate hireDate = selectedEmployee.getHireDate();
            hireDateField.setText(hireDate != null ? hireDate.format(dateFormatter) : "");

            LocalDate termDate = selectedEmployee.getTerminationDate();
            terminationDateField.setText(termDate != null ? termDate.format(dateFormatter) : "");

            activeCheckBox.setSelected(selectedEmployee.isActive());

            payTypeCombo.setSelectedItem(selectedEmployee.getPayType());
            payRateField.setText(selectedEmployee.getPayRate() != null
                ? selectedEmployee.getPayRate().toString()
                : "");

            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading employee details: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===================== CRUD ACTIONS =====================

    private void addEmployee() {
        if (!validateForm()) return;

        Employee employee = new Employee();
        populateEmployeeFromForm(employee);

        try {
            if (employeeDAO.addEmployee(employee)) {
                JOptionPane.showMessageDialog(this, "Employee added successfully!");
                clearForm();
                loadEmployees();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to add employee.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error adding employee: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        if (selectedEmployee == null) return;
        if (!validateForm()) return;

        populateEmployeeFromForm(selectedEmployee);

        try {
            if (employeeDAO.updateEmployee(selectedEmployee)) {
                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
                clearForm();
                loadEmployees();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update employee.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error updating employee: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        if (selectedEmployee == null) return;

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this employee?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (employeeDAO.deleteEmployee(selectedEmployee.getEmployeeId())) {
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                    clearForm();
                    loadEmployees();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete employee.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting employee: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ===================== FORM HELPERS =====================

    private void populateEmployeeFromForm(Employee employee) {
        employee.setFirstName(firstNameField.getText().trim());
        employee.setLastName(lastNameField.getText().trim());

        // DOB
        String dobText = dobField.getText().trim();
        employee.setDateOfBirth(parseDateOrNull(dobText));

        String gender = (String) genderCombo.getSelectedItem();
        employee.setGender(gender);

        employee.setEmail(emailField.getText().trim());

        // Save only digits for phone number
        employee.setPhoneNumber(getPhoneDigits(phoneNumberField.getText()));

        employee.setBuildingName(buildingNameField.getText().trim());
        employee.setSuiteNumber(suiteNumberField.getText().trim());
        employee.setStreetAddress(streetAddressField.getText().trim());
        employee.setCity(cityField.getText().trim());
        employee.setState(stateField.getText().trim());
        employee.setZipCode(zipField.getText().trim());
        employee.setCountry(countryField.getText().trim());

        employee.setPositionTitle(positionField.getText().trim());
        employee.setEmploymentType((String) employmentTypeCombo.getSelectedItem());

        // hire date is required (validated beforehand)
        employee.setHireDate(LocalDate.parse(hireDateField.getText().trim(), dateFormatter));

        // termination date optional
        String termText = terminationDateField.getText().trim();
        employee.setTerminationDate(parseDateOrNull(termText));

        employee.setActiveStatus(activeCheckBox.isSelected());
        employee.setPayType((String) payTypeCombo.getSelectedItem());

        String payRateText = payRateField.getText().trim();
        employee.setPayRate(payRateText.isEmpty() ? null : new BigDecimal(payRateText));
    }

    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        dobField.setText("");
        genderCombo.setSelectedIndex(0);
        emailField.setText("");
        phoneNumberField.setText("");
        buildingNameField.setText("");
        suiteNumberField.setText("");
        streetAddressField.setText("");
        cityField.setText("");
        stateField.setText("PA");
        zipField.setText("");
        countryField.setText("USA");
        positionField.setText("");
        employmentTypeCombo.setSelectedIndex(0);
        hireDateField.setText(LocalDate.now().format(dateFormatter));
        terminationDateField.setText("");
        activeCheckBox.setSelected(true);
        payTypeCombo.setSelectedIndex(0);
        payRateField.setText("");
        selectedEmployee = null;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        employeesTable.clearSelection();
    }

    private boolean validateForm() {
        if (firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "First name and last name are required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (streetAddressField.getText().trim().isEmpty() ||
            cityField.getText().trim().isEmpty() ||
            stateField.getText().trim().isEmpty() ||
            zipField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Complete address is required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (positionField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Position title is required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            LocalDate.parse(hireDateField.getText().trim(), dateFormatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid hire date format. Use " + DATE_PATTERN + ".",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            BigDecimal payRate = new BigDecimal(payRateField.getText().trim());
            if (payRate.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this,
                    "Pay rate cannot be negative.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid pay rate. Please enter a valid number.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // termination date (if present) must be >= hire date
        if (!terminationDateField.getText().trim().isEmpty()) {
            try {
                LocalDate hireDate = LocalDate.parse(hireDateField.getText().trim(), dateFormatter);
                LocalDate termDate = LocalDate.parse(terminationDateField.getText().trim(), dateFormatter);
                if (termDate.isBefore(hireDate)) {
                    JOptionPane.showMessageDialog(this,
                        "Termination date cannot be before hire date.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this,
                    "Invalid termination date format. Use " + DATE_PATTERN + ".",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        return true;
    }

    private LocalDate parseDateOrNull(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(text.trim(), dateFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String nvl(String value) {
        return value == null ? "" : value;
    }

    public void refreshData() {
        loadEmployees();
    }
}
