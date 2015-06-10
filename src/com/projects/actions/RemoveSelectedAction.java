package com.projects.actions;

import com.projects.management.SystemController;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;

/**
 * Created by Dan on 6/2/2015.
 */
public class RemoveSelectedAction extends AbstractAction
{
    private SystemController controller;
    private JTree tree;

    public RemoveSelectedAction(String text, ImageIcon icon, String desc, Integer mnemonic, JTree t, SystemController control)
    {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        controller = control;
        tree = t;
    }

    public void actionPerformed(ActionEvent event)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();

        if (nodeInfo instanceof String) return;

        DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
        treeModel.removeNodeFromParent(node);

        controller.removeModel(nodeInfo);
    }
}
