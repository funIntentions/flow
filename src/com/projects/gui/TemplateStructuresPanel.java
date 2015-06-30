package com.projects.gui;

import com.projects.models.Structure;
import com.projects.models.Template;
import com.projects.systems.StructureManager;

import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.List;

/**
 * Created by Dan on 6/19/2015.
 */
public class TemplateStructuresPanel extends StructurePanel implements SubscribedView
{
    public TemplateStructuresPanel(MouseListener mouseListener)
    {
        super("Template Structures", mouseListener);
    }

     public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(StructureManager.PC_TEMPLATE_READY))
        {
            structureTable.clearTable();
            Template template = (Template)event.getNewValue();

            List<Structure> structures = template.getStructureTemplates();

            for (Structure structure : structures)
            {
                structureTable.addRow(structure);
            }
        }
        else if (event.getPropertyName().equals(StructureManager.PC_STRUCTURE_EDITED))
        {
            Structure structure = (Structure)event.getNewValue();

            int numberOfRows = structureTable.getRowCount();

            for (int i = 0; i < numberOfRows; ++i)
            {
                if ((structureTable.getRow(i)).getId().intValue() == structure.getId().intValue())
                {
                    structureTable.setRow(i, structure);
                }
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

}
