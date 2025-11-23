package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.applefitnessequipment.dao.ClientDAO;
import com.applefitnessequipment.dao.ClientLocationDAO;
import com.applefitnessequipment.dao.EquipmentQuoteDAO;
import com.applefitnessequipment.dao.InvoiceDAO;
import com.applefitnessequipment.dao.PMAgreementDAO;
import com.applefitnessequipment.model.Client;
import com.applefitnessequipment.model.ClientLocation;
import com.applefitnessequipment.model.EquipmentQuote;
import com.applefitnessequipment.model.Invoice;
import com.applefitnessequipment.model.PreventiveMaintenanceAgreement;

public class InvoiceDialog extends JDialog {
    private InvoiceDAO invoiceDAO;
    private ClientDAO clientDAO;
    private ClientLocationDAO locationDAO;
    private EquipmentQuoteDAO quoteDAO;
    private PMAgreementDAO pmaDAO;
    private Invoice invoice;
    private boolean saved = false;

    // Link to Quote/Agreement
    private JRadioButton noneRadio, equipmentQuoteRadio, pmaRadio;
    private JComboBox<EquipmentQuote> equipmentQuoteCombo;
    private JComboBox<PreventiveMaintenanceAgreement> pmaCombo;

    // Core invoice fields
    private JTextField invoiceNumberField, quoteNumberField, poNumberField;
    private JTextField invoiceDateField, dueDateField, paidDateField;
    private JTextField termsField;
    private JComboBox<String> statusCombo;

    // Money fields
    private JTextField taxRateField, taxAmountField;
    private JTextField totalAmountField, paymentsAppliedField, balanceDueField;

    // Fee and interest fields
    private JTextField returnedCheckFeeField, interestPercentField;
    private JTextField interestStartDaysField, interestIntervalDaysField;

    // Client selection
    private JComboBox<Client> clientCombo;
    private JComboBox<ClientLocation> billLocationCombo, jobLocationCombo;

    private DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private List<Client> allClients;

    public InvoiceDialog(JFrame parent, Invoice invoice) {
        super(parent, invoice == null ? "Create Invoice" : "Edit Invoice", true);
        this.invoice = invoice;
        this.invoiceDAO = new InvoiceDAO();
        this.clientDAO = new ClientDAO();
        this.locationDAO = new ClientLocationDAO();
        this.quoteDAO = new EquipmentQuoteDAO();
        this.pmaDAO = new PMAgreementDAO();

        initComponents();
        loadClients();
        loadQuotesAndAgreements();

        if (invoice != null) {
            populateForm();
        } else {
            setDefaultValues();
        }

        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(700, 850));
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        // ===== LINK TO QUOTE/AGREEMENT =====
        addSectionLabel(formPanel, gbc, row++, "LINK TO EXISTING QUOTE/AGREEMENT (OPTIONAL)");

        // Radio buttons for selection type
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        noneRadio = new JRadioButton("None", true);
        equipmentQuoteRadio = new JRadioButton("Equipment Quote");
        pmaRadio = new JRadioButton("PM Agreement");

        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(noneRadio);
        radioGroup.add(equipmentQuoteRadio);
        radioGroup.add(pmaRadio);

        radioPanel.add(noneRadio);
        radioPanel.add(equipmentQuoteRadio);
        radioPanel.add(pmaRadio);
        formPanel.add(radioPanel, gbc);
        gbc.gridwidth = 1;
        row++;

        // Equipment Quote combo
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Equipment Quote:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        equipmentQuoteCombo = new JComboBox<>();
        equipmentQuoteCombo.setEnabled(false);
        equipmentQuoteCombo.addActionListener(e -> populateFromEquipmentQuote());
        ModernUIHelper.styleComboBox(equipmentQuoteCombo);
        formPanel.add(equipmentQuoteCombo, gbc);
        row++;

        // PMA combo
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("PM Agreement:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        pmaCombo = new JComboBox<>();
        pmaCombo.setEnabled(false);
        pmaCombo.addActionListener(e -> populateFromPMAgreement());
        ModernUIHelper.styleComboBox(pmaCombo);
        formPanel.add(pmaCombo, gbc);
        row++;

        // Radio button listeners to enable/disable combos
        noneRadio.addActionListener(e -> {
            equipmentQuoteCombo.setEnabled(false);
            pmaCombo.setEnabled(false);
        });
        equipmentQuoteRadio.addActionListener(e -> {
            equipmentQuoteCombo.setEnabled(true);
            pmaCombo.setEnabled(false);
            if (equipmentQuoteCombo.getSelectedItem() != null) {
                populateFromEquipmentQuote();
            }
        });
        pmaRadio.addActionListener(e -> {
            equipmentQuoteCombo.setEnabled(false);
            pmaCombo.setEnabled(true);
            if (pmaCombo.getSelectedItem() != null) {
                populateFromPMAgreement();
            }
        });

        // ===== CLIENT SELECTION =====
        addSectionLabel(formPanel, gbc, row++, "CLIENT INFORMATION");

        // Client
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Client:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        clientCombo = new JComboBox<>();
        clientCombo.setEditable(true);

        JTextField textField = (JTextField) clientCombo.getEditor().getEditorComponent();
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER ||
                    e.getKeyCode() == java.awt.event.KeyEvent.VK_TAB) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    String text = textField.getText();
                    filterClientCombo(text);
                });
            }
        });

        clientCombo.addActionListener(e -> {
            if (e != null && clientCombo.getSelectedItem() instanceof Client) {
                loadLocationsForClient();
            }
        });

        ModernUIHelper.styleComboBox(clientCombo);
        formPanel.add(clientCombo, gbc);
        row++;

        // Bill Location
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Bill To Location:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        billLocationCombo = new JComboBox<>();
        ModernUIHelper.styleComboBox(billLocationCombo);
        formPanel.add(billLocationCombo, gbc);
        row++;

        // Job Location
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Job At Location:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        jobLocationCombo = new JComboBox<>();
        ModernUIHelper.styleComboBox(jobLocationCombo);
        formPanel.add(jobLocationCombo, gbc);
        row++;

        // ===== INVOICE DETAILS =====
        addSectionLabel(formPanel, gbc, row++, "INVOICE DETAILS");

        // Invoice Number
        row = addField(formPanel, gbc, row, "Invoice Number:*", invoiceNumberField = new JTextField(20));

        // Quote Number (auto-populated from link)
        quoteNumberField = new JTextField(20);
        quoteNumberField.setEditable(false);
        quoteNumberField.setBackground(java.awt.Color.LIGHT_GRAY);
        row = addField(formPanel, gbc, row, "Quote Number (auto):", quoteNumberField);

        // PO Number
        row = addField(formPanel, gbc, row, "PO Number:", poNumberField = new JTextField(20));

        // Invoice Date
        row = addField(formPanel, gbc, row, "Invoice Date (MM/dd/yyyy):*", invoiceDateField = new JTextField(20));

        // Due Date
        row = addField(formPanel, gbc, row, "Due Date (MM/dd/yyyy):*", dueDateField = new JTextField(20));

        // Terms
        row = addField(formPanel, gbc, row, "Terms:*", termsField = new JTextField(20));

        // Status
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Status:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        statusCombo = new JComboBox<>(new String[]{"Draft", "Open", "Paid", "Overdue", "Void"});
        statusCombo.addActionListener(e -> updatePaidDate());
        ModernUIHelper.styleComboBox(statusCombo);
        formPanel.add(statusCombo, gbc);
        row++;

        // Paid Date
        row = addField(formPanel, gbc, row, "Paid Date (MM/dd/yyyy):", paidDateField = new JTextField(20));

        // ===== FINANCIAL DETAILS =====
        addSectionLabel(formPanel, gbc, row++, "FINANCIAL DETAILS");

        // Tax Rate (not used for auto-calc until invoice items are implemented)
        row = addField(formPanel, gbc, row, "Tax Rate %:*", taxRateField = new JTextField(20));

        // Tax Amount (editable for now, will be auto-calc when items added)
        taxAmountField = new JTextField(20);
        taxAmountField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
        });
        row = addField(formPanel, gbc, row, "Tax Amount:*", taxAmountField);

        // Total Amount (editable for now, will be auto-calc when items added)
        totalAmountField = new JTextField(20);
        totalAmountField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
        });
        row = addField(formPanel, gbc, row, "Total Amount:*", totalAmountField);

        // Payments Applied
        paymentsAppliedField = new JTextField(20);
        paymentsAppliedField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
        });
        row = addField(formPanel, gbc, row, "Payments Applied:*", paymentsAppliedField);

        // Balance Due (auto-calculated, read-only)
        balanceDueField = new JTextField(20);
        balanceDueField.setEditable(false);
        balanceDueField.setBackground(java.awt.Color.LIGHT_GRAY);
        row = addField(formPanel, gbc, row, "Balance Due (auto):", balanceDueField);

        // ===== FEES AND INTEREST =====
        addSectionLabel(formPanel, gbc, row++, "FEES & INTEREST");

        // Returned Check Fee
        row = addField(formPanel, gbc, row, "Returned Check Fee:*", returnedCheckFeeField = new JTextField(20));

        // Interest Percent
        row = addField(formPanel, gbc, row, "Interest Percent:*", interestPercentField = new JTextField(20));

        // Interest Start Days
        row = addField(formPanel, gbc, row, "Interest Start Days:*", interestStartDaysField = new JTextField(20));

        // Interest Interval Days
        row = addField(formPanel, gbc, row, "Interest Interval Days:*", interestIntervalDaysField = new JTextField(20));

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton(invoice == null ? "Create" : "Save");
        saveButton.addActionListener(e -> saveInvoice());
        ModernUIHelper.styleButton(saveButton, "success");
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        ModernUIHelper.styleButton(cancelButton, "secondary");
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadQuotesAndAgreements() {
        try {
            // Load equipment quotes
            List<EquipmentQuote> quotes = quoteDAO.getAllQuotes();
            for (EquipmentQuote quote : quotes) {
                equipmentQuoteCombo.addItem(quote);
            }

            // Load PMA agreements
            List<PreventiveMaintenanceAgreement> agreements = pmaDAO.getAllAgreements();
            for (PreventiveMaintenanceAgreement pma : agreements) {
                pmaCombo.addItem(pma);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading quotes/agreements: " + ex.getMessage());
        }
    }

    private void populateFromEquipmentQuote() {
        EquipmentQuote selectedQuote = (EquipmentQuote) equipmentQuoteCombo.getSelectedItem();
        if (selectedQuote != null) {
            quoteNumberField.setText(selectedQuote.getQuoteNumber());
        }
    }

    private void populateFromPMAgreement() {
        PreventiveMaintenanceAgreement selectedPMA = (PreventiveMaintenanceAgreement) pmaCombo.getSelectedItem();
        if (selectedPMA != null) {
            quoteNumberField.setText(selectedPMA.getAgreementNumber());
        }
    }

    private void addSectionLabel(JPanel panel, GridBagConstraints gbc, int row, String text) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(java.awt.Font.BOLD, 14f));
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        panel.add(label, gbc);
        gbc.gridwidth = 1;
    }

    private int addField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        ModernUIHelper.styleTextField(field);
        panel.add(field, gbc);
        return row + 1;
    }

    private void calculateBalanceDue() {
        try {
            // Calculate balance due = Total Amount - Payments Applied
            // Total Amount stays constant - only balance changes
            String totalText = totalAmountField.getText().trim();
            String paymentsText = paymentsAppliedField.getText().trim();

            if (!totalText.isEmpty() && !paymentsText.isEmpty()) {
                BigDecimal total = new BigDecimal(totalText);
                BigDecimal payments = new BigDecimal(paymentsText);
                BigDecimal balance = total.subtract(payments);
                balanceDueField.setText(balance.toString());
            }
        } catch (Exception e) {
            // Invalid input, ignore
        }
    }

    private void updatePaidDate() {
        String status = (String) statusCombo.getSelectedItem();
        if ("Paid".equals(status) && paidDateField.getText().trim().isEmpty()) {
            paidDateField.setText(LocalDate.now().format(displayDateFormatter));
        }
    }

    private void loadClients() {
        try {
            allClients = clientDAO.getAllClients();
            clientCombo.removeAllItems();
            for (Client client : allClients) {
                clientCombo.addItem(client);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading clients: " + ex.getMessage());
        }
    }

    private void filterClientCombo(String searchText) {
        if (allClients == null) return;

        String search = searchText.toLowerCase().trim();

        java.awt.event.ActionListener[] listeners = clientCombo.getActionListeners();
        for (java.awt.event.ActionListener listener : listeners) {
            clientCombo.removeActionListener(listener);
        }

        clientCombo.removeAllItems();

        for (Client client : allClients) {
            String clientName = client.toString();
            if (search.isEmpty() || clientName.toLowerCase().contains(search)) {
                clientCombo.addItem(client);
            }
        }

        for (java.awt.event.ActionListener listener : listeners) {
            clientCombo.addActionListener(listener);
        }

        JTextField editor = (JTextField) clientCombo.getEditor().getEditorComponent();
        editor.setText(searchText);

        if (!search.isEmpty() && clientCombo.getItemCount() > 0) {
            clientCombo.showPopup();
        }
    }

    private void loadLocationsForClient() {
        Client selected = (Client) clientCombo.getSelectedItem();
        if (selected == null) return;

        try {
            List<ClientLocation> locations = locationDAO.getLocationsByClientId(selected.getClientId());
            billLocationCombo.removeAllItems();
            jobLocationCombo.removeAllItems();
            for (ClientLocation loc : locations) {
                billLocationCombo.addItem(loc);
                jobLocationCombo.addItem(loc);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading locations: " + ex.getMessage());
        }
    }

    private void setDefaultValues() {
        invoiceDateField.setText(LocalDate.now().format(displayDateFormatter));
        dueDateField.setText(LocalDate.now().plusDays(30).format(displayDateFormatter));
        termsField.setText("Net 30");
        statusCombo.setSelectedItem("Draft");
        taxRateField.setText("6.00");
        taxAmountField.setText("0.00");
        totalAmountField.setText("0.00");
        paymentsAppliedField.setText("0.00");
        balanceDueField.setText("0.00");
        returnedCheckFeeField.setText("40.00");
        interestPercentField.setText("10.00");
        interestStartDaysField.setText("90");
        interestIntervalDaysField.setText("30");
    }

    private void populateForm() {
        try {
            // Check which type of quote/agreement is linked
            if (invoice.getEquipmentQuoteId() != null) {
                equipmentQuoteRadio.setSelected(true);
                equipmentQuoteCombo.setEnabled(true);
                // Try to select the linked quote
                for (int i = 0; i < equipmentQuoteCombo.getItemCount(); i++) {
                    EquipmentQuote q = equipmentQuoteCombo.getItemAt(i);
                    if (q.getEquipmentQuoteId() != null && q.getEquipmentQuoteId().equals(invoice.getEquipmentQuoteId())) {
                        equipmentQuoteCombo.setSelectedIndex(i);
                        break;
                    }
                }
            } else if (invoice.getPreventiveMaintenanceAgreementId() != null) {
                pmaRadio.setSelected(true);
                pmaCombo.setEnabled(true);
                // Try to select the linked PMA
                for (int i = 0; i < pmaCombo.getItemCount(); i++) {
                    PreventiveMaintenanceAgreement pma = pmaCombo.getItemAt(i);
                    if (pma.getPmaId() != null && pma.getPmaId().equals(invoice.getPreventiveMaintenanceAgreementId())) {
                        pmaCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }

            // Select client
            boolean clientFound = false;
            if (invoice.getClientId() != null) {
                for (int i = 0; i < clientCombo.getItemCount(); i++) {
                    if (clientCombo.getItemAt(i).getClientId().equals(invoice.getClientId())) {
                        clientCombo.setSelectedIndex(i);
                        clientFound = true;
                        break;
                    }
                }
            }

            if (!clientFound) {
                String storedClientName = "";
                if (invoice.getBillToCompanyName() != null && !invoice.getBillToCompanyName().isEmpty()) {
                    storedClientName = invoice.getBillToCompanyName();
                } else if (invoice.getBillToContactName() != null && !invoice.getBillToContactName().isEmpty()) {
                    storedClientName = invoice.getBillToContactName();
                } else {
                    storedClientName = "Unknown Client";
                }

                JOptionPane.showMessageDialog(this,
                    "Original client no longer exists.\n" +
                    "Stored Bill To: " + storedClientName,
                    "Client Not Found", JOptionPane.INFORMATION_MESSAGE);
            }

            // Load and select locations
            if (clientFound) {
                loadLocationsForClient();
                for (int i = 0; i < billLocationCombo.getItemCount(); i++) {
                    if (billLocationCombo.getItemAt(i).getClientLocationId().equals(invoice.getBillingLocationId())) {
                        billLocationCombo.setSelectedIndex(i);
                        break;
                    }
                }
                for (int i = 0; i < jobLocationCombo.getItemCount(); i++) {
                    if (jobLocationCombo.getItemAt(i).getClientLocationId().equals(invoice.getJobLocationId())) {
                        jobLocationCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }

            invoiceNumberField.setText(invoice.getInvoiceNumber());
            quoteNumberField.setText(invoice.getQuoteNumber() != null ? invoice.getQuoteNumber() : "");
            poNumberField.setText(invoice.getPoNumber() != null ? invoice.getPoNumber() : "");
            invoiceDateField.setText(invoice.getInvoiceDate().format(displayDateFormatter));
            dueDateField.setText(invoice.getDueDate().format(displayDateFormatter));
            termsField.setText(invoice.getTerms());
            statusCombo.setSelectedItem(invoice.getStatus());
            paidDateField.setText(invoice.getPaidDate() != null ? invoice.getPaidDate().format(displayDateFormatter) : "");

            taxRateField.setText(invoice.getTaxRatePercent().toString());
            taxAmountField.setText(invoice.getTaxAmount().toString());
            totalAmountField.setText(invoice.getTotalAmount().toString());
            paymentsAppliedField.setText(invoice.getPaymentsApplied().toString());

            BigDecimal balance = invoice.getTotalAmount().subtract(invoice.getPaymentsApplied());
            balanceDueField.setText(balance.toString());

            returnedCheckFeeField.setText(invoice.getReturnedCheckFee() != null ? invoice.getReturnedCheckFee().toString() : "40.00");
            interestPercentField.setText(invoice.getInterestPercent() != null ? invoice.getInterestPercent().toString() : "10.00");
            interestStartDaysField.setText(invoice.getInterestStartDays() != null ? invoice.getInterestStartDays().toString() : "90");
            interestIntervalDaysField.setText(invoice.getInterestIntervalDays() != null ? invoice.getInterestIntervalDays().toString() : "30");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error populating form: " + ex.getMessage());
        }
    }

    private void saveInvoice() {
        if (!validateForm()) return;

        try {
            if (invoice == null) {
                invoice = new Invoice();
            }

            populateInvoiceFromForm();

            if (invoice.getInvoiceId() == null) {
                if (invoiceDAO.addInvoice(invoice)) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, "Invoice created successfully!");
                    dispose();
                }
            } else {
                if (invoiceDAO.updateInvoice(invoice)) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, "Invoice updated successfully!");
                    dispose();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving invoice: " + ex.getMessage());
        }
    }

    private void populateInvoiceFromForm() {
        Client client = (Client) clientCombo.getSelectedItem();
        ClientLocation billLoc = (ClientLocation) billLocationCombo.getSelectedItem();
        ClientLocation jobLoc = (ClientLocation) jobLocationCombo.getSelectedItem();

        invoice.setClientId(client.getClientId());
        invoice.setBillingLocationId(billLoc.getClientLocationId());
        invoice.setJobLocationId(jobLoc.getClientLocationId());

        // Set quote/agreement FK based on radio selection
        if (equipmentQuoteRadio.isSelected() && equipmentQuoteCombo.getSelectedItem() != null) {
            EquipmentQuote quote = (EquipmentQuote) equipmentQuoteCombo.getSelectedItem();
            invoice.setEquipmentQuoteId(quote.getEquipmentQuoteId());
            invoice.setPreventiveMaintenanceAgreementId(null);
        } else if (pmaRadio.isSelected() && pmaCombo.getSelectedItem() != null) {
            PreventiveMaintenanceAgreement pma = (PreventiveMaintenanceAgreement) pmaCombo.getSelectedItem();
            invoice.setPreventiveMaintenanceAgreementId(pma.getPmaId());
            invoice.setEquipmentQuoteId(null);
        } else {
            invoice.setEquipmentQuoteId(null);
            invoice.setPreventiveMaintenanceAgreementId(null);
        }

        invoice.setInvoiceNumber(invoiceNumberField.getText().trim());
        invoice.setQuoteNumber(quoteNumberField.getText().trim().isEmpty() ? null : quoteNumberField.getText().trim());
        invoice.setPoNumber(poNumberField.getText().trim().isEmpty() ? null : poNumberField.getText().trim());

        invoice.setInvoiceDate(LocalDate.parse(invoiceDateField.getText().trim(), displayDateFormatter));
        invoice.setDueDate(LocalDate.parse(dueDateField.getText().trim(), displayDateFormatter));

        String paidDateText = paidDateField.getText().trim();
        invoice.setPaidDate(paidDateText.isEmpty() ? null : LocalDate.parse(paidDateText, displayDateFormatter));

        invoice.setTerms(termsField.getText().trim());
        invoice.setStatus((String) statusCombo.getSelectedItem());

        // Subtotal will be 0 until invoice items are added
        invoice.setSubtotalAmount(BigDecimal.ZERO);
        invoice.setTaxRatePercent(new BigDecimal(taxRateField.getText().trim()));
        invoice.setTaxAmount(new BigDecimal(taxAmountField.getText().trim()));
        invoice.setTotalAmount(new BigDecimal(totalAmountField.getText().trim()));
        invoice.setPaymentsApplied(new BigDecimal(paymentsAppliedField.getText().trim()));

        invoice.setReturnedCheckFee(new BigDecimal(returnedCheckFeeField.getText().trim()));
        invoice.setInterestPercent(new BigDecimal(interestPercentField.getText().trim()));
        invoice.setInterestStartDays(Integer.parseInt(interestStartDaysField.getText().trim()));
        invoice.setInterestIntervalDays(Integer.parseInt(interestIntervalDaysField.getText().trim()));

        // Capture Client snapshot
        invoice.setClientTypeSnapshot(client.getClientType());
        invoice.setClientCompanyNameSnapshot(client.getCompanyName());
        invoice.setClientFirstNameSnapshot(client.getFirstName());
        invoice.setClientLastNameSnapshot(client.getLastName());

        // Capture BILL TO location snapshot
        invoice.setBillToCompanyName(billLoc.getCompanyName());
        invoice.setBillToContactName(billLoc.getContactName());
        invoice.setBillToStreetAddress(billLoc.getStreetAddress());
        invoice.setBillToBuildingName(billLoc.getBuildingName());
        invoice.setBillToSuite(billLoc.getSuite());
        invoice.setBillToRoomNumber(billLoc.getRoomNumber());
        invoice.setBillToDepartment(billLoc.getDepartment());
        invoice.setBillToCity(billLoc.getCity());
        invoice.setBillToCounty(billLoc.getCounty());
        invoice.setBillToState(billLoc.getState());
        invoice.setBillToZIPCode(billLoc.getZipCode());
        invoice.setBillToCountry(billLoc.getCountry());
        invoice.setBillToPhone(billLoc.getPhone());
        invoice.setBillToPONumber(poNumberField.getText().trim().isEmpty() ? null : poNumberField.getText().trim());

        // Capture JOB AT location snapshot
        invoice.setJobAtCompanyName(jobLoc.getCompanyName());
        invoice.setJobAtContactName(jobLoc.getContactName());
        invoice.setJobAtStreetAddress(jobLoc.getStreetAddress());
        invoice.setJobAtBuildingName(jobLoc.getBuildingName());
        invoice.setJobAtSuite(jobLoc.getSuite());
        invoice.setJobAtRoomNumber(jobLoc.getRoomNumber());
        invoice.setJobAtDepartment(jobLoc.getDepartment());
        invoice.setJobAtCity(jobLoc.getCity());
        invoice.setJobAtCounty(jobLoc.getCounty());
        invoice.setJobAtState(jobLoc.getState());
        invoice.setJobAtZIPCode(jobLoc.getZipCode());
        invoice.setJobAtCountry(jobLoc.getCountry());
        invoice.setJobAtPhone(jobLoc.getPhone());
        invoice.setJobAtPONumber(poNumberField.getText().trim().isEmpty() ? null : poNumberField.getText().trim());

        // FROM company fields - using defaults from database schema
        invoice.setFromCompanyName("Apple Fitness Equipment");
        invoice.setFromStreetAddress("1412 Majestic View Dr.");
        invoice.setFromCity("State College");
        invoice.setFromState("PA");
        invoice.setFromZIPCode("16801");
        invoice.setFromPhone("8148262922");
        invoice.setFromFax("8148262933");
    }

    private boolean validateForm() {
        if (clientCombo.getSelectedItem() == null ||
            billLocationCombo.getSelectedItem() == null ||
            jobLocationCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select client and locations.");
            return false;
        }
        if (invoiceNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invoice number is required.");
            return false;
        }
        if (invoiceDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invoice date is required.");
            return false;
        }
        try {
            LocalDate.parse(invoiceDateField.getText().trim(), displayDateFormatter);
            LocalDate.parse(dueDateField.getText().trim(), displayDateFormatter);

            String paidDateText = paidDateField.getText().trim();
            if (!paidDateText.isEmpty()) {
                LocalDate.parse(paidDateText, displayDateFormatter);
            }

            new BigDecimal(taxRateField.getText().trim());
            new BigDecimal(paymentsAppliedField.getText().trim());
            new BigDecimal(returnedCheckFeeField.getText().trim());
            new BigDecimal(interestPercentField.getText().trim());
            Integer.parseInt(interestStartDaysField.getText().trim());
            Integer.parseInt(interestIntervalDaysField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date or number format.\nDates should be MM/dd/yyyy");
            return false;
        }
        return true;
    }

    public boolean isSaved() {
        return saved;
    }
}
