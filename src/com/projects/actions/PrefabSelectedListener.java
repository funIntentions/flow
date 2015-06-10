package com.projects.actions;

import com.projects.management.SystemController;
import com.projects.models.IndividualModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Dan on 6/9/2015.
 */
public class PrefabSelectedListener extends MouseAdapter
{
    private SystemController controller;

    public PrefabSelectedListener(SystemController control)
    {
        controller = control;
    }

    public void mousePressed(MouseEvent event)
    {
        JTree prefabTree = (JTree)event.getSource();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           prefabTree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf() && (nodeInfo instanceof IndividualModel))
        {
            IndividualModel model = (IndividualModel)nodeInfo;
            controller.newIndividualSelected(model.getId(), true);
        }
    }
}
