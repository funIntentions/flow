package com.projects.input.actions;

import com.projects.helper.Constants;
import com.projects.helper.Utils;
import com.projects.management.SystemController;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by Dan on 6/12/2015.
 */
public class SaveFileAction extends AbstractAction
{
    private JFileChooser fileChooser;
    private JPanel owner;
    private SystemController controller;
    private String ext;

    public SaveFileAction(JPanel panel, SystemController control)
    {
        super("Save File", null);
        putValue(SHORT_DESCRIPTION, null);
        putValue(MNEMONIC_KEY, null);
        owner = panel;
        fileChooser = new JFileChooser();
        ext = Constants.SMART_GRID_FILE;

        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                String extension = Utils.getExtension(f);
                return extension != null && extension.equals(ext);
            }

            @Override
            public String getDescription() {
                return ext;
            }
        };

        fileChooser.setFileFilter(fileFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        controller = control;
    }

    public void actionPerformed(ActionEvent e)
    {
        File file;
        int result = fileChooser.showSaveDialog(owner);

        if (result == JFileChooser.APPROVE_OPTION)
        {
            file = fileChooser.getSelectedFile();

            if (!fileChooser.getSelectedFile().getAbsolutePath().endsWith(ext))
                file = new File (fileChooser.getSelectedFile() + "." + ext);

            if (ext.equals(Constants.SMART_GRID_FILE))
                controller.saveSimulation(file);
        }
        else
        {
            System.out.println("File Save Command Cancelled."); //TODO: log once logger is integrated
        }
    }
}
