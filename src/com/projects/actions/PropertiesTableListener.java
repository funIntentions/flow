package com.projects.actions;

import com.projects.management.SystemController;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Created by Dan on 6/1/2015.
 */
public class PropertiesTableListener implements TableModelListener
{
    SystemController controller;

    public PropertiesTableListener(SystemController control)
    {
        controller = control;
    }

    @Override
    public void tableChanged(TableModelEvent e)
    {
        if (e.getType() == TableModelEvent.UPDATE)
        {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (row >= 0 && column >= 0)
            {
                TableModel model = (TableModel)e.getSource();
                Object data = model.getValueAt(row, column);
                controller.selectionPropertyChanged(row, data);
            }
        }
    }
}
