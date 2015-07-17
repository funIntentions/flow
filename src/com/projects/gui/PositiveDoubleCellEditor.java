package com.projects.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by Dan on 7/16/2015.
 */
public class PositiveDoubleCellEditor extends DefaultCellEditor
{
    private static final Border red = new LineBorder(Color.red);
    private static final Border black = new LineBorder(Color.black);
    NumberFormat doubleFormat;
    private JFormattedTextField textField;

    public PositiveDoubleCellEditor(final JFormattedTextField textField)
    {
        super(textField);
        this.textField = textField;
        this.textField.setHorizontalAlignment(JTextField.RIGHT);

        doubleFormat = new DecimalFormat("#.0");
        NumberFormatter doubleFormatter = new NumberFormatter(doubleFormat);
        doubleFormatter.setFormat(doubleFormat);

        textField.setFormatterFactory(new DefaultFormatterFactory(doubleFormatter));
    }

    @Override
    public boolean stopCellEditing()
    {

        String text = textField.getText();

        try
        {
            double v = Double.valueOf(text);

            if (v < 0)
            {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e)
        {
            textField.setBorder(red);
            return false;
        }

        return super.stopCellEditing();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value, boolean isSelected, int row, int column)
    {
        textField.setBorder(black);
        textField.setValue(value);
        return super.getTableCellEditorComponent(
                table, value, isSelected, row, column);
    }

    public Object getCellEditorValue() {
        JFormattedTextField ftf = (JFormattedTextField)getComponent();
        Object o = ftf.getValue();
        if (o instanceof Double)
        {
            return o;
        }
        else if (o instanceof Number)
        {
            return new Double(((Number)o).doubleValue());
        }
        else
        {
            try
            {
                return doubleFormat.parseObject(o.toString());
            }
            catch (ParseException exc)
            {
                System.err.println("getCellEditorValue: can't parse o: " + o);
                return null;
            }
        }
    }
}
