package com.projects.input.actions;

import com.projects.management.SystemController;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Dan on 6/26/2015.
 */
public class LoadDefaultAction extends AbstractAction
{
    SystemController controller;

    public LoadDefaultAction(SystemController systemController)
    {
        super("Open Default", null);
        controller = systemController;
    }

    public void actionPerformed(ActionEvent event)
    {
        controller.loadDefault();
    }
}
