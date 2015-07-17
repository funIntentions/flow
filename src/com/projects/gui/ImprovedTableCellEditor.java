package com.projects.gui;

import com.projects.gui.ImprovedFormattedTextField;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.text.Format;

/**
 * Created by Dan on 7/15/2015.
 */
public class ImprovedTableCellEditor extends DefaultCellEditor
{
    ImprovedFormattedTextField textField;

    public ImprovedTableCellEditor(ImprovedFormattedTextField textField)
    {
        super(textField);
        this.textField = textField;
    }

    @Override
    public boolean stopCellEditing()
    {
        return (textField.isContentValid() && super.stopCellEditing());
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int row, int column)
    {
        return super.getTableCellEditorComponent(
                table, value, isSelected, row, column);
    }

    public Object getCellEditorValue() {
        return textField.getText();
    }
}
