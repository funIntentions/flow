package com.projects.gui.table;

import com.projects.models.IndividualModel;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by Dan on 5/29/2015.
 */
public abstract class ObjectTable<T> extends AbstractTableModel
{
    protected String[] columnNames = {"Instances"};
    protected ArrayList<T> data;

    public ObjectTable()
    {
        data = new ArrayList<T>();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public abstract Object getValueAt(int row, int col);

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public void addRow(T row)
    {
        data.add(row);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public T getRow(int row)
    {
        return data.get(row);
    }

    public void removeRow(int row)
    {
        data.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void clearTable()
    {
        data.clear();
        fireTableDataChanged();
    }
}
