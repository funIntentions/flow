package com.projects.gui;

import com.projects.models.IndividualModel;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by Dan on 5/29/2015.
 */
public class InstanceTable extends AbstractTableModel
{
    private String[] columnNames = {"Instances"};
    private ArrayList<IndividualModel> data;

    public InstanceTable()
    {
        data = new ArrayList<IndividualModel>();
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

    public Object getValueAt(int row, int col)
    {
        IndividualModel model = data.get(row);
        Object value;

        switch(col)
        {
            case 0:
            {
                value = model.getName();
            } break;
            default:
            {
                value = "default";
            }
        }

        return value;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public void addRow(IndividualModel row)
    {
        data.add(row);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public IndividualModel getRow(int row)
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
