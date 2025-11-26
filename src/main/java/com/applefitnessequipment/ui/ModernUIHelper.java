package com.applefitnessequipment.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Helper class for applying modern UI styling to Swing components
 */
public class ModernUIHelper {
    
    // Apple Fitness Equipment brand colors
    public static final Color PRIMARY_COLOR   = new Color(204, 34, 41);    // Brand Red #CC2229
    public static final Color SUCCESS_COLOR   = new Color(46, 125, 50);    // Green
    public static final Color DANGER_COLOR    = new Color(204, 34, 41);    // Red (same as brand)
    public static final Color WARNING_COLOR   = new Color(245, 124, 0);    // Orange
    public static final Color SECONDARY_COLOR = new Color(158, 158, 158);  // Gray
    public static final Color LIGHT_BG        = new Color(245, 245, 245);  // Light gray background
    public static final Color WHITE           = Color.WHITE;
    public static final Color TEXT_COLOR      = new Color(33, 33, 33);     // Near black text
    
    // Fonts
    public static final Font TITLE_FONT  = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font SMALL_FONT  = new Font("Segoe UI", Font.PLAIN, 11);
    
    /**
     * Style a button with primary/semantic colors
     */
    public static void styleButton(JButton button, String type) {
        button.setFont(NORMAL_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        // Make Swing use white for disabled text globally
        UIManager.put("Button.disabledText", WHITE);

        // Use a basic UI so LAF doesn’t get too fancy with states
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        // White text always
        button.setForeground(WHITE);

        // Background by type
        switch (type.toLowerCase()) {
            case "primary":
                button.setBackground(PRIMARY_COLOR);
                break;
            case "success":
                button.setBackground(SUCCESS_COLOR);
                break;
            case "danger":
                button.setBackground(DANGER_COLOR);
                break;
            case "warning":
                button.setBackground(WARNING_COLOR);
                break;
            case "secondary":
            default:
                button.setBackground(SECONDARY_COLOR);
                break;
        }

        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(button.getBackground().darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        // Hover effect (only when enabled)
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color original = button.getBackground();
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(original.brighter());
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(original);
            }
        });
    }
    
    /**
     * Style a table with modern appearance
     */
    public static void styleTable(JTable table) {
        // Table styling
        table.setFont(NORMAL_FONT);
        table.setRowHeight(32);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(255, 235, 238)); // Light red selection
        table.setSelectionForeground(TEXT_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new java.awt.Dimension(1, 1));
        table.setBackground(WHITE);
        table.setFocusable(false); // Disable focus to prevent focus border

        // Header styling - dark background with white text for maximum visibility
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(33, 33, 33));  // Near black
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        header.setOpaque(true);
        header.setPreferredSize(new java.awt.Dimension(header.getPreferredSize().width, 40));

        // Custom header renderer
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(new Color(33, 33, 33));  // Dark background
                label.setForeground(Color.WHITE);            // White text
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
                label.setOpaque(true);
                return label;
            }
        });

        // Zebra striping and center alignment
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? WHITE : new Color(250, 250, 250));
                }

                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
    }
    
    /**
     * Create a modern card-style border for panels with elevated appearance
     */
    public static Border createModernBorder(String title) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 3, new Color(200, 200, 200)), // Deeper shadow
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    BorderFactory.createMatteBorder(3, 0, 0, 0, PRIMARY_COLOR) // Red top accent bar
                )
            ),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12),
                title,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_COLOR  // Red title for brand consistency
            )
        );
    }
    
    /**
     * Style a label as a title
     */
    public static void styleTitle(JLabel label) {
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT_COLOR);
    }
    
    /**
     * Get standard text field width (larger for better UX)
     */
    public static final int STANDARD_FIELD_WIDTH = 30;
    
    /**
     * Style a text field with modern appearance and focus states
     */
    public static void styleTextField(JTextField field) {
        field.setFont(NORMAL_FONT);
        Border normalBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        );
        Border focusBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(4, 7, 4, 7)
        );
        field.setBorder(normalBorder);

        // Add focus listener for red border highlight
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(focusBorder);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(normalBorder);
            }
        });
    }
    
    /**
     * Style a combo box with modern appearance
     */
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(NORMAL_FONT);
    }

    /**
     * Add toggle behavior to table - clicking an already selected row will deselect it
     */
    public static void addTableToggleBehavior(JTable table) {
        addTableToggleBehavior(table, null);
    }

    /**
     * Add toggle behavior to table with callback - clicking anywhere in the selected row will deselect it
     * @param table The table to add toggle behavior to
     * @param onDeselect Optional callback to run when a row is deselected (e.g., to clear a form)
     */
    public static void addTableToggleBehavior(JTable table, Runnable onDeselect) {
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            private int lastClickedRow = -1;

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                // Only process if we clicked on a valid cell within the table bounds
                if (row >= 0 && col >= 0) {
                    // Check if clicking anywhere in the same row that's currently selected
                    if (lastClickedRow == row && table.getSelectedRow() == row) {
                        // Prevent the default selection behavior by consuming the event
                        e.consume();
                        // Deselect it
                        table.clearSelection();
                        table.repaint(); // Force repaint to clear any residual selection highlight
                        lastClickedRow = -1;
                        // Trigger the deselect callback if provided
                        if (onDeselect != null) {
                            onDeselect.run();
                        }
                    } else {
                        // Update the last clicked row (will be processed by default table behavior)
                        lastClickedRow = row;
                    }
                }
            }
        });
    }
}
