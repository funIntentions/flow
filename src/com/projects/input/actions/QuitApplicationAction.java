package com.projects.input.actions;

import com.projects.management.SystemController;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Dan on 6/2/2015.
 */
public class QuitApplicationAction extends AbstractAction
{
     private SystemController controller;

    public QuitApplicationAction(SystemController control)
    {
        super("Quit", null);
        putValue(SHORT_DESCRIPTION, null);
        putValue(MNEMONIC_KEY, null);
        controller = control;
    }

    public void actionPerformed(ActionEvent event)
    {
        controller.quitApplication();
    }
}

