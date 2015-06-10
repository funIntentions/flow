package com.projects.actions;

import com.projects.management.SystemController;
import com.projects.models.IndividualModel;
import com.projects.models.Prefab;

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
        if (nodeInfo instanceof IndividualModel)
        {
            IndividualModel model = (IndividualModel)nodeInfo;
            controller.worldIndividualSelected(model.getId());
        }
        else if (nodeInfo instanceof Prefab)
        {
            Prefab prefab = (Prefab)nodeInfo;
            controller.worldPrefabSelected(prefab.getId());
        }
    }
}
