package com.projects.gui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by Dan on 5/29/2015.
 */
public class InstanceTable extends AbstractTableModel
{
    private String[] columnNames = {"Instances"};
    private ArrayList<Object[]> data;

    public InstanceTable()
    {
        data = new ArrayList<Object[]>();
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

    public Object getValueAt(int row, int col) {
        return data.get(row)[col];
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public void addRow(Object[] row)
    {
        data.add(row);
        fireTableRowsInserted(data.size() - 1, data.size() - 1); // TODO: lookup API for this call
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
