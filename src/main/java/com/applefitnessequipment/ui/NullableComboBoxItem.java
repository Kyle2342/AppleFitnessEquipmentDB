package com.applefitnessequipment.ui;

/**
 * Wrapper class to allow null/empty selection in JComboBox.
 * Displays a placeholder text for the null option.
 */
public class NullableComboBoxItem<T> {
    private final T value;
    private final String displayText;
    private final boolean isNullOption;

    /**
     * Creates the null/empty option.
     */
    public NullableComboBoxItem(String placeholderText) {
        this.value = null;
        this.displayText = placeholderText;
        this.isNullOption = true;
    }

    /**
     * Creates a regular option with a value.
     */
    public NullableComboBoxItem(T value) {
        this.value = value;
        this.displayText = value != null ? value.toString() : "";
        this.isNullOption = false;
    }

    public T getValue() {
        return value;
    }

    public boolean isNullOption() {
        return isNullOption;
    }

    @Override
    public String toString() {
        return displayText;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        NullableComboBoxItem<?> other = (NullableComboBoxItem<?>) obj;

        if (isNullOption && other.isNullOption) return true;
        if (isNullOption != other.isNullOption) return false;

        return value != null ? value.equals(other.value) : other.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
