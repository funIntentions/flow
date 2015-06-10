package com.projects.actions;

import com.projects.management.SystemController;
import com.projects.models.ClassModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Dan on 6/1/2015.
 */
public class ClassSelectedListener extends MouseAdapter
{
    SystemController controller;

    public ClassSelectedListener(SystemController control)
    {
        controller = control;
    }

    public void mousePressed(MouseEvent event)
    {
        JTree classTree = (JTree)event.getSource();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           classTree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf() && (nodeInfo instanceof ClassModel))
        {
            ClassModel model = (ClassModel)nodeInfo;
            controller.newClassSelected(model.getId());
        }
    }
}
