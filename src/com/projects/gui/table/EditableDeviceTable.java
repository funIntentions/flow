package com.projects.gui.table;

/**
 * Created by Dan on 7/27/2015.
 */
public class EditableDeviceTable extends DeviceTable
{
    @Override
    public boolean isCellEditable(int row, int col)
    {
        return (col == 0);
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        data.get(row).setName(value.toString());
        fireTableCellUpdated(row, col);
    }
}
