package com.projects.actions;

import com.projects.gui.table.StructureTable;
import com.projects.management.SystemController;
import com.projects.models.IndividualModel;
import com.projects.models.Structure;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Dan on 5/28/2015.
 */
public class StructureSelectedListener extends MouseAdapter
{
    private SystemController controller;

    public StructureSelectedListener(SystemController control)
    {
        controller = control;
    }

    public void mousePressed(MouseEvent event)
    {
        JTable target = (JTable)event.getSource();
        Structure structure = ((StructureTable)target.getModel()).getRow(target.getSelectedRow());
        controller.structureTemplateSelected(structure);
        //controller.ontologyIndividualSelected(model.getId(), false);
    }
}