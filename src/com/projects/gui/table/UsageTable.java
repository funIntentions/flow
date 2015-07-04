package com.projects.gui.table;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Dan on 7/3/2015.
 */
public class UsageTable extends AbstractTableModel
{
    private String[] columnNames = {"From", "To"};
    private ArrayList<Object[]> data;

    public UsageTable()
    {
        data = new ArrayList<Object[]>();

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
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

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
       return true;
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        data.get(row)[col] = value;
        fireTableCellUpdated(row, col);
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
