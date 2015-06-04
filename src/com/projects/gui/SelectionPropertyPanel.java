package com.projects.gui;

import com.projects.models.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;

/**
 * Created by Dan on 5/27/2015.
 */
public class SelectionPropertyPanel extends JPanel implements SubscribedView
{
    // TODO: eliminate the repetition between this here and the enum in SystemController
    enum selection_type
    {
        CLASS,
        INDIVIDUAL,
        INSTANCE,
        NONE
    }

    JTable propertyTable;
    JTextArea selectionDescription;
    PropertiesTable propertiesTable;
    JScrollPane propertyScrollPane;
    JScrollPane descriptionScrollPane;
    private selection_type currentlyDisplaying;

    public SelectionPropertyPanel(String borderName, TableModelListener propertiesTableListener)
    {
        setLayout(new GridLayout(0, 1));
        setBorder(BorderFactory.createTitledBorder(borderName));
        propertiesTable = new PropertiesTable();
        propertiesTable.addTableModelListener(propertiesTableListener);
        propertyTable = new JTable(propertiesTable)
        {
            private static final long serialVersionUID = 1L;
            private Class editingClass;

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                editingClass = null;
                int modelColumn = convertColumnIndexToModel(column);
                if (modelColumn == 1) {
                    Class rowClass = getModel().getValueAt(row, modelColumn).getClass();
                    return getDefaultRenderer(rowClass);
                } else {
                    return super.getCellRenderer(row, column);
                }
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                editingClass = null;
                int modelColumn = convertColumnIndexToModel(column);
                if (modelColumn == 1) {
                    editingClass = getModel().getValueAt(row, modelColumn).getClass();
                    return getDefaultEditor(editingClass);
                } else {
                    return super.getCellEditor(row, column);
                }
            }
            //  This method is also invoked by the editor when the value in the editor
            //  component is saved in the TableModel. The class was saved when the
            //  editor was invoked so the proper class can be created.

            @Override
            public Class getColumnClass(int column) {
                return editingClass != null ? editingClass : super.getColumnClass(column);
            }
        };
        selectionDescription = new JTextArea();
        selectionDescription.setLineWrap(true);
        selectionDescription.setEditable(false);
        selectionDescription.setWrapStyleWord(true);
        selectionDescription.setMinimumSize(new Dimension(50, 25));
        descriptionScrollPane = new JScrollPane(selectionDescription);
        descriptionScrollPane.setBorder(BorderFactory.createTitledBorder("Description"));
        propertyScrollPane = new JScrollPane(propertyTable);
        propertyScrollPane.setBorder(BorderFactory.createTitledBorder("Properties"));
        add(descriptionScrollPane);
        add(propertyScrollPane);
        currentlyDisplaying = selection_type.NONE;
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(OntologyModel.PC_NEW_INDIVIDUAL_SELECTED)
                || event.getPropertyName().equals(WorldModel.PC_NEW_INSTANCE_SELECTED))
        {
            if (event.getPropertyName().equals(OntologyModel.PC_NEW_INDIVIDUAL_SELECTED)) // TODO: make nicer
            {
                currentlyDisplaying = selection_type.INDIVIDUAL;
            }
            else
            {
                currentlyDisplaying = selection_type.INSTANCE;
            }

            propertiesTable.clearTable();
            IndividualModel model = (IndividualModel)event.getNewValue();
            Iterator<PropertyModel> i = model.listProperties();

            selectionDescription.setText(model.getDescription());

            while (i.hasNext())
            {
                PropertyModel property = i.next();
                Object[] row = {property.getName(), property.getValue()};
                propertiesTable.addRow(row);
            }
        }
        else if (event.getPropertyName().equals(OntologyModel.PC_NEW_CLASS_SELECTED))
        {
            propertiesTable.clearTable();

            ClassModel model = (ClassModel)event.getNewValue();
            selectionDescription.setText(model.getDescription());

            currentlyDisplaying = selection_type.CLASS;
        }
        else if (event.getPropertyName().equals(OntologyModel.PC_ONTOLOGY_CLEARED) || event.getPropertyName().equals(WorldModel.PC_WORLD_CLEARED))
        {
            propertiesTable.clearTable();
        }
        else if (event.getPropertyName().equals(WorldModel.PC_INSTANCE_DELETED))
        {
            if (currentlyDisplaying == selection_type.INSTANCE)
                propertiesTable.clearTable();
        }
    }
}
