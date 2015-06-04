package com.projects.gui;

import com.projects.models.IndividualModel;
import com.projects.models.OntologyModel;
import com.projects.models.WorldModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 * Created by Dan on 5/27/2015.
 */
public class InstancePanel extends JScrollPane implements SubscribedView
{
    JTable instanceTable;
    InstanceTable instancesTable;
    MouseListener selectionListener;

    public InstancePanel(MouseListener listener)
    {
        selectionListener = listener;
        instancesTable = new InstanceTable();
        instanceTable = new JTable(instancesTable);
        instanceTable.addMouseListener(selectionListener);
        instanceTable.setTableHeader(null);
        getViewport().add(instanceTable);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(WorldModel.PC_NEW_INSTANCE_ADDED))
        {
            IndividualModel instance = (IndividualModel)event.getNewValue();

            Object[] instances = {instance.getName()};
            instancesTable.addRow(instances);
        }
        else if (event.getPropertyName().equals(WorldModel.PC_INSTANCE_DELETED))
        {
            int row = ((Integer)event.getNewValue());
            instancesTable.removeRow(row);
        }
        else if (event.getPropertyName().equals(OntologyModel.PC_ONTOLOGY_CLEARED))
        {
            instancesTable.clearTable();
        }
    }
}
