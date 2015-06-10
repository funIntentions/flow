package com.projects.gui;

import com.projects.models.ClassModel;
import com.projects.models.OntologyModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.util.List;

/**
 * Created by Dan on 6/1/2015.
 */
public class ClassPanel extends JScrollPane implements SubscribedView
{
    private JTree classTree;
    private DefaultTreeModel classTreeModel;
    private DefaultMutableTreeNode root;

    public ClassPanel(MouseAdapter mouseAdapter)
    {
        root = new DefaultMutableTreeNode("Classes");

        classTreeModel = new DefaultTreeModel(root);
        classTree = new JTree(classTreeModel);
        classTree.setShowsRootHandles(true);
        classTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        classTree.addMouseListener(mouseAdapter);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        classTree.setCellRenderer(renderer);

        getViewport().add(classTree);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(OntologyModel.PC_NEW_ONTOLOGY_CLASSES_LOADED))
        {
            ClassModel rootClass = (ClassModel)event.getNewValue();

            List<ClassModel> children = rootClass.getChildren();
            for (ClassModel child : children)
            {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
                classTreeModel.insertNodeInto(childNode, root, root.getChildCount());
                addSubclasses(child, childNode);
            }
        }
        else if (event.getPropertyName().equals(OntologyModel.PC_ONTOLOGY_CLEARED))
        {
            root.removeAllChildren();
            classTreeModel.reload();
        }
    }

    void addSubclasses(ClassModel parent, DefaultMutableTreeNode parentNode)
    {
        List<ClassModel> children = parent.getChildren();

        for (ClassModel child : children)
        {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
            classTreeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            addSubclasses(child, childNode);
        }
    }
}
