package com.applefitnessequipment.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Apple Fitness Equipment Design System
 * Brand colors and styling based on applefitnessequipment.com
 */
public class AppTheme {

    // ===== BRAND COLORS =====
    public static final Color BRAND_RED = new Color(204, 34, 41);        // #CC2229
    public static final Color BRAND_RED_DARK = new Color(153, 25, 31);
    public static final Color BRAND_RED_LIGHT = new Color(230, 76, 82);
    public static final Color BRAND_BLACK = new Color(0, 0, 0);
    public static final Color BRAND_WHITE = new Color(255, 255, 255);

    // UI Colors
    public static final Color BACKGROUND = new Color(245, 245, 245);
    public static final Color SURFACE = new Color(255, 255, 255);
    public static final Color BORDER = new Color(224, 224, 224);
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    public static final Color TEXT_MUTED = new Color(158, 158, 158);

    // Status Colors
    public static final Color SUCCESS = new Color(46, 125, 50);
    public static final Color WARNING = new Color(245, 124, 0);
    public static final Color DANGER = new Color(204, 34, 41);
    public static final Color INFO = new Color(2, 136, 209);

    // Table Colors
    public static final Color TABLE_HEADER_BG = new Color(33, 33, 33);
    public static final Color TABLE_ROW_ALT = new Color(250, 250, 250);
    public static final Color TABLE_SELECTION = new Color(255, 235, 238);
    public static final Color TABLE_GRID = new Color(238, 238, 238);

    // ===== FONTS =====
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_TITLE_LARGE = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 12);

    // ===== SPACING =====
    public static final int SPACING_XS = 4;
    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 16;
    public static final int SPACING_LG = 24;
    public static final int SPACING_XL = 32;

    // ===== FIELD SIZES =====
    public static final int FIELD_WIDTH = 30;
    public static final int FIELD_HEIGHT = 35;
    public static final int BUTTON_HEIGHT = 36;
    public static final int TABLE_ROW_HEIGHT = 32;
}
