package com.projects.actions;

import com.projects.management.SystemController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by Dan on 5/27/2015.
 */
public class LoadInstancesAction extends AbstractAction
{
    private JFileChooser fileChooser;
    private JPanel owner;
    private SystemController controller;

    public LoadInstancesAction(String text, ImageIcon icon, String desc, Integer mnemonic, JPanel panel, SystemController control)
    {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        owner = panel;
        fileChooser = new JFileChooser();
        controller = control;
    }

    public void actionPerformed(ActionEvent e)
    {
        File fileWithInstances;
        int result = fileChooser.showOpenDialog(owner);

        if (result == JFileChooser.APPROVE_OPTION)
        {
            fileWithInstances = fileChooser.getSelectedFile();

            controller.loadOntology(fileWithInstances);
        }
        else
        {
            System.out.println("File Open Command Cancelled."); //TODO: log once logger is integrated
        }
    }
}
