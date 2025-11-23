package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.applefitnessequipment.dao.ClientDAO;
import com.applefitnessequipment.dao.EmployeeTimeLogDAO;
import com.applefitnessequipment.dao.EquipmentQuoteCompleteDAO;
import com.applefitnessequipment.dao.InvoiceDAO;
import com.applefitnessequipment.dao.PreventiveMaintenanceDAO;
import com.applefitnessequipment.model.Client;
import com.applefitnessequipment.model.EquipmentQuoteComplete;
import com.applefitnessequipment.model.Invoice;
import com.applefitnessequipment.model.PreventiveMaintenance;

public class DashboardPanel extends JPanel {

    // Brand colors
    private static final Color PRIMARY_RED = new Color(204, 34, 41);
    private static final Color PRIMARY_RED_DARK = new Color(153, 26, 31);
    private static final Color TEXT_DARK = new Color(33, 33, 33);
    private static final Color TEXT_GRAY = new Color(100, 100, 100);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PANEL_BG = new Color(245, 245, 245);

    // DAOs
    private ClientDAO clientDAO;
    private InvoiceDAO invoiceDAO;
    private EquipmentQuoteCompleteDAO quoteDAO;
    private PreventiveMaintenanceDAO pmDAO;
    private EmployeeTimeLogDAO timeLogDAO;

    // Reference to parent tabbed pane for navigation
    private JTabbedPane parentTabbedPane;

    public DashboardPanel(JTabbedPane tabbedPane) {
        this.parentTabbedPane = tabbedPane;
        initDAOs();
        initUI();
    }

    private void initDAOs() {
        clientDAO = new ClientDAO();
        invoiceDAO = new InvoiceDAO();
        quoteDAO = new EquipmentQuoteCompleteDAO();
        pmDAO = new PreventiveMaintenanceDAO();
        timeLogDAO = new EmployeeTimeLogDAO();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(PANEL_BG);

        // Create main scrollable content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(PANEL_BG);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Welcome Section
        mainContent.add(createWelcomeSection());
        mainContent.add(Box.createVerticalStrut(25));

        // Stats Cards Section
        mainContent.add(createStatsSection());
        mainContent.add(Box.createVerticalStrut(25));

        // Quick Actions Section
        mainContent.add(createQuickActionsSection());
        mainContent.add(Box.createVerticalStrut(25));

        // Recent Activity Section
        mainContent.add(createRecentActivitySection());

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createWelcomeSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        // Greeting
        LocalDateTime now = LocalDateTime.now();
        String greeting = getGreeting(now.getHour());
        JLabel greetingLabel = new JLabel(greeting);
        greetingLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        greetingLabel.setForeground(TEXT_DARK);
        greetingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Date and tagline
        String dateStr = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        JLabel dateLabel = new JLabel(dateStr);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(TEXT_GRAY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel taglineLabel = new JLabel("Your fitness equipment management hub");
        taglineLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        taglineLabel.setForeground(TEXT_GRAY);
        taglineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(greetingLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(dateLabel);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(taglineLabel);

        panel.add(textPanel, BorderLayout.WEST);

        return panel;
    }

    private String getGreeting(int hour) {
        if (hour < 12) return "Good Morning!";
        else if (hour < 17) return "Good Afternoon!";
        else return "Good Evening!";
    }

    private JPanel createStatsSection() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // Fetch data
        int clientCount = getClientCount();
        Object[] invoiceData = getInvoiceData();
        int quoteCount = getOpenQuoteCount();
        int pmCount = getUpcomingPMCount();

        // Create stat cards
        panel.add(createStatCard("Active Clients", String.valueOf(clientCount), "Total registered clients", new Color(59, 130, 246)));
        panel.add(createStatCard("Open Invoices", "$" + formatCurrency((BigDecimal) invoiceData[1]), (int) invoiceData[0] + " pending", new Color(16, 185, 129)));
        panel.add(createStatCard("Equipment Quotes", String.valueOf(quoteCount), "Active quotes", new Color(245, 158, 11)));
        panel.add(createStatCard("PM Due Soon", String.valueOf(pmCount), "Next 30 days", PRIMARY_RED));

        return panel;
    }

    private JPanel createStatCard(String title, String value, String subtitle, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded rectangle with shadow effect
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);

                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);

                // Top accent bar
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth() - 3, 4, 15, 15);
                g2.fillRect(0, 4, getWidth() - 3, 2);

                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));
        card.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT_GRAY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(TEXT_DARK);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitleLabel);

        return card;
    }

    private JPanel createQuickActionsSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel sectionTitle = new JLabel("Quick Actions");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(TEXT_DARK);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        buttonsPanel.setOpaque(false);

        buttonsPanel.add(createQuickActionButton("New Client", "Add a new client", 0));
        buttonsPanel.add(createQuickActionButton("New Quote", "Create equipment quote", 6));
        buttonsPanel.add(createQuickActionButton("New Invoice", "Generate invoice", 4));
        buttonsPanel.add(createQuickActionButton("Time Log", "Record work hours", 3));

        section.add(sectionTitle, BorderLayout.NORTH);
        section.add(buttonsPanel, BorderLayout.CENTER);

        return section;
    }

    private JButton createQuickActionButton(String text, String tooltip, int tabIndex) {
        // Track scale for hover animation - start smaller to allow room for scaling
        final float[] scale = {0.96f};
        final javax.swing.Timer[] animTimer = {null};

        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Apply scale transformation for hover effect
                int w = getWidth();
                int h = getHeight();
                int scaledW = (int) (w * scale[0]);
                int scaledH = (int) (h * scale[0]);
                int offsetX = (w - scaledW) / 2;
                int offsetY = (h - scaledH) / 2;

                if (getModel().isPressed()) {
                    GradientPaint gp = new GradientPaint(0, 0, PRIMARY_RED_DARK, 0, scaledH, PRIMARY_RED);
                    g2.setPaint(gp);
                } else if (getModel().isRollover()) {
                    GradientPaint gp = new GradientPaint(0, 0, PRIMARY_RED, 0, scaledH, PRIMARY_RED_DARK);
                    g2.setPaint(gp);
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, PRIMARY_RED, 0, scaledH, PRIMARY_RED_DARK);
                    g2.setPaint(gp);
                }

                g2.fillRoundRect(offsetX, offsetY, scaledW, scaledH, 16, 16);
                g2.dispose();

                // Draw text centered
                g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(getFont());
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int textX = (w - fm.stringWidth(getText())) / 2;
                int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setToolTipText(tooltip);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 50));

        // Remove border to prevent clipping during scale animation
        button.setBorder(null);

        // Add hover animation - scales from 0.96 to 1.0 on hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (animTimer[0] != null) animTimer[0].stop();
                animTimer[0] = new javax.swing.Timer(10, evt -> {
                    if (scale[0] < 1.0f) {
                        scale[0] += 0.005f;
                        button.repaint();
                    } else {
                        scale[0] = 1.0f;
                        button.repaint();
                        ((javax.swing.Timer) evt.getSource()).stop();
                    }
                });
                animTimer[0].start();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (animTimer[0] != null) animTimer[0].stop();
                animTimer[0] = new javax.swing.Timer(10, evt -> {
                    if (scale[0] > 0.96f) {
                        scale[0] -= 0.005f;
                        button.repaint();
                    } else {
                        scale[0] = 0.96f;
                        button.repaint();
                        ((javax.swing.Timer) evt.getSource()).stop();
                    }
                });
                animTimer[0].start();
            }
        });

        // Navigate to corresponding tab
        button.addActionListener(e -> {
            if (parentTabbedPane != null && tabIndex < parentTabbedPane.getTabCount()) {
                parentTabbedPane.setSelectedIndex(tabIndex + 1); // +1 because Dashboard is first
            }
        });

        return button;
    }

    private JPanel createRecentActivitySection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);

        JLabel sectionTitle = new JLabel("Recent Activity");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(TEXT_DARK);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Create tabbed tables
        JPanel tablesPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        tablesPanel.setOpaque(false);

        tablesPanel.add(createRecentInvoicesCard());
        tablesPanel.add(createRecentQuotesCard());
        tablesPanel.add(createUpcomingPMCard());

        section.add(sectionTitle, BorderLayout.NORTH);
        section.add(tablesPanel, BorderLayout.CENTER);

        return section;
    }

    private JPanel createRecentInvoicesCard() {
        JPanel card = createActivityCard("Recent Invoices");

        String[] columns = {"Invoice #", "Client", "Amount", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            List<Invoice> invoices = invoiceDAO.getAllInvoices();
            int count = 0;
            for (Invoice inv : invoices) {
                if (count >= 5) break;

                String clientName = getClientName(inv.getClientId());
                model.addRow(new Object[]{
                    inv.getInvoiceNumber(),
                    clientName,
                    "$" + formatCurrency(inv.getTotalAmount()),
                    inv.getStatus()
                });
                count++;
            }
        } catch (SQLException e) {
            model.addRow(new Object[]{"--", "--", "--", "--"});
        }

        JTable table = createStyledTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(300, 150));

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createRecentQuotesCard() {
        JPanel card = createActivityCard("Recent Quotes");

        String[] columns = {"Quote #", "Contact", "Amount", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            List<EquipmentQuoteComplete> quotes = quoteDAO.getAllQuotes();
            int count = 0;
            for (EquipmentQuoteComplete quote : quotes) {
                if (count >= 5) break;

                model.addRow(new Object[]{
                    quote.getQuoteNumber(),
                    quote.getContactName(),
                    "$" + formatCurrency(quote.getSubtotalAmount()),
                    quote.getStatus()
                });
                count++;
            }
        } catch (SQLException e) {
            model.addRow(new Object[]{"--", "--", "--", "--"});
        }

        JTable table = createStyledTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(300, 150));

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createUpcomingPMCard() {
        JPanel card = createActivityCard("Upcoming PM Agreements");

        String[] columns = {"Agreement #", "Client", "End Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            List<PreventiveMaintenance> agreements = pmDAO.getAllAgreements();
            LocalDate now = LocalDate.now();
            LocalDate thirtyDays = now.plusDays(30);
            int count = 0;

            for (PreventiveMaintenance pm : agreements) {
                if (count >= 5) break;
                if (pm.getEndDate() != null &&
                    (pm.getEndDate().isBefore(thirtyDays) || pm.getEndDate().isEqual(thirtyDays)) &&
                    pm.getEndDate().isAfter(now)) {

                    String clientName = getClientName(pm.getClientId());
                    model.addRow(new Object[]{
                        pm.getAgreementNumber(),
                        clientName,
                        pm.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                        pm.getStatus()
                    });
                    count++;
                }
            }

            if (count == 0) {
                model.addRow(new Object[]{"No upcoming", "agreements", "due", "--"});
            }
        } catch (SQLException e) {
            model.addRow(new Object[]{"--", "--", "--", "--"});
        }

        JTable table = createStyledTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(300, 150));

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createActivityCard(String title) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);

                // Card background
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_RED);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        card.add(titleLabel, BorderLayout.NORTH);

        return card;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(28);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(255, 235, 238));
        table.setSelectionForeground(TEXT_DARK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setForeground(TEXT_DARK);

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        return table;
    }

    // Data fetching methods

    private int getClientCount() {
        try {
            return clientDAO.getAllClients().size();
        } catch (SQLException e) {
            return 0;
        }
    }

    private Object[] getInvoiceData() {
        // Returns [count, totalAmount]
        try {
            List<Invoice> invoices = invoiceDAO.getAllInvoices();
            int pendingCount = 0;
            BigDecimal totalDue = BigDecimal.ZERO;

            for (Invoice inv : invoices) {
                // Count unpaid invoices: Draft, Open, or Overdue
                if ("Draft".equalsIgnoreCase(inv.getStatus()) ||
                    "Open".equalsIgnoreCase(inv.getStatus()) ||
                    "Overdue".equalsIgnoreCase(inv.getStatus())) {
                    pendingCount++;
                    if (inv.getTotalAmount() != null) {
                        totalDue = totalDue.add(inv.getTotalAmount());
                    }
                }
            }

            return new Object[]{pendingCount, totalDue};
        } catch (SQLException e) {
            return new Object[]{0, BigDecimal.ZERO};
        }
    }

    private int getOpenQuoteCount() {
        try {
            List<EquipmentQuoteComplete> quotes = quoteDAO.getAllQuotes();
            int count = 0;
            for (EquipmentQuoteComplete quote : quotes) {
                String status = quote.getStatus();
                // Count active quotes: Draft or Sent (not yet Accepted, Declined, or Expired)
                if (status != null &&
                    ("Draft".equalsIgnoreCase(status) || "Sent".equalsIgnoreCase(status))) {
                    count++;
                }
            }
            return count;
        } catch (SQLException e) {
            return 0;
        }
    }

    private int getUpcomingPMCount() {
        try {
            List<PreventiveMaintenance> agreements = pmDAO.getAllAgreements();
            LocalDate now = LocalDate.now();
            LocalDate thirtyDays = now.plusDays(30);
            int count = 0;

            for (PreventiveMaintenance pm : agreements) {
                if (pm.getEndDate() != null &&
                    (pm.getEndDate().isBefore(thirtyDays) || pm.getEndDate().isEqual(thirtyDays)) &&
                    pm.getEndDate().isAfter(now)) {
                    count++;
                }
            }
            return count;
        } catch (SQLException e) {
            return 0;
        }
    }

    private String getClientName(int clientId) {
        try {
            Client client = clientDAO.getClientById(clientId);
            if (client != null) {
                if (client.getCompanyName() != null && !client.getCompanyName().isEmpty()) {
                    return client.getCompanyName();
                }
                return client.getFirstName() + " " + client.getLastName();
            }
        } catch (SQLException e) {
            // Fall through
        }
        return "Unknown";
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0.00";
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(amount);
    }

    // TODO: Backend queries needed for optimized dashboard metrics:
    // - getActiveClientCount() - COUNT query instead of fetching all clients
    // - getPendingInvoiceSummary() - SUM and COUNT with WHERE status = 'Pending'
    // - getOpenQuoteCount() - COUNT query with WHERE status = 'Open'
    // - getUpcomingPMAgreementCount() - COUNT with date range filter
    // - getEmployeeHoursThisWeek() - SUM of TotalHours for current week
}
