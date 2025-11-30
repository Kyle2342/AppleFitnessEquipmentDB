package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.applefitnessequipment.dao.CompanyDAO;
import com.applefitnessequipment.model.Company;

public class CompanyPanel extends JPanel {
    private CompanyDAO companyDAO;
    private JTextField companyNameField, streetAddressField, cityField, countyField;
    private JTextField stateField, zipCodeField, countryField;
    private JTextField phoneField, faxField, emailField, websiteURLField;
    private JButton updateButton;
    private Company company;
    private boolean isUpdatingPhone = false;

    public CompanyPanel() {
        companyDAO = new CompanyDAO();
        initComponents();
        loadCompany();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(ModernUIHelper.createModernBorder("Company Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Company Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Company Name:*"), gbc);
        gbc.gridx = 1;
        companyNameField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(companyNameField);
        formPanel.add(companyNameField, gbc);
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

        // County
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("County:*"), gbc);
        gbc.gridx = 1;
        countyField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(countyField);
        formPanel.add(countyField, gbc);
        row++;

        // State
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("State:*"), gbc);
        gbc.gridx = 1;
        stateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(stateField);
        formPanel.add(stateField, gbc);
        row++;

        // ZIP Code
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("ZIP Code:*"), gbc);
        gbc.gridx = 1;
        zipCodeField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(zipCodeField);
        formPanel.add(zipCodeField, gbc);
        row++;

        // Country
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Country:*"), gbc);
        gbc.gridx = 1;
        countryField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(countryField);
        formPanel.add(countryField, gbc);
        row++;

        // Phone - with formatting
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Phone:*"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(phoneField);
        setupPhoneFormatting(phoneField);
        formPanel.add(phoneField, gbc);
        row++;

        // Fax - with formatting
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Fax:*"), gbc);
        gbc.gridx = 1;
        faxField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(faxField);
        setupPhoneFormatting(faxField);
        formPanel.add(faxField, gbc);
        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Email:*"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(emailField);
        formPanel.add(emailField, gbc);
        row++;

        // Website URL
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Website URL:*"), gbc);
        gbc.gridx = 1;
        websiteURLField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(websiteURLField);
        formPanel.add(websiteURLField, gbc);
        row++;

        // Update Button - Centered
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        updateButton = new JButton("Update Company Info");
        updateButton.addActionListener(e -> updateCompany());
        ModernUIHelper.styleButton(updateButton, "success");
        formPanel.add(updateButton, gbc);

        // Wrap form in scroll pane for smaller screens
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Setup phone number formatting: displays as (123) 456-7890 but saves as 1234567890
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

                // Remove all non-digits
                String digitsOnly = newText.replaceAll("[^0-9]", "");

                // Limit to 10 digits
                if (digitsOnly.length() > 10) {
                    return;
                }

                // Format the number
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
     * Extract just the digits from a formatted phone number
     */
    private String getPhoneDigits(String formatted) {
        return formatted.replaceAll("[^0-9]", "");
    }

    private void loadCompany() {
        try {
            company = companyDAO.getCompany();
            if (company != null) {
                companyNameField.setText(company.getCompanyName());
                streetAddressField.setText(company.getStreetAddress());
                cityField.setText(company.getCity());
                countyField.setText(company.getCounty());
                stateField.setText(company.getState());
                zipCodeField.setText(company.getZipCode());
                countryField.setText(company.getCountry());

                // Format phone and fax for display
                String phone = company.getPhone();
                if (phone != null && !phone.isEmpty()) {
                    phoneField.setText(formatPhoneNumber(phone));
                }

                String fax = company.getFax();
                if (fax != null && !fax.isEmpty()) {
                    faxField.setText(formatPhoneNumber(fax));
                }

                emailField.setText(company.getEmail());
                websiteURLField.setText(company.getWebsiteURL());
            } else {
                // Create default company record if it doesn't exist
                createDefaultCompany();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading company information: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createDefaultCompany() {
        Company defaultCompany = new Company();
        defaultCompany.setCompanyId(1);
        defaultCompany.setCompanyName("Apple Fitness Equipment");
        defaultCompany.setStreetAddress("1412 Majestic View Dr.");
        defaultCompany.setCity("State College");
        defaultCompany.setCounty("Centre");
        defaultCompany.setState("PA");
        defaultCompany.setZipCode("16801");
        defaultCompany.setCountry("USA");
        defaultCompany.setPhone("8148262922");
        defaultCompany.setFax("8148262933");
        defaultCompany.setEmail("gbartram90@gmail.com");
        defaultCompany.setWebsiteURL("https://applefitnessequipment.com/");

        try {
            if (companyDAO.insertCompany(defaultCompany)) {
                company = defaultCompany;
                loadCompany();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error creating default company: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCompany() {
        if (!validateForm()) return;

        company.setCompanyName(companyNameField.getText().trim());
        company.setStreetAddress(streetAddressField.getText().trim());
        company.setCity(cityField.getText().trim());
        company.setCounty(countyField.getText().trim());
        company.setState(stateField.getText().trim());
        company.setZipCode(zipCodeField.getText().trim());
        company.setCountry(countryField.getText().trim());
        company.setPhone(getPhoneDigits(phoneField.getText()));  // Save only digits
        company.setFax(getPhoneDigits(faxField.getText()));      // Save only digits
        company.setEmail(emailField.getText().trim());
        company.setWebsiteURL(websiteURLField.getText().trim());

        try {
            if (companyDAO.updateCompany(company)) {
                JOptionPane.showMessageDialog(this, "Company information updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update company information.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating company: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateForm() {
        if (companyNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Company name is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (streetAddressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Street address is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (cityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "City is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (countyField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "County is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (stateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "State is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (zipCodeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ZIP code is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (countryField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Country is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (faxField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fax is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (websiteURLField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Website URL is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    public void refreshData() {
        loadCompany();
    }
}
