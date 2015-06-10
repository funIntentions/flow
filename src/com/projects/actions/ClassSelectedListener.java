package com.projects.actions;

import com.projects.management.SystemController;
import com.projects.models.ClassModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Responsible for notifying the SystemController that a class has been selected.
 * Created by Dan on 6/1/2015.
 */
public class ClassSelectedListener extends MouseAdapter
{
    private SystemController controller;

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
        if (nodeInfo instanceof ClassModel)
        {
            ClassModel model = (ClassModel)nodeInfo;
            controller.classSelected(model.getId());
        }
    }
}
