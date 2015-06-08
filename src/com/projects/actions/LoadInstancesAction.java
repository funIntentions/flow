package com.projects.actions;

import com.projects.helper.Constants;
import com.projects.helper.Utils;
import com.projects.management.SystemController;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
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

        FileFilter owlFilter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                String extension = Utils.getExtension(f);
                if (extension != null && extension.equals(Constants.OWL))
                    return true;
                return false;
            }

            @Override
            public String getDescription() {
                return Constants.OWL;
            }
        };

        fileChooser.setFileFilter(owlFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);
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
