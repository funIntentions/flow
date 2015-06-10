package com.projects.actions;

import com.projects.management.SystemController;
import com.projects.models.IndividualModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Dan on 5/29/2015.
 */
public class InstanceSelectedListener extends MouseAdapter
{
    private SystemController controller;

    public InstanceSelectedListener(SystemController control)
    {
        controller = control;
    }

    public void mousePressed(MouseEvent event)
    {
        JTree worldInstanceTree = (JTree)event.getSource();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           worldInstanceTree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf() && (nodeInfo instanceof IndividualModel))
        {
            IndividualModel model = (IndividualModel)nodeInfo;
            controller.newInstanceSelected(model.getId());
        }
    }
}
