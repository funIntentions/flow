package com.projects.input.actions;

import com.projects.gui.table.StructureTable;
import com.projects.management.SystemController;
import com.projects.models.Structure;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Dan on 6/2/2015.
 */
public class RemoveSelectedStructureAction extends AbstractAction
{
    private SystemController controller;
    private StructureTable structuresTable;
    private JTable table;

    public RemoveSelectedStructureAction(StructureTable structuresTable, JTable table, SystemController control)
    {
        super("Remove Structure", null);
        putValue(SHORT_DESCRIPTION, null);
        putValue(MNEMONIC_KEY, null);
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
            structuresTable.removeRow(i);

            controller.removeWorldStructure(structure.getId());
        }
    }
}
