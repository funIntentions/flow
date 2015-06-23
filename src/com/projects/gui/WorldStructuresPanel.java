package com.projects.gui;

import com.projects.models.Structure;
import com.projects.models.Template;
import com.projects.models.TemplateManager;
import com.projects.models.WorldModel;

import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.List;

/**
 * Created by Dan on 6/19/2015.
 */
public class WorldStructuresPanel extends StructurePanel implements SubscribedView
{
    public WorldStructuresPanel(MouseListener mouseListener)
    {
        super("World Structures", mouseListener);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(WorldModel.PC_NEW_STRUCTURE))
        {
            Structure structure = (Structure)event.getNewValue();
            structureTable.addRow(structure);
        }
        else if (event.getPropertyName().equals(TemplateManager.PC_STRUCTURE_EDITED))
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
    }
}