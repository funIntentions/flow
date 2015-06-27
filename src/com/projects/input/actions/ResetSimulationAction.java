package com.projects.input.actions;

import com.projects.gui.table.StructureTable;
import com.projects.management.SystemController;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Dan on 6/26/2015.
 */
public class ResetSimulationAction extends AbstractAction
{
    private SystemController controller;

    public ResetSimulationAction(String text, ImageIcon icon, SystemController control)
    {
        super(text, icon);
        controller = control;
    }

    public void actionPerformed(ActionEvent event)
    {
        controller.resetSimulation();
    }
}
