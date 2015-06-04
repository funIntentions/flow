package com.projects.gui;

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.projects.actions.PropertiesTableListener;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by Dan on 5/28/2015.
 */
public class PropertiesTable extends AbstractTableModel
{
    private String[] columnNames = {"Property",
                                        "Value"};
    private ArrayList<Object[]> data;

    public PropertiesTable()
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

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
       return (col != 0);
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

    public void clearTable()
    {
        data.clear();
        fireTableDataChanged();
    }
}
