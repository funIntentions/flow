package com.projects.gui.table;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by Dan on 7/23/2015.
 */
public class RankingTable extends AbstractTableModel
{
    private String[] columnNames;
    private ArrayList<Object[]> data;

    public RankingTable(String[] columnNames)
    {
        data = new ArrayList<Object[]>();
        this.columnNames = columnNames;
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
        if (c == 0)
        {
            return String.class;
        }
        else
        {
            return Double.class;
        }
    }

    public void addRow(Object[] row)
    {
        data.add(row);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public void clearTable()
    {
        data.clear();
        fireTableDataChanged();
    }
}
