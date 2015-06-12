package com.projects.gui;

import com.projects.models.OntologyModel;
import com.projects.models.PropertyNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.util.List;

/**
 * Created by Dan on 6/11/2015.
 */
public class DataPropertyPanel extends JScrollPane implements SubscribedView
{
    private String title;
    private JTree propertyTree;
    private DefaultTreeModel propertyTreeModel;
    private DefaultMutableTreeNode root;

    public DataPropertyPanel(MouseAdapter mouseAdapter)
    {
        title = "Data Properties";
        root = new DefaultMutableTreeNode(title);

        propertyTreeModel = new DefaultTreeModel(root);
        propertyTree = new JTree(propertyTreeModel);
        propertyTree.setShowsRootHandles(true);
        propertyTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        propertyTree.addMouseListener(mouseAdapter);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        propertyTree.setCellRenderer(renderer);

        getViewport().add(propertyTree);
    }

    public String getTitle()
    {
        return title;
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(OntologyModel.PC_NEW_ONTOLOGY_DATA_PROPERTIES_LOADED))
        {
            PropertyNode rootClass = (PropertyNode)event.getNewValue();

            List<PropertyNode> children = rootClass.getChildren();
            for (PropertyNode child : children)
            {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
                propertyTreeModel.insertNodeInto(childNode, root, root.getChildCount());
                addSubclasses(child, childNode);
            }
        }
        else if (event.getPropertyName().equals(OntologyModel.PC_ONTOLOGY_CLEARED))
        {
            root.removeAllChildren();
            propertyTreeModel.reload();
        }
    }

    void addSubclasses(PropertyNode parent, DefaultMutableTreeNode parentNode)
    {
        List<PropertyNode> children = parent.getChildren();

        for (PropertyNode child : children)
        {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
            propertyTreeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            addSubclasses(child, childNode);
        }
    }
}
