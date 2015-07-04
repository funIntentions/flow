package com.projects.gui.table;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.*;
import java.util.Date;

/**
 * Created by Dan on 7/3/2015.
 */
public class TimeEditor extends DefaultCellEditor
{
    SimpleDateFormat timeFormat;

    public TimeEditor()
    {
        super (new JFormattedTextField());
        timeFormat = new SimpleDateFormat("HH:mm");
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        JFormattedTextField editor = (JFormattedTextField)super.getTableCellEditorComponent(table, value, isSelected, row, column);

        if (value != null)
        {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            editor.setFormatterFactory(new DefaultFormatterFactory(new DateFormatter(timeFormat)));
            editor.setValue(value);
        }

        return editor;
    }

    @Override
    public boolean stopCellEditing()
    {
        try {
            // try to get the value
            this.getCellEditorValue();
            return super.stopCellEditing();
        } catch (Exception ex) {
            return false;
        }

    }

    @Override
    public Object getCellEditorValue()
    {
        // get content of textField
        String value = super.getCellEditorValue().toString();

        // try to parse a number
        try {
            Date time = timeFormat.parse(value);

            // return an instance of column class
            return time;

        } catch (ParseException pex) {
            throw new RuntimeException(pex);
        }
    }


}
