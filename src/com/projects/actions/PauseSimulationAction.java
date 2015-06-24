package com.projects.actions;

import com.projects.management.SystemController;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Dan on 6/24/2015.
 */
public class PauseSimulationAction extends AbstractAction
{
    SystemController controller;

    public PauseSimulationAction(String text, Icon icon, SystemController systemController)
    {
        super(text, icon);
        controller = systemController;
    }

    public void actionPerformed(ActionEvent event)
    {
        controller.pauseSimulation();
    }
}
