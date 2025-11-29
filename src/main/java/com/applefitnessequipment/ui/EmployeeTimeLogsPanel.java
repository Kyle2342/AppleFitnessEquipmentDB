package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

import com.applefitnessequipment.dao.EmployeeDAO;
import com.applefitnessequipment.dao.EmployeeTimeLogDAO;
import com.applefitnessequipment.model.Employee;
import com.applefitnessequipment.model.EmployeeTimeLog;

public class EmployeeTimeLogsPanel extends JPanel {

    private EmployeeTimeLogDAO timeLogDAO;
    private EmployeeDAO employeeDAO;
    private JTable timeLogsTable;
    private DefaultTableModel tableModel;
    private JComboBox<Employee> employeeCombo, filterEmployeeCombo;
    private JComboBox<String> dayOfWeekCombo;
    private JFormattedTextField logDateField, timeInField, timeOutField;
    private JTextField totalHoursField, milesField;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private EmployeeTimeLog selectedTimeLog;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public EmployeeTimeLogsPanel() {
        timeLogDAO = new EmployeeTimeLogDAO();
        employeeDAO = new EmployeeDAO();
        initComponents();
        loadEmployees();
        loadTimeLogs();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ============================================================
        // Top Panel - Filter
        // ============================================================
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Employee:"));

        filterEmployeeCombo = new JComboBox<>();
        filterEmployeeCombo.addItem(null);

        // White background + renderer
        filterEmployeeCombo.setBackground(Color.WHITE);
        filterEmployeeCombo.setOpaque(true);
        filterEmployeeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                if (value == null) {
                    value = "Show All";
                }
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (!isSelected) {
                    comp.setBackground(Color.WHITE);
                }
                return comp;
            }
        });
        filterEmployeeCombo.setFocusable(false);

        filterEmployeeCombo.addActionListener(e -> filterTimeLogsByEmployee());  // Auto-filter on change
        ModernUIHelper.styleComboBox(filterEmployeeCombo);
        filterPanel.add(filterEmployeeCombo);

        add(filterPanel, BorderLayout.NORTH);

        // ============================================================
        // Center Panel - Table
        // ============================================================
        String[] columns = {"ID", "Employee", "Day", "Date", "Time In", "Time Out", "Total Hrs", "Miles"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        timeLogsTable = new JTable(tableModel);
        timeLogsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ModernUIHelper.styleTable(timeLogsTable);
        ModernUIHelper.addTableToggleBehavior(timeLogsTable, this::clearForm);

        // Hide ID + Employee technical column
        timeLogsTable.getColumnModel().getColumn(0).setMinWidth(0);
        timeLogsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        timeLogsTable.getColumnModel().getColumn(0).setWidth(0);
        timeLogsTable.getColumnModel().getColumn(1).setMinWidth(0);
        timeLogsTable.getColumnModel().getColumn(1).setMaxWidth(0);
        timeLogsTable.getColumnModel().getColumn(1).setWidth(0);

        timeLogsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = timeLogsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadSelectedTimeLog();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(timeLogsTable);

        scrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.getComponent() == scrollPane && !timeLogsTable.getBounds().contains(e.getPoint())) {
                    timeLogsTable.clearSelection();
                    clearForm();
                }
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        // ============================================================
        // Right Panel - Form
        // ============================================================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(ModernUIHelper.createModernBorder("Employee Time Log Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Standard size like invoices
        JTextField sizingField = new JTextField(20);
        ModernUIHelper.styleTextField(sizingField);
        Dimension standardInputSize = sizingField.getPreferredSize();

        int row = 0;



        // Employee
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Employee:*"), gbc);
        gbc.gridx = 1;
        employeeCombo = new JComboBox<>();
        employeeCombo.setPreferredSize(standardInputSize);

        employeeCombo.setBackground(Color.WHITE);
        employeeCombo.setOpaque(true);
        employeeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (!isSelected) {
                    comp.setBackground(Color.WHITE);
                }
                return comp;
            }
        });
        // Remove dashed focus rectangle
        employeeCombo.setFocusable(false);

        ModernUIHelper.styleComboBox(employeeCombo);
        formPanel.add(employeeCombo, gbc);
        row++;

        // Day of Week
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Day of Week:*"), gbc);
        gbc.gridx = 1;
        dayOfWeekCombo = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"});
        dayOfWeekCombo.setPreferredSize(standardInputSize);

        dayOfWeekCombo.setBackground(Color.WHITE);
        dayOfWeekCombo.setOpaque(true);
        dayOfWeekCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (!isSelected) {
                    comp.setBackground(Color.WHITE);
                }
                return comp;
            }
        });
        dayOfWeekCombo.setFocusable(false);

        ModernUIHelper.styleComboBox(dayOfWeekCombo);
        formPanel.add(dayOfWeekCombo, gbc);
        row++;

        // Log Date
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Date:* (MM/dd/yyyy)"), gbc);
        gbc.gridx = 1;
        logDateField = createMaskedField("##/##/####");
        logDateField.setPreferredSize(standardInputSize);
        logDateField.setText(LocalDate.now().format(dateFormatter));
        formPanel.add(logDateField, gbc);
        row++;

        // Time In
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Time In:* (HH:mm)"), gbc);
        gbc.gridx = 1;
        timeInField = createMaskedField("##:##");
        timeInField.setPreferredSize(standardInputSize);
        timeInField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateTotalHoursDisplay(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateTotalHoursDisplay(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTotalHoursDisplay(); }
        });
        formPanel.add(timeInField, gbc);
        row++;

        // Time Out
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Time Out:* (HH:mm)"), gbc);
        gbc.gridx = 1;
        timeOutField = createMaskedField("##:##");
        timeOutField.setPreferredSize(standardInputSize);
        timeOutField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateTotalHoursDisplay(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateTotalHoursDisplay(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTotalHoursDisplay(); }
        });
        formPanel.add(timeOutField, gbc);
        row++;

        // Total Hours
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Total Hours:"), gbc);
        gbc.gridx = 1;
        totalHoursField = new JTextField();
        totalHoursField.setPreferredSize(standardInputSize);
        totalHoursField.setEditable(false);
        totalHoursField.setFocusable(false);  // Prevent highlighting when clicked
        ModernUIHelper.styleTextField(totalHoursField);
        totalHoursField.setBackground(new Color(240, 240, 240));  // Light gray to indicate auto-generated
        formPanel.add(totalHoursField, gbc);
        row++;

        // Miles
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Miles:"), gbc);
        gbc.gridx = 1;
        milesField = new JTextField();
        milesField.setPreferredSize(standardInputSize);
        ModernUIHelper.styleTextField(milesField);
        formPanel.add(milesField, gbc);
        row++;

        // Buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());

        addButton = new JButton("Add");
        addButton.addActionListener(e -> addTimeLog());
        ModernUIHelper.styleButton(addButton, "success");
        buttonPanel.add(addButton);

        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateTimeLog());
        updateButton.setEnabled(false);
        ModernUIHelper.styleButton(updateButton, "warning");
        buttonPanel.add(updateButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteTimeLog());
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

    // Section label helper
    private void addSectionLabel(JPanel panel, GridBagConstraints gbc, int row, String text) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;

        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(java.awt.Font.BOLD, 13f));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 0, 5, 0)
        ));
        panel.add(label, gbc);

        gbc.gridwidth = 1;
    }

    // Helper for masked fields
    private JFormattedTextField createMaskedField(String mask) {
        try {
            MaskFormatter formatter = new MaskFormatter(mask);
            formatter.setPlaceholderCharacter('_');
            JFormattedTextField field = new JFormattedTextField(formatter);
            ModernUIHelper.styleTextField(field);
            return field;
        } catch (ParseException e) {
            JFormattedTextField field = new JFormattedTextField();
            ModernUIHelper.styleTextField(field);
            return field;
        }
    }

    // Auto-update Total Hours field as user types TimeIn/TimeOut
    private void updateTotalHoursDisplay() {
        String timeInText = timeInField.getText().trim();
        String timeOutText = timeOutField.getText().trim();

        // Check if both fields have valid time format (HH:mm)
        if (timeInText.length() == 5 && timeOutText.length() == 5 &&
            !timeInText.contains("_") && !timeOutText.contains("_")) {
            try {
                LocalTime timeIn = LocalTime.parse(timeInText, timeFormatter);
                LocalTime timeOut = LocalTime.parse(timeOutText, timeFormatter);

                if (timeOut.isAfter(timeIn)) {
                    // Calculate hours using the same logic as the model
                    EmployeeTimeLog tempLog = new EmployeeTimeLog();
                    tempLog.setTimeIn(timeIn);
                    tempLog.setTimeOut(timeOut);
                    tempLog.recalculateTotalHours();

                    if (tempLog.getTotalHours() != null) {
                        totalHoursField.setText(tempLog.getTotalHours().toString());
                    } else {
                        totalHoursField.setText("");
                    }
                } else {
                    totalHoursField.setText("");
                }
            } catch (DateTimeParseException e) {
                totalHoursField.setText("");
            }
        } else {
            totalHoursField.setText("");
        }
    }

    private void loadEmployees() {
        try {
            List<Employee> employees = employeeDAO.getAllEmployees();
            employeeCombo.removeAllItems();
            filterEmployeeCombo.removeAllItems();

            // Add "Show All" option first for filter combo
            filterEmployeeCombo.addItem(null);

            for (Employee emp : employees) {
                employeeCombo.addItem(emp);
                filterEmployeeCombo.addItem(emp);
            }

            employeeCombo.setSelectedIndex(-1);
            filterEmployeeCombo.setSelectedIndex(0);  // Default to "Show All"
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading employees: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTimeLogs() {
        try {
            List<EmployeeTimeLog> logs = timeLogDAO.getAllTimeLogs();
            tableModel.setRowCount(0);
            for (EmployeeTimeLog log : logs) {
                tableModel.addRow(new Object[]{
                    log.getEmployeeTimeLogId(),
                    getEmployeeDisplayName(log.getEmployeeId()),
                    log.getDayOfWeek(),
                    log.getLogDate() != null ? log.getLogDate().format(dateFormatter) : "",
                    log.getTimeIn() != null ? log.getTimeIn().format(timeFormatter) : "",
                    log.getTimeOut() != null ? log.getTimeOut().format(timeFormatter) : "",
                    log.getTotalHours(),
                    log.getMiles()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading time logs: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTimeLogsByEmployee() {
        Employee selectedEmployee = (Employee) filterEmployeeCombo.getSelectedItem();
        if (selectedEmployee == null) {
            loadTimeLogs();
            return;
        }

        try {
            List<EmployeeTimeLog> logs = timeLogDAO.getTimeLogsByEmployeeId(selectedEmployee.getEmployeeId());
            tableModel.setRowCount(0);
            for (EmployeeTimeLog log : logs) {
                tableModel.addRow(new Object[]{
                    log.getEmployeeTimeLogId(),
                    getEmployeeDisplayName(log.getEmployeeId()),
                    log.getDayOfWeek(),
                    log.getLogDate() != null ? log.getLogDate().format(dateFormatter) : "",
                    log.getTimeIn() != null ? log.getTimeIn().format(timeFormatter) : "",
                    log.getTimeOut() != null ? log.getTimeOut().format(timeFormatter) : "",
                    log.getTotalHours(),
                    log.getMiles()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error filtering time logs: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedTimeLog() {
        int selectedRow = timeLogsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int timeLogId = (Integer) tableModel.getValueAt(selectedRow, 0);
            try {
                selectedTimeLog = timeLogDAO.getTimeLogById(timeLogId);
                if (selectedTimeLog != null) {
                    for (int i = 0; i < employeeCombo.getItemCount(); i++) {
                        Employee emp = employeeCombo.getItemAt(i);
                        if (emp != null && emp.getEmployeeId().equals(selectedTimeLog.getEmployeeId())) {
                            employeeCombo.setSelectedIndex(i);
                            break;
                        }
                    }

                    dayOfWeekCombo.setSelectedItem(selectedTimeLog.getDayOfWeek());
                    logDateField.setText(selectedTimeLog.getLogDate().format(dateFormatter));
                    timeInField.setText(selectedTimeLog.getTimeIn().format(timeFormatter));
                    timeOutField.setText(selectedTimeLog.getTimeOut().format(timeFormatter));
                    totalHoursField.setText(selectedTimeLog.getTotalHours() != null ?
                        selectedTimeLog.getTotalHours().toString() : "");
                    milesField.setText(selectedTimeLog.getMiles() != null ?
                        selectedTimeLog.getMiles().toString() : "");

                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading time log details: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addTimeLog() {
        if (!validateForm()) return;

        EmployeeTimeLog log = new EmployeeTimeLog();
        populateTimeLogFromForm(log);

        try {
            if (timeLogDAO.addTimeLog(log)) {
                JOptionPane.showMessageDialog(this, "Time log added successfully!");
                clearForm();
                loadTimeLogs();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add time log.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding time log: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTimeLog() {
        if (selectedTimeLog == null) return;
        if (!validateForm()) return;

        populateTimeLogFromForm(selectedTimeLog);

        try {
            if (timeLogDAO.updateTimeLog(selectedTimeLog)) {
                JOptionPane.showMessageDialog(this, "Time log updated successfully!");
                clearForm();
                loadTimeLogs();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update time log.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating time log: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTimeLog() {
        if (selectedTimeLog == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this time log?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (timeLogDAO.deleteTimeLog(selectedTimeLog.getEmployeeTimeLogId())) {
                    JOptionPane.showMessageDialog(this, "Time log deleted successfully!");
                    clearForm();
                    loadTimeLogs();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete time log.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting time log: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void populateTimeLogFromForm(EmployeeTimeLog log) {
        Employee selectedEmployee = (Employee) employeeCombo.getSelectedItem();
        log.setEmployeeId(selectedEmployee.getEmployeeId());
        log.setDayOfWeek((String) dayOfWeekCombo.getSelectedItem());
        log.setLogDate(LocalDate.parse(logDateField.getText().trim(), dateFormatter));
        log.setTimeIn(LocalTime.parse(timeInField.getText().trim(), timeFormatter));
        log.setTimeOut(LocalTime.parse(timeOutField.getText().trim(), timeFormatter));

        if (!milesField.getText().trim().isEmpty()) {
            log.setMiles(new BigDecimal(milesField.getText().trim()));
        } else {
            log.setMiles(null);
        }

        log.recalculateTotalHours();
        if (log.getTotalHours() != null) {
            totalHoursField.setText(log.getTotalHours().toString());
        } else {
            totalHoursField.setText("");
        }
    }

    private void clearForm() {
        employeeCombo.setSelectedIndex(-1);
        dayOfWeekCombo.setSelectedIndex(0);
        logDateField.setText(LocalDate.now().format(dateFormatter));
        timeInField.setText("");
        timeOutField.setText("");
        totalHoursField.setText("");
        milesField.setText("");
        selectedTimeLog = null;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        timeLogsTable.clearSelection();
    }

    private boolean validateForm() {
        if (employeeCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(logDateField.getText().trim(), dateFormatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use MM/dd/yyyy.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            LocalTime timeIn = LocalTime.parse(timeInField.getText().trim(), timeFormatter);
            LocalTime timeOut = LocalTime.parse(timeOutField.getText().trim(), timeFormatter);

            if (!timeOut.isAfter(timeIn)) {
                JOptionPane.showMessageDialog(this, "Time Out must be after Time In.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Use HH:mm (e.g., 08:30).",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (parsedDate.isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Log date cannot be in the future.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String expectedDay =
            parsedDate.getDayOfWeek().toString().substring(0, 1).toUpperCase() +
            parsedDate.getDayOfWeek().toString().substring(1).toLowerCase();
        if (!expectedDay.equals(dayOfWeekCombo.getSelectedItem())) {
            JOptionPane.showMessageDialog(this, "Day of Week must match the selected date.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!milesField.getText().trim().isEmpty()) {
            try {
                BigDecimal miles = new BigDecimal(milesField.getText().trim());
                if (miles.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this, "Miles cannot be negative.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid miles value.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        return true;
    }

    public void refreshData() {
        loadEmployees();
        loadTimeLogs();
    }

    private String getEmployeeDisplayName(Integer employeeId) {
        if (employeeId == null) return "";
        for (int i = 0; i < employeeCombo.getItemCount(); i++) {
            Employee emp = employeeCombo.getItemAt(i);
            if (emp != null && employeeId.equals(emp.getEmployeeId())) {
                return emp.toString();
            }
        }
        return "Employee " + employeeId;
    }
}
