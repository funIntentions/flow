package com.projects.gui;

import com.projects.models.Structure;
import com.projects.models.Template;
import com.projects.models.TemplateManager;

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
        super("Structures", mouseListener);
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

}
