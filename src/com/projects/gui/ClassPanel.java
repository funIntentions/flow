package com.projects.gui;

import com.projects.actions.CreateInstanceFromSelectionAction;
import com.projects.models.ClassModel;
import com.projects.models.IndividualModel;
import com.projects.models.OntologyModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 * Created by Dan on 6/1/2015.
 */
public class ClassPanel extends JScrollPane implements SubscribedView
{
    // Change this to ClassList...
    JList<String> instanceList; // TODO: restructure so table instead of JList is used. Also make Jlist prettier...
    ListSelectionListener selectionListener;

    public ClassPanel(ListSelectionListener listener)
    {
        String[] list = {};
        selectionListener = listener;
        instanceList = createList(list);
        getViewport().add(instanceList);
    }

    public JList<String> createList(String[] list)
    {
        JList<String> newList = new JList<String>(list);
        newList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        newList.setLayoutOrientation(JList.VERTICAL);
        newList.setVisibleRowCount(-1);
        newList.addListSelectionListener(selectionListener);
        return newList;
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(OntologyModel.PC_NEW_ONTOLOGY_CLASSES_LOADED))
        {
            ArrayList<ClassModel> instances = (ArrayList<ClassModel>)event.getNewValue();

            int length = instances.size();
            String[] list = new String[length];

            for (int i = 0; i < length; ++i)
            {
                list[i] = instances.get(i).getName();
            }

            getViewport().remove(instanceList);
            instanceList = createList(list);
            getViewport().add(instanceList);

        }
        else if (event.getPropertyName().equals(OntologyModel.PC_ONTOLOGY_CLEARED))
        {
            getViewport().remove(instanceList);
        }
    }
}
