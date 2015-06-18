package com.projects.gui;

import com.projects.gui.table.PropertiesTable;
import com.projects.helper.StructureType;
import com.projects.models.Prefab;
import com.projects.models.Template;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * Created by Dan on 6/9/2015.
 */
public class StructureCreationControl
{
    public static final int OK = 0;
    private static final int NOT_OK = 1;

    private JLabel nameLabel;
    private JLabel suffixLabel;
    private JLabel infoLabel;
    private JTextField nameField;
    private JTextField suffixField;
    private JPanel creationPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JDialog creationDialog;
    //private Collection<Prefab> prefabs;
    PropertiesTable propertiesTable;
    JTable propertyTable;
    TableModelListener propertiesTableListener;
    private int result;

    public StructureCreationControl(JFrame frame)//, StructureType type, Template template)
    {
        //prefabs = prefabList;
        nameField = new JTextField(14);
        suffixField = new JTextField(5);
        infoLabel = new JLabel("Enter a unique name and suffix.");
        nameLabel = new JLabel("Prefab's Name: ");
        suffixLabel = new JLabel("Member Suffix: ");
        creationPanel = new JPanel(new GridLayout(1,2));
        leftPanel = createLeftPanel();
        rightPanel = createRightPanel();


        creationPanel.add(leftPanel);
        creationPanel.add(rightPanel);
        creationDialog = new JDialog(frame, "Structure Creation", true);
        creationDialog.setContentPane(creationPanel);
        creationDialog.pack();
        creationDialog.setSize(new Dimension(800, 600));
        creationDialog.setResizable(false);
        creationDialog.setVisible(false);
    }

    public void display()
    {
        creationDialog.setVisible(true);
    }

    private JPanel createRightPanel()
    {
        JPanel right = new JPanel(new BorderLayout(10,10));

        JPanel infoPanel = new JPanel(new FlowLayout());
        infoPanel.add(infoLabel);
        right.add(infoPanel, BorderLayout.PAGE_START);

        propertiesTable = new PropertiesTable();
        //propertiesTable.addTableModelListener(propertiesTableListener); TODO: look into a special listener for this?
        propertyTable = new JTable(propertiesTable)
        {
            private static final long serialVersionUID = 1L;
            private Class editingClass;

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                editingClass = null;
                int modelColumn = convertColumnIndexToModel(column);
                if (modelColumn == 1)
                {
                    Object value = getModel().getValueAt(row, modelColumn);
                    if (value == null)
                    {
                        value = 0.0;
                        getModel().setValueAt(value, row, column);
                    }
                    Class rowClass = value.getClass();
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

        JScrollPane propertiesScrollPane = new JScrollPane(propertyTable);
        right.add(propertiesScrollPane, BorderLayout.CENTER);

        AbstractAction okAction = new AbstractAction("OK")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!conflictsExist())
                {
                    creationDialog.setVisible(false);
                    result = OK;
                }
            }
        };

        AbstractAction cancelAction = new AbstractAction("Cancel")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                result = NOT_OK;
                creationDialog.setVisible(false);
            }
        };

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JButton(okAction));
        buttonPanel.add(new JButton(cancelAction));
        right.add(buttonPanel, BorderLayout.PAGE_END);

        return right;
    }

    private JPanel createLeftPanel()
    {
        JPanel left = new JPanel(new BorderLayout(10,10));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(Box.createHorizontalStrut(15));
        inputPanel.add(suffixLabel);
        inputPanel.add(suffixField);
        left.add(inputPanel, BorderLayout.PAGE_START);

        DeviceTabbedPane deviceTabbedPane = new DeviceTabbedPane();
        left.add(deviceTabbedPane, BorderLayout.CENTER);

        AbstractAction addApplianceAction = new AbstractAction("Add Appliance")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!conflictsExist())
                {
                    creationDialog.setVisible(false);
                    result = OK;
                }
            }
        };

        AbstractAction addSourceAction = new AbstractAction("Add Energy Source")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                result = NOT_OK;
                creationDialog.setVisible(false);
            }
        };

        AbstractAction addStorageAction = new AbstractAction("Add Energy Storage")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                result = NOT_OK;
                creationDialog.setVisible(false);
            }
        };

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JButton(addApplianceAction));
        buttonPanel.add(new JButton(addSourceAction));
        buttonPanel.add(new JButton(addStorageAction));
        left.add(buttonPanel, BorderLayout.PAGE_END);

        return left;
    }

    Boolean conflictsExist()
    {
        String newName = nameField.getText();
        String newSuffix = suffixField.getText();

        if (newName.equals(""))
        {
            infoLabel.setText("Name cannot be blank");
            return true;
        }

        if (newSuffix.equals(""))
        {
            infoLabel.setText("Suffix cannot be blank");
            return true;
        }

        /*for (Prefab prefab : prefabs)
        {
            if (prefab.getName().equals(newName))
            {
                infoLabel.setText("A prefab with this name already exists");
                return true;
            }

            if (prefab.getMemberSuffix().equals(newSuffix))
            {
                infoLabel.setText("A prefab with this suffix already exists");
                return true;
            }
        }*/

        return false;
    }

    public int getResult()
    {
        return result;
    }

    public String getName()
    {
        return nameField.getText();
    }

    public String getSuffix()
    {
        return suffixField.getText();
    }
}
