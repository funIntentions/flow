package com.projects.gui.panel;

import com.projects.gui.ImprovedFormattedTextField;
import com.projects.gui.table.DeviceTable;
import com.projects.gui.ImprovedTableCellEditor;
import com.projects.gui.table.StringFormat;
import com.projects.models.Device;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import java.awt.event.MouseListener;

/**
 * Created by Dan on 6/18/2015.
 */
public class DevicePanel extends JScrollPane
{
    private String title;
    private JTable templateTable;
    private DeviceTable deviceTable;

    public DevicePanel(String panelTitle, TableModelListener tableModelListener, MouseListener mouseListener)
    {
        title = panelTitle;
        deviceTable = new DeviceTable();
        deviceTable.addTableModelListener(tableModelListener);
        templateTable = new JTable(deviceTable)
        {
            ImprovedTableCellEditor improvedTableCellEditor = new ImprovedTableCellEditor(new ImprovedFormattedTextField(new StringFormat())); // TODO: fix error in removing devices while editing them

            @Override
            public TableCellEditor getCellEditor(int row, int column)
            {
               return improvedTableCellEditor;
            }
        };
        templateTable.addMouseListener(mouseListener);
        templateTable.setTableHeader(null);
        getViewport().add(templateTable);
    }

    public String getTitle()
    {
        return title;
    }

    public void addDevice(Device device)
    {
        deviceTable.addRow(device);
    }

    public void clearTable()
    {
        deviceTable.clearTable();
    }

    public JTable getTable()
    {
        return templateTable;
    }
}
