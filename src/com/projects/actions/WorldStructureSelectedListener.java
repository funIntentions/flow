package com.projects.actions;

import com.projects.gui.table.StructureTable;
import com.projects.management.SystemController;
import com.projects.models.Structure;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Dan on 5/29/2015.
 */
public class WorldStructureSelectedListener extends MouseAdapter
{
    private SystemController controller;

    public WorldStructureSelectedListener(SystemController control)
    {
        controller = control;
    }

    public void mousePressed(MouseEvent event)
    {
        JTable target = (JTable)event.getSource();
        Structure structure = ((StructureTable)target.getModel()).getRow(target.getSelectedRow());

        controller.selectWorldStructure(structure);
    }
}
