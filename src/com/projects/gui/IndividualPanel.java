package com.projects.gui;

import com.projects.actions.CreateInstanceFromSelectionAction;
import com.projects.models.IndividualModel;
import com.projects.models.OntologyModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 * Created by Dan on 5/29/2015.
 */
public class IndividualPanel extends JScrollPane implements SubscribedView
{
    private JTable individualTable;
    private InstanceTable instancesTable;
    private MouseListener selectionListener;

    public IndividualPanel(MouseListener listener)
    {
        selectionListener = listener;
        instancesTable = new InstanceTable();
        individualTable = new JTable(instancesTable);
        individualTable.addMouseListener(selectionListener);
        individualTable.setTableHeader(null);
        getViewport().add(individualTable);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(OntologyModel.PC_NEW_ONTOLOGY_INDIVIDUALS_LOADED))
        {
            ArrayList<IndividualModel> instances = (ArrayList<IndividualModel>)event.getNewValue();

            for (IndividualModel model : instances)
            {
                instancesTable.addRow(model);
            }
        }
        else if (event.getPropertyName().equals(OntologyModel.PC_ONTOLOGY_CLEARED))
        {
            instancesTable.clearTable();
        }
    }

    public JTable getIndividualTable()
    {
        return individualTable;
    }
}
