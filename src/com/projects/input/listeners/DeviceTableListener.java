package com.projects.input.listeners;

import com.projects.management.SystemController;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Created by Dan on 6/25/2015.
 */
public class DeviceTableListener implements TableModelListener
{
    SystemController controller;

    public DeviceTableListener(SystemController systemController)
    {
        controller = systemController;
    }

    public void tableChanged(TableModelEvent e)
    {
        if (e.getType() == TableModelEvent.UPDATE)
        {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (row >= 0 && column >= 0)
            {
                TableModel model = (TableModel)e.getSource();
                Object value = model.getValueAt(row, column);
                controller.editDeviceName(row, value.toString());
            }
        }
    }
}
