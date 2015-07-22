package com.projects.gui.panel;

import com.projects.gui.SubscribedView;
import com.projects.gui.panel.StructurePanel;
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
                if ((structureTable.getRow(i)).getId() == structure.getId())
                {
                    structureTable.setRow(i, structure);
                }
            }
        }
    }

}
