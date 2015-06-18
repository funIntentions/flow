package com.projects.gui;

import com.projects.gui.table.StructureTable;
import com.projects.models.*;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 5/29/2015.
 */
public class StructurePanel extends JScrollPane implements SubscribedView
{
    private String title;
    private JTable templateTable;
    private StructureTable structureTable;
    private MouseListener selectionListener;

    public StructurePanel(MouseListener listener)
    {
        title = "Structures";
        selectionListener = listener;
        structureTable = new StructureTable();
        templateTable = new JTable(structureTable);
        templateTable.addMouseListener(selectionListener);
        templateTable.setTableHeader(null);
        getViewport().add(templateTable);
    }

    public String getTitle()
    {
        return title;
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(TemplateManager.PC_TEMPLATE_READY))
        {
            Template template = (Template)event.getNewValue();

            List<Structure> structures = template.getStructureTemplates();

            for (Structure structure : structures)
            {
                structureTable.addRow(structure);
            }
        }

        /*if (event.getPropertyName().equals(OntologyModel.PC_NEW_ONTOLOGY_INDIVIDUALS_LOADED))
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
        }*/
    }

    public JTable getTemplateTable() {
        return templateTable;
    }

    public void setTemplateTable(JTable templateTable) {
        this.templateTable = templateTable;
    }

    public StructureTable getStructureTable() {
        return structureTable;
    }

    public void setStructureTable(StructureTable structureTable) {
        this.structureTable = structureTable;
    }
}
