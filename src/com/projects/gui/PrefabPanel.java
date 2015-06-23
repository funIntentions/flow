package com.projects.gui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;

/**
 * Created by Dan on 6/3/2015.
 */
public class PrefabPanel extends JScrollPane implements SubscribedView
{
    private String title;
    private JTree prefabTree;
    private DefaultTreeModel prefabTreeModel;
    private DefaultMutableTreeNode root;

    public PrefabPanel(MouseAdapter mouseAdapter)
    {
        title = "Prefabs";
        root = new DefaultMutableTreeNode(title);

        prefabTreeModel = new DefaultTreeModel(root);
        prefabTree = new JTree(prefabTreeModel);
        prefabTree.setShowsRootHandles(true);
        prefabTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        prefabTree.addMouseListener(mouseAdapter);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        prefabTree.setCellRenderer(renderer);

        getViewport().add(prefabTree);
    }

    public String getTitle()
    {
        return title;
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        /*if (event.getPropertyName().equals(OntologyModel.PC_NEW_PREFAB_CREATED))
        {
            Prefab prefab = (Prefab)event.getNewValue();

            DefaultMutableTreeNode prefabNode = new DefaultMutableTreeNode(prefab);
            prefabTreeModel.insertNodeInto(prefabNode, root, root.getChildCount());

            for (IndividualModel individual : prefab.getMembers())
            {
                DefaultMutableTreeNode memberNode = new DefaultMutableTreeNode(individual);
                prefabTreeModel.insertNodeInto(memberNode, prefabNode, prefabNode.getChildCount());
            }

        }
        else if (event.getPropertyName().equals(OntologyModel.PC_ONTOLOGY_PREFABS_LOADED)
                ||(event.getPropertyName().equals(OntologyModel.PC_ONTOLOGY_CLEARED)))
        {
            root.removeAllChildren();
            prefabTreeModel.reload();
        }*/
    }

    public JTree getPrefabTree()
    {
        return prefabTree;
    }
}
