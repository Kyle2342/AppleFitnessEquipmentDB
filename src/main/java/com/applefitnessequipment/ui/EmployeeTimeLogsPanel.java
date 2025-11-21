package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

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
    private JTextField logDateField, timeInFirstField, timeOutFirstField;
    private JTextField timeInSecondField, timeOutSecondField;
    private JTextField totalHoursField, milesField, ptoHoursField;
    private JButton addButton, updateButton, deleteButton, clearButton, filterButton;
    private EmployeeTimeLog selectedTimeLog;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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

        // Top Panel - Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Employee:"));
        filterEmployeeCombo = new JComboBox<>();
        filterEmployeeCombo.addItem(null);
        filterPanel.add(filterEmployeeCombo);
        filterButton = new JButton("Filter");
        filterButton.addActionListener(e -> filterTimeLogsByEmployee());
        filterPanel.add(filterButton);
        JButton refreshButton = new JButton("Show All");
        refreshButton.addActionListener(e -> loadTimeLogs());
        filterPanel.add(refreshButton);

        add(filterPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"ID", "Employee ID", "Day", "Date", "Time In", "Time Out", "Total Hrs", "Miles", "PTO Hrs"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        timeLogsTable = new JTable(tableModel);
        timeLogsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Apply modern styling
        ModernUIHelper.styleTable(timeLogsTable);
        
        // Hide the ID columns from the user view
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
        
        // Allow deselection by clicking on empty space
        timeLogsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = timeLogsTable.rowAtPoint(e.getPoint());
                if (row == -1) {
                    clearForm();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(timeLogsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Right Panel - Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(ModernUIHelper.createModernBorder("Time Log Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Employee
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Employee:*"), gbc);
        gbc.gridx = 1;
        employeeCombo = new JComboBox<>();
        ModernUIHelper.styleComboBox(employeeCombo);
        formPanel.add(employeeCombo, gbc);
        row++;

        // Day of Week
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Day of Week:*"), gbc);
        gbc.gridx = 1;
        dayOfWeekCombo = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"});
        ModernUIHelper.styleComboBox(dayOfWeekCombo);
        formPanel.add(dayOfWeekCombo, gbc);
        row++;

        // Log Date
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Date:* (yyyy-MM-dd)"), gbc);
        gbc.gridx = 1;
        logDateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        logDateField.setText(LocalDate.now().format(dateFormatter));
        ModernUIHelper.styleTextField(logDateField);
        formPanel.add(logDateField, gbc);
        row++;

        // Time In First
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Time In:* (HH:mm)"), gbc);
        gbc.gridx = 1;
        timeInFirstField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(timeInFirstField);
        formPanel.add(timeInFirstField, gbc);
        row++;

        // Time Out First
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Time Out:* (HH:mm)"), gbc);
        gbc.gridx = 1;
        timeOutFirstField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(timeOutFirstField);
        formPanel.add(timeOutFirstField, gbc);
        row++;

        // Time In Second
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Time In 2: (HH:mm)"), gbc);
        gbc.gridx = 1;
        timeInSecondField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(timeInSecondField);
        formPanel.add(timeInSecondField, gbc);
        row++;

        // Time Out Second
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Time Out 2: (HH:mm)"), gbc);
        gbc.gridx = 1;
        timeOutSecondField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(timeOutSecondField);
        formPanel.add(timeOutSecondField, gbc);
        row++;

        // Total Hours
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Total Hours:"), gbc);
        gbc.gridx = 1;
        totalHoursField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(totalHoursField);
        formPanel.add(totalHoursField, gbc);
        row++;

        // Miles
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Miles:"), gbc);
        gbc.gridx = 1;
        milesField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(milesField);
        formPanel.add(milesField, gbc);
        row++;

        // PTO Hours
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("PTO Hours:"), gbc);
        gbc.gridx = 1;
        ptoHoursField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(ptoHoursField);
        formPanel.add(ptoHoursField, gbc);
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

    private void loadEmployees() {
        try {
            List<Employee> employees = employeeDAO.getAllEmployees();
            employeeCombo.removeAllItems();
            filterEmployeeCombo.removeAllItems();
            filterEmployeeCombo.addItem(null);
            
            for (Employee emp : employees) {
                employeeCombo.addItem(emp);
                filterEmployeeCombo.addItem(emp);
            }
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
                    log.getTimeLogId(),
                    log.getEmployeeId(),
                    log.getDayOfWeek(),
                    log.getLogDate(),
                    log.getTimeInFirst(),
                    log.getTimeOutFirst(),
                    log.getTotalHours(),
                    log.getMiles(),
                    log.getPtoHours()
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
                    log.getTimeLogId(),
                    log.getEmployeeId(),
                    log.getDayOfWeek(),
                    log.getLogDate(),
                    log.getTimeInFirst(),
                    log.getTimeOutFirst(),
                    log.getTotalHours(),
                    log.getMiles(),
                    log.getPtoHours()
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
                    // Find and select the employee
                    for (int i = 0; i < employeeCombo.getItemCount(); i++) {
                        Employee emp = employeeCombo.getItemAt(i);
                        if (emp != null && emp.getEmployeeId().equals(selectedTimeLog.getEmployeeId())) {
                            employeeCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    dayOfWeekCombo.setSelectedItem(selectedTimeLog.getDayOfWeek());
                    logDateField.setText(selectedTimeLog.getLogDate().format(dateFormatter));
                    timeInFirstField.setText(selectedTimeLog.getTimeInFirst().format(timeFormatter));
                    timeOutFirstField.setText(selectedTimeLog.getTimeOutFirst().format(timeFormatter));
                    timeInSecondField.setText(selectedTimeLog.getTimeInSecond() != null ? 
                        selectedTimeLog.getTimeInSecond().format(timeFormatter) : "");
                    timeOutSecondField.setText(selectedTimeLog.getTimeOutSecond() != null ? 
                        selectedTimeLog.getTimeOutSecond().format(timeFormatter) : "");
                    totalHoursField.setText(selectedTimeLog.getTotalHours() != null ? 
                        selectedTimeLog.getTotalHours().toString() : "");
                    milesField.setText(selectedTimeLog.getMiles() != null ? 
                        selectedTimeLog.getMiles().toString() : "");
                    ptoHoursField.setText(selectedTimeLog.getPtoHours() != null ? 
                        selectedTimeLog.getPtoHours().toString() : "");
                    
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
                if (timeLogDAO.deleteTimeLog(selectedTimeLog.getTimeLogId())) {
                    JOptionPane.showMessageDialog(this, "Time log deleted successfully!");
                    clearForm();
                    loadTimeLogs();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete time log.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
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
        log.setTimeInFirst(LocalTime.parse(timeInFirstField.getText().trim(), timeFormatter));
        log.setTimeOutFirst(LocalTime.parse(timeOutFirstField.getText().trim(), timeFormatter));
        
        if (!timeInSecondField.getText().trim().isEmpty()) {
            log.setTimeInSecond(LocalTime.parse(timeInSecondField.getText().trim(), timeFormatter));
        }
        if (!timeOutSecondField.getText().trim().isEmpty()) {
            log.setTimeOutSecond(LocalTime.parse(timeOutSecondField.getText().trim(), timeFormatter));
        }
        if (!totalHoursField.getText().trim().isEmpty()) {
            log.setTotalHours(new BigDecimal(totalHoursField.getText().trim()));
        }
        if (!milesField.getText().trim().isEmpty()) {
            log.setMiles(new BigDecimal(milesField.getText().trim()));
        }
        if (!ptoHoursField.getText().trim().isEmpty()) {
            log.setPtoHours(new BigDecimal(ptoHoursField.getText().trim()));
        }
    }

    private void clearForm() {
        if (employeeCombo.getItemCount() > 0) {
            employeeCombo.setSelectedIndex(0);
        }
        dayOfWeekCombo.setSelectedIndex(0);
        logDateField.setText(LocalDate.now().format(dateFormatter));
        timeInFirstField.setText("");
        timeOutFirstField.setText("");
        timeInSecondField.setText("");
        timeOutSecondField.setText("");
        totalHoursField.setText("");
        milesField.setText("");
        ptoHoursField.setText("");
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

        try {
            LocalDate.parse(logDateField.getText().trim(), dateFormatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            LocalTime.parse(timeInFirstField.getText().trim(), timeFormatter);
            LocalTime.parse(timeOutFirstField.getText().trim(), timeFormatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Use HH:mm (e.g., 08:30).",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }
}
