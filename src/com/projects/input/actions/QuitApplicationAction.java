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

    public QuitApplicationAction(String text, ImageIcon icon, String desc, Integer mnemonic, SystemController control)
    {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        controller = control;
    }

    public void actionPerformed(ActionEvent event)
    {
        controller.quitApplication();
    }
}

