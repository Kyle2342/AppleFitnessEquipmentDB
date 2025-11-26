package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
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

import com.applefitnessequipment.dao.ClientDAO;
import com.applefitnessequipment.dao.ClientLocationDAO;
import com.applefitnessequipment.dao.EquipmentQuoteDAO;
import com.applefitnessequipment.model.Client;
import com.applefitnessequipment.model.ClientLocation;
import com.applefitnessequipment.model.EquipmentQuote;

/**
 * UI for EquipmentQuotes aligned to applefitnessequipmentdb_schema.sql.
 */
public class EquipmentQuotesPanel extends JPanel {
    private final EquipmentQuoteDAO quoteDAO;
    private final ClientDAO clientDAO;
    private final ClientLocationDAO locationDAO;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private JTable quotesTable;
    private DefaultTableModel tableModel;

    private JComboBox<Client> clientCombo;
    private JComboBox<ClientLocation> billLocationCombo;
    private JComboBox<ClientLocation> jobLocationCombo;
    private JTextField quoteNumberField;
    private JTextField quoteDateField;
    private JComboBox<String> statusCombo;
    private JTextField contactNameField;
    private JTextField salespersonField;
    private JTextField shipViaField;
    private JTextField freightTermsField;
    private JTextField paymentTermsField;
    private JTextField fobField;
    private JTextField subtotalField;
    private JTextField discountField;
    private JTextField freightField;
    private JTextField taxRateField;
    private JTextField extendedTotalField;
    private JTextField taxAmountField;
    private JTextField quoteTotalField;
    private JCheckBox signatureCheckbox;

    private EquipmentQuote selectedQuote;

    public EquipmentQuotesPanel() {
        this.quoteDAO = new EquipmentQuoteDAO();
        this.clientDAO = new ClientDAO();
        this.locationDAO = new ClientLocationDAO();

        initComponents();
        loadClients();
        loadQuotes();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Quote #", "Client", "Date", "Status", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        quotesTable = new JTable(tableModel);
        quotesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernUIHelper.styleTable(quotesTable);
        ModernUIHelper.addTableToggleBehavior(quotesTable, this::clearForm);

        quotesTable.getColumnModel().getColumn(0).setMinWidth(0);
        quotesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        quotesTable.getColumnModel().getColumn(0).setWidth(0);

        quotesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && quotesTable.getSelectedRow() >= 0) {
                loadSelectedQuote();
            }
        });

        JScrollPane tableScroll = new JScrollPane(quotesTable);
        add(tableScroll, BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(ModernUIHelper.createModernBorder("Quote Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        // Client info
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Client:*"), gbc);
        gbc.gridx = 1;
        clientCombo = new JComboBox<>();
        clientCombo.addActionListener(e -> loadLocationsForClient());
        ModernUIHelper.styleComboBox(clientCombo);
        formPanel.add(clientCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Billing Location:*"), gbc);
        gbc.gridx = 1;
        billLocationCombo = new JComboBox<>();
        ModernUIHelper.styleComboBox(billLocationCombo);
        formPanel.add(billLocationCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Job Location:*"), gbc);
        gbc.gridx = 1;
        jobLocationCombo = new JComboBox<>();
        ModernUIHelper.styleComboBox(jobLocationCombo);
        formPanel.add(jobLocationCombo, gbc);
        row++;

        // Quote details
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Quote #*"), gbc);
        gbc.gridx = 1;
        quoteNumberField = new JTextField(20);
        ModernUIHelper.styleTextField(quoteNumberField);
        formPanel.add(quoteNumberField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Quote Date (MM/dd/yyyy):*"), gbc);
        gbc.gridx = 1;
        quoteDateField = new JTextField(20);
        ModernUIHelper.styleTextField(quoteDateField);
        formPanel.add(quoteDateField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Status:*"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Draft", "Sent", "Expired", "Active", "Declined", "Canceled", "Completed"});
        ModernUIHelper.styleComboBox(statusCombo);
        formPanel.add(statusCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Contact Name:*"), gbc);
        gbc.gridx = 1;
        contactNameField = new JTextField(20);
        ModernUIHelper.styleTextField(contactNameField);
        formPanel.add(contactNameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Salesperson:*"), gbc);
        gbc.gridx = 1;
        salespersonField = new JTextField(20);
        ModernUIHelper.styleTextField(salespersonField);
        formPanel.add(salespersonField, gbc);
        row++;

        // Shipping & payment
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Ship Via:*"), gbc);
        gbc.gridx = 1;
        shipViaField = new JTextField(20);
        ModernUIHelper.styleTextField(shipViaField);
        formPanel.add(shipViaField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Freight Terms:*"), gbc);
        gbc.gridx = 1;
        freightTermsField = new JTextField(20);
        ModernUIHelper.styleTextField(freightTermsField);
        formPanel.add(freightTermsField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Payment Terms:*"), gbc);
        gbc.gridx = 1;
        paymentTermsField = new JTextField(20);
        ModernUIHelper.styleTextField(paymentTermsField);
        formPanel.add(paymentTermsField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("FOB Location:*"), gbc);
        gbc.gridx = 1;
        fobField = new JTextField(20);
        ModernUIHelper.styleTextField(fobField);
        formPanel.add(fobField, gbc);
        row++;

        // Amounts
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Subtotal:*"), gbc);
        gbc.gridx = 1;
        subtotalField = new JTextField(20);
        ModernUIHelper.styleTextField(subtotalField);
        formPanel.add(subtotalField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Total Discount:"), gbc);
        gbc.gridx = 1;
        discountField = new JTextField(20);
        ModernUIHelper.styleTextField(discountField);
        formPanel.add(discountField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Freight:"), gbc);
        gbc.gridx = 1;
        freightField = new JTextField(20);
        ModernUIHelper.styleTextField(freightField);
        formPanel.add(freightField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Tax Rate %:*"), gbc);
        gbc.gridx = 1;
        taxRateField = new JTextField(20);
        ModernUIHelper.styleTextField(taxRateField);
        formPanel.add(taxRateField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Extended Total (calc):"), gbc);
        gbc.gridx = 1;
        extendedTotalField = new JTextField(20);
        extendedTotalField.setEditable(false);
        extendedTotalField.setBackground(java.awt.Color.WHITE);
        ModernUIHelper.styleTextField(extendedTotalField);
        formPanel.add(extendedTotalField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Sales Tax (calc):"), gbc);
        gbc.gridx = 1;
        taxAmountField = new JTextField(20);
        taxAmountField.setEditable(false);
        taxAmountField.setBackground(java.awt.Color.WHITE);
        ModernUIHelper.styleTextField(taxAmountField);
        formPanel.add(taxAmountField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Quote Total (calc):"), gbc);
        gbc.gridx = 1;
        quoteTotalField = new JTextField(20);
        quoteTotalField.setEditable(false);
        quoteTotalField.setBackground(java.awt.Color.WHITE);
        ModernUIHelper.styleTextField(quoteTotalField);
        formPanel.add(quoteTotalField, gbc);
        row++;

        // Signature
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Client Signed:"), gbc);
        gbc.gridx = 1;
        signatureCheckbox = new JCheckBox();
        formPanel.add(signatureCheckbox, gbc);
        row++;

        // Buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addQuote());
        ModernUIHelper.styleButton(addButton, "success");
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateQuote());
        ModernUIHelper.styleButton(updateButton, "warning");
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteQuote());
        ModernUIHelper.styleButton(deleteButton, "danger");
        buttonPanel.add(deleteButton);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());
        ModernUIHelper.styleButton(clearButton, "secondary");
        buttonPanel.add(clearButton);

        formPanel.add(buttonPanel, gbc);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(formScroll, BorderLayout.EAST);

        DocumentListener calcListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { recalculateDerivedTotals(); }
            public void removeUpdate(DocumentEvent e) { recalculateDerivedTotals(); }
            public void changedUpdate(DocumentEvent e) { recalculateDerivedTotals(); }
        };
        subtotalField.getDocument().addDocumentListener(calcListener);
        discountField.getDocument().addDocumentListener(calcListener);
        freightField.getDocument().addDocumentListener(calcListener);
        taxRateField.getDocument().addDocumentListener(calcListener);

        clearForm();
    }

    private void loadClients() {
        try {
            List<Client> clients = clientDAO.getAllClients();
            clientCombo.removeAllItems();
            for (Client client : clients) {
                clientCombo.addItem(client);
            }
            clientCombo.setSelectedIndex(-1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading clients: " + ex.getMessage());
        }
    }

    private void loadLocationsForClient() {
        Client client = (Client) clientCombo.getSelectedItem();
        billLocationCombo.removeAllItems();
        jobLocationCombo.removeAllItems();

        if (client == null) return;

        try {
            List<ClientLocation> locations = locationDAO.getLocationsByClientId(client.getClientId());
            for (ClientLocation loc : locations) {
                if ("Billing".equalsIgnoreCase(loc.getLocationType())) {
                    billLocationCombo.addItem(loc);
                }
                if ("Job".equalsIgnoreCase(loc.getLocationType())) {
                    jobLocationCombo.addItem(loc);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading locations: " + ex.getMessage());
        }
    }

    private void loadQuotes() {
        try {
            List<EquipmentQuote> quotes = quoteDAO.getAllQuotes();
            tableModel.setRowCount(0);
            for (EquipmentQuote quote : quotes) {
                tableModel.addRow(new Object[]{
                    quote.getEquipmentQuoteId(),
                    quote.getQuoteNumber(),
                    getClientName(quote.getClientId()),
                    quote.getQuoteDate() != null ? quote.getQuoteDate().format(dateFormatter) : "",
                    quote.getStatus(),
                    quote.getQuoteTotalAmount()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading quotes: " + ex.getMessage());
        }
    }

    private void loadSelectedQuote() {
        int row = quotesTable.getSelectedRow();
        if (row < 0) return;

        Integer id = (Integer) tableModel.getValueAt(row, 0);
        try {
            selectedQuote = quoteDAO.getQuoteById(id);
            if (selectedQuote == null) return;

            selectClientById(selectedQuote.getClientId());
            loadLocationsForClient();
            selectLocation(billLocationCombo, selectedQuote.getClientBillingLocationId());
            selectLocation(jobLocationCombo, selectedQuote.getClientJobLocationId());

            quoteNumberField.setText(selectedQuote.getQuoteNumber());
            quoteDateField.setText(selectedQuote.getQuoteDate() != null ? selectedQuote.getQuoteDate().format(dateFormatter) : "");
            statusCombo.setSelectedItem(selectedQuote.getStatus());
            contactNameField.setText(selectedQuote.getContactName());
            salespersonField.setText(selectedQuote.getSalespersonName());
            shipViaField.setText(selectedQuote.getShipVia());
            freightTermsField.setText(selectedQuote.getFreightTerms());
            paymentTermsField.setText(selectedQuote.getPaymentTerms());
            fobField.setText(selectedQuote.getFobLocation());
            subtotalField.setText(valueOrDefault(selectedQuote.getSubtotalAmount(), "0.00"));
            discountField.setText(valueOrDefault(selectedQuote.getTotalDiscountAmount(), "0.00"));
            freightField.setText(valueOrDefault(selectedQuote.getFreightAmount(), "0.00"));
            taxRateField.setText(valueOrDefault(selectedQuote.getSalesTaxRatePercent(), "6.00"));
            signatureCheckbox.setSelected(Boolean.TRUE.equals(selectedQuote.getClientSignatureBoolean()));

            // Generated columns from DB
            extendedTotalField.setText(valueOrDefault(selectedQuote.getExtendedTotalAmount(), ""));
            taxAmountField.setText(valueOrDefault(selectedQuote.getSalesTaxAmount(), ""));
            quoteTotalField.setText(valueOrDefault(selectedQuote.getQuoteTotalAmount(), ""));
            recalculateDerivedTotals();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading quote: " + ex.getMessage());
        }
    }

    private void addQuote() {
        if (!validateForm()) return;

        EquipmentQuote quote = new EquipmentQuote();
        populateFromForm(quote);

        try {
            if (quoteDAO.addQuote(quote)) {
                JOptionPane.showMessageDialog(this, "Quote added successfully!");
                clearForm();
                loadQuotes();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding quote: " + ex.getMessage());
        }
    }

    private void updateQuote() {
        if (selectedQuote == null) return;
        if (!validateForm()) return;

        populateFromForm(selectedQuote);

        try {
            if (quoteDAO.updateQuote(selectedQuote)) {
                JOptionPane.showMessageDialog(this, "Quote updated successfully!");
                clearForm();
                loadQuotes();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating quote: " + ex.getMessage());
        }
    }

    private void deleteQuote() {
        if (selectedQuote == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this quote?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (quoteDAO.deleteQuote(selectedQuote.getEquipmentQuoteId())) {
                JOptionPane.showMessageDialog(this, "Quote deleted successfully!");
                clearForm();
                loadQuotes();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting quote: " + ex.getMessage());
        }
    }

    private void populateFromForm(EquipmentQuote quote) {
        Client client = (Client) clientCombo.getSelectedItem();
        ClientLocation bill = (ClientLocation) billLocationCombo.getSelectedItem();
        ClientLocation job = (ClientLocation) jobLocationCombo.getSelectedItem();

        quote.setClientId(client.getClientId());
        quote.setClientBillingLocationId(bill.getClientLocationId());
        quote.setClientJobLocationId(job.getClientLocationId());
        quote.setQuoteNumber(quoteNumberField.getText().trim());
        quote.setQuoteDate(LocalDate.parse(quoteDateField.getText().trim(), dateFormatter));
        quote.setStatus((String) statusCombo.getSelectedItem());
        quote.setContactName(contactNameField.getText().trim());
        quote.setSalespersonName(salespersonField.getText().trim());
        quote.setShipVia(shipViaField.getText().trim());
        quote.setFreightTerms(freightTermsField.getText().trim());
        quote.setPaymentTerms(paymentTermsField.getText().trim());
        quote.setFobLocation(fobField.getText().trim());
        quote.setSubtotalAmount(new BigDecimal(subtotalField.getText().trim()));
        quote.setTotalDiscountAmount(new BigDecimal(discountField.getText().trim()));
        quote.setFreightAmount(new BigDecimal(freightField.getText().trim()));
        quote.setSalesTaxRatePercent(new BigDecimal(taxRateField.getText().trim()));
        quote.setClientSignatureBoolean(signatureCheckbox.isSelected());
    }

    private boolean validateForm() {
        if (clientCombo.getSelectedItem() == null ||
            billLocationCombo.getSelectedItem() == null ||
            jobLocationCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select client and locations.");
            return false;
        }
        if (quoteNumberField.getText().trim().isEmpty() ||
            quoteDateField.getText().trim().isEmpty() ||
            contactNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Quote number, date, and contact name are required.");
            return false;
        }
        try {
            LocalDate.parse(quoteDateField.getText().trim(), dateFormatter);
            new BigDecimal(subtotalField.getText().trim());
            new BigDecimal(discountField.getText().trim());
            new BigDecimal(freightField.getText().trim());
            new BigDecimal(taxRateField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date or number format. Use MM/dd/yyyy for dates.");
            return false;
        }
        return true;
    }

    private void clearForm() {
        clientCombo.setSelectedIndex(-1);
        billLocationCombo.removeAllItems();
        jobLocationCombo.removeAllItems();
        quoteNumberField.setText("");
        quoteDateField.setText(LocalDate.now().format(dateFormatter));
        statusCombo.setSelectedItem("Draft");
        contactNameField.setText("");
        salespersonField.setText("Greg Bartram");
        shipViaField.setText("AFE Truck/Trailer");
        freightTermsField.setText("Ppd & Add");
        paymentTermsField.setText("See Notes");
        fobField.setText("Truck Curbside");
        subtotalField.setText("0.00");
        discountField.setText("0.00");
        freightField.setText("0.00");
        taxRateField.setText("6.00");
        extendedTotalField.setText("");
        taxAmountField.setText("");
        quoteTotalField.setText("");
        signatureCheckbox.setSelected(false);
        selectedQuote = null;
        quotesTable.clearSelection();
        recalculateDerivedTotals();
    }

    private void recalculateDerivedTotals() {
        try {
            BigDecimal subtotal = new BigDecimal(subtotalField.getText().trim());
            BigDecimal discount = new BigDecimal(discountField.getText().trim());
            BigDecimal freight = new BigDecimal(freightField.getText().trim());
            BigDecimal taxRate = new BigDecimal(taxRateField.getText().trim());

            BigDecimal extendedTotal = subtotal.subtract(discount).add(freight);
            BigDecimal taxAmount = extendedTotal.multiply(taxRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal quoteTotal = extendedTotal.add(taxAmount);

            extendedTotalField.setText(extendedTotal.setScale(2, RoundingMode.HALF_UP).toString());
            taxAmountField.setText(taxAmount.toString());
            quoteTotalField.setText(quoteTotal.setScale(2, RoundingMode.HALF_UP).toString());
        } catch (Exception e) {
            extendedTotalField.setText("");
            taxAmountField.setText("");
            quoteTotalField.setText("");
        }
    }

    private void selectClientById(Integer clientId) {
        if (clientId == null) return;
        for (int i = 0; i < clientCombo.getItemCount(); i++) {
            Client c = clientCombo.getItemAt(i);
            if (c != null && clientId.equals(c.getClientId())) {
                clientCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void selectLocation(JComboBox<ClientLocation> combo, Integer id) {
        if (id == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            ClientLocation loc = combo.getItemAt(i);
            if (loc != null && id.equals(loc.getClientLocationId())) {
                combo.setSelectedIndex(i);
                break;
            }
        }
    }

    private String getClientName(Integer clientId) {
        if (clientId == null) return "Unknown";
        try {
            Client client = clientDAO.getClientById(clientId);
            return client == null ? "Unknown" : client.toString();
        } catch (SQLException e) {
            return "Unknown";
        }
    }

    private String valueOrDefault(BigDecimal value, String defaultValue) {
        return value == null ? defaultValue : value.toString();
    }

    public void refreshData() {
        loadClients();
        loadQuotes();
    }
}
