package com.projects.gui;


import com.projects.models.IndividualModel;
import com.projects.models.OntologyModel;
import com.projects.models.Prefab;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.beans.PropertyChangeEvent;

/**
 * Created by Dan on 6/3/2015.
 */
public class PrefabPanel extends JScrollPane implements SubscribedView, TreeSelectionListener
{
    private JTree prefabTree;
    private DefaultTreeModel prefabTreeModel;
    private DefaultMutableTreeNode root;

    public PrefabPanel()
    {
        root = new DefaultMutableTreeNode("Prefabs");

        prefabTreeModel = new DefaultTreeModel(root);
        prefabTree = new JTree(prefabTreeModel);
        prefabTree.setShowsRootHandles(true);
        prefabTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        prefabTree.addTreeSelectionListener(this);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        prefabTree.setCellRenderer(renderer);

        getViewport().add(prefabTree);
    }

    public void createNodes(DefaultMutableTreeNode root)
    {
    }

    public void valueChanged(TreeSelectionEvent event)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           prefabTree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) // TODO: check to see if this is a String or not before the cast
        {
            IndividualModel model = (IndividualModel)nodeInfo;
        }
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(OntologyModel.PC_NEW_PREFAB_CREATED))
        {
            Prefab prefab = (Prefab)event.getNewValue();

            DefaultMutableTreeNode prefabNode = new DefaultMutableTreeNode(prefab.getName());
            prefabTreeModel.insertNodeInto(prefabNode, root, root.getChildCount());

            for (IndividualModel individual : prefab.getMembers())
            {
                DefaultMutableTreeNode memberNode = new DefaultMutableTreeNode(individual);
                prefabTreeModel.insertNodeInto(memberNode, prefabNode, prefabNode.getChildCount());
            }

        }
    }
}
