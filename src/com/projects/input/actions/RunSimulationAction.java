package com.projects.input.actions;

import com.projects.management.SystemController;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Dan on 6/24/2015.
 */
public class RunSimulationAction extends AbstractAction
{
    SystemController controller;

    public RunSimulationAction(String text, Icon icon, SystemController systemController)
    {
        super(text, icon);
        controller = systemController;
    }

    public void actionPerformed(ActionEvent event)
    {
        controller.runSimulation();
    }
}
