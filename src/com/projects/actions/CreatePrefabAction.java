package com.projects.actions;

import com.projects.gui.InstanceTable;
import com.projects.management.SystemController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by Dan on 6/3/2015.
 */
public class CreatePrefabAction extends AbstractAction
{
    SystemController controller;
    JTable individualsTable;

    public CreatePrefabAction(String text, ImageIcon icon, String desc, Integer mnemonic, JTable table, SystemController control)
    {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        controller = control;
        individualsTable = table;
    }

    public void actionPerformed(ActionEvent event)
    {
        int[] indexList = individualsTable.getSelectedRows();
        Integer[] idList = new Integer[indexList.length];

        InstanceTable tableModel = (InstanceTable)individualsTable.getModel();

        int i = 0;
        for (int index : indexList)
        {
            idList[i++] = (tableModel).getRow(index).getId();
        }

        controller.createPrefabFromSelection(idList);
    }
}
