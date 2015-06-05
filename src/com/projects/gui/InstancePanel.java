package com.projects.gui;

import com.projects.management.SystemController;
import com.projects.models.IndividualModel;
import com.projects.models.OntologyModel;
import com.projects.models.Prefab;
import com.projects.models.WorldModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 * Created by Dan on 5/27/2015.
 */
public class InstancePanel extends JScrollPane implements SubscribedView, TreeSelectionListener
{
    private JTree worldInstanceTree;
    private DefaultTreeModel worldInstanceTreeModel;
    private DefaultMutableTreeNode root;
    private SystemController control;

    public InstancePanel(SystemController controller)
    {
        root = new DefaultMutableTreeNode("World Instances");
        control = controller;

        worldInstanceTreeModel = new DefaultTreeModel(root);
        worldInstanceTree = new JTree(worldInstanceTreeModel);
        worldInstanceTree.setShowsRootHandles(true);
        worldInstanceTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        worldInstanceTree.addTreeSelectionListener(this);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        worldInstanceTree.setCellRenderer(renderer);

        getViewport().add(worldInstanceTree);
    }

    public void valueChanged(TreeSelectionEvent event)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           worldInstanceTree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf() && (nodeInfo instanceof IndividualModel))
        {
            IndividualModel model = (IndividualModel)nodeInfo;
            control.newInstanceSelected(model.getId());
        }
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(WorldModel.PC_NEW_INSTANCE_ADDED_FROM_PREFAB))
        {
            Prefab prefab = (Prefab)event.getNewValue();

            DefaultMutableTreeNode prefabNode = new DefaultMutableTreeNode(prefab);
            worldInstanceTreeModel.insertNodeInto(prefabNode, root, root.getChildCount());

            for (IndividualModel individual : prefab.getMembers())
            {
                DefaultMutableTreeNode memberNode = new DefaultMutableTreeNode(individual);
                worldInstanceTreeModel.insertNodeInto(memberNode, prefabNode, prefabNode.getChildCount());
            }
        }
        else if (event.getPropertyName().equals(WorldModel.PC_NEW_INSTANCE_ADDED))
        {
            IndividualModel model = (IndividualModel)event.getNewValue();
            DefaultMutableTreeNode instanceNode = new DefaultMutableTreeNode(model);
            worldInstanceTreeModel.insertNodeInto(instanceNode, root, root.getChildCount());
        }
        else if (event.getPropertyName().equals(OntologyModel.PC_ONTOLOGY_CLEARED))
        {
            root.removeAllChildren();
            worldInstanceTreeModel.reload();
        }
    }
}
    /*JTable instanceTable;
    InstanceTable instancesTable;
    MouseListener selectionListener;

    public InstancePanel(MouseListener listener)
    {
        selectionListener = listener;
        instancesTable = new InstanceTable();
        instanceTable = new JTable(instancesTable);
        instanceTable.addMouseListener(selectionListener);
        instanceTable.setTableHeader(null);
        getViewport().add(instanceTable);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(WorldModel.PC_NEW_INSTANCE_ADDED))
        {
            IndividualModel instance = (IndividualModel)event.getNewValue();
            instancesTable.addRow(instance);
        }
        else if (event.getPropertyName().equals(WorldModel.PC_INSTANCE_DELETED))
        {
            int id = ((Integer)event.getNewValue());
            int numRows = instancesTable.getRowCount();

            for (int row = 0; row < numRows; ++row)
            {
                IndividualModel model = instancesTable.getRow(row);
                if (id == model.getId())
                {
                    instancesTable.removeRow(row);
                    break;
                }
            }
        }
        else if (event.getPropertyName().equals(OntologyModel.PC_ONTOLOGY_CLEARED))
        {
            instancesTable.clearTable();
        }
    }
}*/
