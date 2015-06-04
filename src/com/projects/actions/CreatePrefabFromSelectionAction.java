package com.projects.actions;

import com.projects.management.SystemController;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Dan on 6/3/2015.
 */
public class CreatePrefabFromSelectionAction extends AbstractAction
{
    SystemController controller;
    JTable individualsTable;

    public CreatePrefabFromSelectionAction(String text, ImageIcon icon, String desc, Integer mnemonic, JTable table, SystemController control)
    {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        controller = control;
        individualsTable = table;
    }

    public void actionPerformed(ActionEvent event)
    {
        controller.createPrefabFromSelection(individualsTable.getSelectedRows());
    }
}
