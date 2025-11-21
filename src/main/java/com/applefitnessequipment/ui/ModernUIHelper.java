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
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Helper class for applying modern UI styling to Swing components
 */
public class ModernUIHelper {
    
    // Modern color palette
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Blue
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);      // Green
    public static final Color DANGER_COLOR = new Color(231, 76, 60);        // Red
    public static final Color WARNING_COLOR = new Color(243, 156, 18);      // Orange
    public static final Color SECONDARY_COLOR = new Color(149, 165, 166);   // Gray
    public static final Color LIGHT_BG = new Color(236, 240, 241);          // Light gray background
    public static final Color WHITE = Color.WHITE;
    public static final Color TEXT_COLOR = new Color(44, 62, 80);           // Dark gray text
    
    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 11);
    
    /**
     * Style a button with primary color
     */
    public static void styleButton(JButton button, String type) {
        button.setFont(NORMAL_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        
        switch(type.toLowerCase()) {
            case "primary":
                button.setBackground(PRIMARY_COLOR);
                button.setForeground(WHITE);
                break;
            case "success":
                button.setBackground(SUCCESS_COLOR);
                button.setForeground(WHITE);
                break;
            case "danger":
                button.setBackground(DANGER_COLOR);
                button.setForeground(WHITE);
                break;
            case "warning":
                button.setBackground(WARNING_COLOR);
                button.setForeground(WHITE);
                break;
            case "secondary":
            default:
                button.setBackground(SECONDARY_COLOR);
                button.setForeground(WHITE);
                break;
        }
        
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(button.getBackground().darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color original = button.getBackground();
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(original.brighter());
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
        table.setRowHeight(30);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(PRIMARY_COLOR.brighter());
        table.setSelectionForeground(WHITE);
        table.setShowGrid(true);
        table.setIntercellSpacing(new java.awt.Dimension(1, 1));
        
        // Header styling - dark background with white text for maximum visibility
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(45, 52, 54));  // Dark gray/black background
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        header.setOpaque(true);
        
        // Create and set a custom header renderer that forces our colors
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(new Color(45, 52, 54));  // Dark background
                label.setForeground(Color.WHITE);  // White text
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
                label.setOpaque(true);
                return label;
            }
        });
        
        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    /**
     * Create a modern border for panels
     */
    public static Border createModernBorder(String title) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                title,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                TITLE_FONT,
                TEXT_COLOR
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
     * Style a text field with modern appearance
     */
    public static void styleTextField(JTextField field) {
        field.setFont(NORMAL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
    
    /**
     * Style a combo box with modern appearance
     */
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(NORMAL_FONT);
    }
}
