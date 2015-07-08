package com.projects.input.actions;

import com.projects.management.SystemController;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Dan on 6/26/2015.
 */
public class ResetSimulationAction extends AbstractAction
{
    private SystemController controller;

    public ResetSimulationAction(SystemController control)
    {
        super("Reset", null);
        controller = control;
    }

    public void actionPerformed(ActionEvent event)
    {
        controller.resetSimulation();
    }
}
