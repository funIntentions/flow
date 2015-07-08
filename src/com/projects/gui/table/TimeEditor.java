package com.projects.gui.table;

import com.projects.helper.Constants;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;
import java.text.*;

/**
 * Created by Dan on 7/3/2015.
 */
public class TimeEditor extends DefaultCellEditor
{
    SimpleDateFormat timeFormat;

    public TimeEditor()
    {
        super (new JFormattedTextField());
        timeFormat = new SimpleDateFormat(Constants.HOURS_AND_MINUTES_FORMAT);
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        JFormattedTextField editor = (JFormattedTextField)super.getTableCellEditorComponent(table, value, isSelected, row, column);

        if (value != null)
        {
            SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.HOURS_AND_MINUTES_FORMAT);
            editor.setFormatterFactory(new DefaultFormatterFactory(new DateFormatter(timeFormat)));
            editor.setValue(value);
        }

        return editor;
    }

    /*@Override
    public boolean stopCellEditing()
    {
        try {
            // try to get the value
            this.getCellEditorValue();
            return super.stopCellEditing();
        } catch (Exception ex) {
            return false;
        }
    }*/

    @Override
    public Object getCellEditorValue()
    {
        // get content of textField
        String value = super.getCellEditorValue().toString();

        // try to parse a number
        try {
            // return an instance of column class
            return timeFormat.parse(value);

        } catch (ParseException pex) {
            throw new RuntimeException(pex);
        }
    }


}
