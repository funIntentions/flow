package com.projects.actions;

import com.projects.management.SystemController;
import com.projects.models.IndividualModel;
import com.projects.models.Prefab;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;

/**
 * Created by Dan on 6/2/2015.
 */
public class DeleteSelectedInstanceAction extends AbstractAction
{
    SystemController controller;
    JTree instanceTree;

    public DeleteSelectedInstanceAction(String text, ImageIcon icon, String desc, Integer mnemonic, JTree tree, SystemController control)
    {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        controller = control;
        instanceTree = tree;
    }

    public void actionPerformed(ActionEvent event)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)instanceTree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();

        if (nodeInfo instanceof String) return;

        DefaultTreeModel treeModel = (DefaultTreeModel)instanceTree.getModel();
        treeModel.removeNodeFromParent(node);

        if (nodeInfo instanceof Prefab)
        {
            controller.removeSelectedInstancePrefab((Prefab)nodeInfo);
        }
        else if (nodeInfo instanceof IndividualModel)
        {
            controller.removeSelectedInstanceIndividual((IndividualModel)nodeInfo);
        }
    }
}
