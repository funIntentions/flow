package com.projects.actions;

import com.projects.gui.table.StructureTable;
import com.projects.management.SystemController;
import com.projects.models.Structure;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;

/**
 * Created by Dan on 6/2/2015.
 */
public class RemoveSelectedStructureAction extends AbstractAction
{
    private SystemController controller;
    private StructureTable structuresTable;
    private JTable table;

    public RemoveSelectedStructureAction(String text, ImageIcon icon, String desc, Integer mnemonic, StructureTable structuresTable, JTable table, SystemController control)
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
        Structure structure = structuresTable.getRow(i);
        structuresTable.removeRow(i);

        controller.removeWorldStructure(structure.getId());
    }
}
