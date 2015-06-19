package com.projects.actions;

import com.projects.gui.StructureCreationControl;
import com.projects.gui.table.StructureTable;
import com.projects.management.SystemController;
import com.projects.models.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.System;

/**
 * Created by Dan on 6/3/2015.
 */
public class CreateStructureAction extends AbstractAction
{
    private SystemController controller;
    private StructureTable structuresTable;
    private StructureCreationControl structureCreationControl;
    private JTable table;

    public CreateStructureAction(String text, ImageIcon icon, String desc, Integer mnemonic, StructureCreationControl structureCreationControl, StructureTable structuresTable, JTable table, SystemController control)
    {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        controller = control;
        this.structuresTable = structuresTable;
        this.table = table;
        this.structureCreationControl = structureCreationControl;
    }

    public void actionPerformed(ActionEvent event)
    {
        int i = table.getSelectedRow();
        Structure structure = structuresTable.getRow(i);

        structureCreationControl.display(structure);

        controller.addStructureToWorld(structure);
    }
}
