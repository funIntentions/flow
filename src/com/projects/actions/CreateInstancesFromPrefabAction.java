package com.projects.actions;

import com.projects.management.SystemController;
import com.projects.models.Prefab;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;

/**
 * Created by Dan on 6/4/2015.
 */
public class CreateInstancesFromPrefabAction extends AbstractAction
{
    SystemController controller;
    JTree tree;

    public CreateInstancesFromPrefabAction(String text, ImageIcon icon, String desc, Integer mnemonic, JTree prefabTree, SystemController control)
    {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        controller = control;
        tree = prefabTree;
    }

    public void actionPerformed(ActionEvent event)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
        if (nodeInfo instanceof Prefab)
        {
            controller.createPrefabInstancesFromPrefab((Prefab)nodeInfo);
        }
    }
}
