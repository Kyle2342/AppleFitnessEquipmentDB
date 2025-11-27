package com.applefitnessequipment.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * A searchable/filterable ComboBox that allows users to type and filter items.
 * As the user types, the dropdown list is filtered to show matching items.
 *
 * @param <T> The type of items in the combo box
 */
public class AutocompleteComboBox<T> extends JComboBox<T> {
    private List<T> allItems;
    private boolean adjusting = false;
    private boolean pendingFilter = false;

    public AutocompleteComboBox() {
        super();
        this.allItems = new ArrayList<>();
        setEditable(true);

        JTextField editor = (JTextField) getEditor().getEditorComponent();
        editor.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { scheduleFilter(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { scheduleFilter(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { scheduleFilter(); }
        });

        // When the user opens the dropdown arrow, always start from the full list.
        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                showAllItemsForPopup();
            }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        // Close the dropdown immediately after a user selection.
        addActionListener(e -> {
            if (adjusting) return;
            // Use invokeLater so the selection event completes before closing.
            SwingUtilities.invokeLater(() -> {
                if (canTogglePopup()) {
                    hidePopup();
                } else {
                    setPopupVisible(false);
                }
            });
        });
    }

    /**
     * Sets all available items for the combo box.
     * This is the master list that will be filtered as user types.
     */
    public void setAllItems(List<T> items) {
        this.allItems = new ArrayList<>(items);
        adjusting = true;
        try {
            refreshItems(items, true);
        } finally {
            adjusting = false;
        }
    }

    /**
     * Gets all available items (unfiltered).
     */
    public List<T> getAllItems() {
        return new ArrayList<>(allItems);
    }

    /**
     * Refreshes the displayed items in the dropdown.
     *
     * @param items          list to display
     * @param clearSelection when true, leaves no selection after refresh
     */
    private void refreshItems(List<T> items, boolean clearSelection) {
        removeAllItems();
        for (T item : items) {
            addItem(item);
        }
        if (clearSelection) {
            setSelectedIndex(-1);
        }
    }

    private boolean canTogglePopup() {
        return isDisplayable() && isShowing();
    }

    private void showAllItemsForPopup() {
        if (adjusting) return;
        adjusting = true;
        try {
            T currentSelection = getSelectedItem();
            refreshItems(allItems, false);
            if (currentSelection != null && allItems.contains(currentSelection)) {
                setSelectedItemByObject(currentSelection);
            }
        } finally {
            adjusting = false;
        }
    }

    /**
 * Filters the items based on what the user has typed.
 */
private void filterItems() {
    if (adjusting) return;
    adjusting = true;
    try {
        JTextField editor = (JTextField) getEditor().getEditorComponent();
        String text = editor.getText();
        String lower = text.toLowerCase();

        // Remember caret so it doesn't jump to start
        int caretPos = editor.getCaretPosition();

        // Build filtered list
        List<T> filtered = new ArrayList<>();
        if (lower.isEmpty()) {
            filtered.addAll(allItems);
        } else {
            for (T item : allItems) {
                if (item != null && item.toString().toLowerCase().contains(lower)) {
                    filtered.add(item);
                }
            }
        }

        // Rebuild model WITHOUT touching selection/editor text
        removeAllItems();
        for (T item : filtered) {
            addItem(item);
        }

        // Restore what the user typed
        editor.setText(text);
        if (caretPos <= text.length()) {
            editor.setCaretPosition(caretPos);
        } else {
            editor.setCaretPosition(text.length());
        }

        // Show/hide popup safely
        if (filtered.isEmpty()) {
            if (canTogglePopup()) {
                hidePopup();
            }
        } else {
            if (canTogglePopup()) {
                showPopup();  // safe: we checked isDisplayable() && isShowing()
            }
        }
    } finally {
        adjusting = false;
    }
}

    /**
     * Gets the currently selected item, or null if nothing/invalid selection.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getSelectedItem() {
        Object selected = super.getSelectedItem();
        if (selected instanceof String) {
            // User typed something that doesn't match an item
            String text = (String) selected;
            for (T item : allItems) {
                if (item != null && item.toString().equalsIgnoreCase(text)) {
                    return item;
                }
            }
            return null;
        }
        return (T) selected;
    }

    /**
     * Sets the selected item by matching object.
     */
    public void setSelectedItemByObject(T item) {
        if (item == null) {
            adjusting = true;
            try {
                setSelectedIndex(-1);
                JTextField editor = (JTextField) getEditor().getEditorComponent();
                editor.setText("");
            } finally {
                adjusting = false;
            }
            return;
        }

        adjusting = true;
        try {
            for (int i = 0; i < getItemCount(); i++) {
                if (item.equals(getItemAt(i))) {
                    setSelectedIndex(i);
                    return;
                }
            }

            // If not found in current filtered list, show all and try again
            refreshItems(allItems, false);
            for (int i = 0; i < getItemCount(); i++) {
                if (item.equals(getItemAt(i))) {
                    setSelectedIndex(i);
                    return;
                }
            }
        } finally {
            adjusting = false;
        }
    }

    @Override
    public void setSelectedItem(Object anObject) {
        super.setSelectedItem(anObject);
        if (!adjusting && isPopupVisible() && canTogglePopup()) {
            hidePopup(); // close dropdown after a selection
        }
    }

    /**
     * Clears the selection and text.
     */
    public void clearSelection() {
        adjusting = true;
        try {
            JTextField editor = (JTextField) getEditor().getEditorComponent();
            editor.setText("");
            refreshItems(allItems, true);
        } finally {
            adjusting = false;
        }
    }

    private void scheduleFilter() {
        if (pendingFilter) return;
        pendingFilter = true;
        SwingUtilities.invokeLater(() -> {
            pendingFilter = false;
            filterItems();
        });
    }
}
