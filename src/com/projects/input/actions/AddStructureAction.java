package com.projects.input.actions;

import com.projects.gui.table.StructureTable;
import com.projects.management.SystemController;
import com.projects.models.*;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Dan on 6/3/2015.
 */
public class AddStructureAction extends AbstractAction
{
    private SystemController controller;
    private StructureTable structuresTable;
    private JTable table;

    public AddStructureAction(String text, ImageIcon icon, String desc, Integer mnemonic, StructureTable structuresTable, JTable table, SystemController control)
    {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        controller = control;
        this.structuresTable = structuresTable;
        this.table = table;
    }

    public void actionPerformed(ActionEvent event)
    {
        int i = table.getSelectedRow();
        if (i >= 0)
        {
            Structure structure = structuresTable.getRow(i);

            controller.addStructureToWorld(structure.getId());
        }
    }
}