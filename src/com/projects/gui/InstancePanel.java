package com.projects.gui;

import com.projects.models.IndividualModel;
import com.projects.models.OntologyModel;
import com.projects.models.Prefab;
import com.projects.models.WorldModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;

/**
 * Created by Dan on 5/27/2015.
 */
public class InstancePanel extends JScrollPane implements SubscribedView
{
    private JTree worldInstanceTree;
    private DefaultTreeModel worldInstanceTreeModel;
    private DefaultMutableTreeNode root;

    public InstancePanel(MouseAdapter mouseAdapter)
    {
        root = new DefaultMutableTreeNode("World Instances");

        worldInstanceTreeModel = new DefaultTreeModel(root);
        worldInstanceTree = new JTree(worldInstanceTreeModel);
        worldInstanceTree.setShowsRootHandles(true);
        worldInstanceTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        worldInstanceTree.addMouseListener(mouseAdapter);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        worldInstanceTree.setCellRenderer(renderer);

        getViewport().add(worldInstanceTree);
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

    public JTree getWorldInstanceTree()
    {
        return worldInstanceTree;
    }
}
