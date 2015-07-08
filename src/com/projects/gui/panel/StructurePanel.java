package com.projects.gui.panel;

import com.projects.gui.table.StructureTable;

import javax.swing.*;
import java.awt.event.MouseListener;

/**
 * Created by Dan on 5/29/2015.
 */
public class StructurePanel extends JScrollPane
{
    protected String title;
    protected JTable templateTable;
    protected StructureTable structureTable;
    protected MouseListener selectionListener;

    public StructurePanel(String title, MouseListener listener)
    {
        this.title = title;
        selectionListener = listener;
        structureTable = new StructureTable();
        templateTable = new JTable(structureTable);
        templateTable.addMouseListener(selectionListener);
        templateTable.setTableHeader(null);
        getViewport().add(templateTable);
    }

    public String getTitle()
    {
        return title;
    }

    public JTable getTemplateTable() {
        return templateTable;
    }

    public void setTemplateTable(JTable templateTable) {
        this.templateTable = templateTable;
    }

    public StructureTable getStructureTable() {
        return structureTable;
    }

    public void setStructureTable(StructureTable structureTable) {
        this.structureTable = structureTable;
    }
}
