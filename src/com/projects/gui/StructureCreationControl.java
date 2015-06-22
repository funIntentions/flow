package com.projects.gui;

import com.projects.actions.DevicePropertiesTableListener;
import com.projects.actions.DeviceSelectedListener;
import com.projects.gui.table.PropertiesTable;
import com.projects.helper.DeviceType;
import com.projects.helper.StructureType;
import com.projects.management.SystemController;
import com.projects.models.*;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.util.List;

/**
 * Created by Dan on 6/9/2015.
 */
public class StructureCreationControl implements SubscribedView
{
    private JLabel nameLabel;
    private JLabel suffixLabel;
    private JLabel infoLabel;
    private JTextField nameField;
    private JTextField suffixField;
    private JPanel creationPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private DeviceSelectedListener deviceSelectedListener;
    private DeviceTabbedPane deviceTabbedPane;
    private JDialog creationDialog;
    private PropertiesTable devicePropertiesTable;
    private PropertiesTable buildingPropertiesTable;
    private JTable propertyTable;
    private TableModelListener devicePropertiesTableListener;
    private SystemController controller;

    private JPanel deviceButtonPanel;
    private JPanel inputInfoPanel;
    private JPanel notificationPanel;
    private JScrollPane devicePropertiesScrollPane;
    private JScrollPane buildingPropertiesScrollPane;
    private JPanel creationControlButtons;

    private StructureType structureType;

    public StructureCreationControl(JFrame frame, SystemController systemController)
    {
        controller = systemController;
        nameField = new JTextField(14);
        suffixField = new JTextField(5);
        infoLabel = new JLabel("Enter a unique name and suffix.");
        nameLabel = new JLabel("Structure's Name: ");
        suffixLabel = new JLabel("Member Suffix: ");
        creationPanel = new JPanel(new GridLayout(1,2));
        leftPanel = new JPanel(new BorderLayout(10,10));
        rightPanel = new JPanel(new BorderLayout(10,10));

        createComponents();

        creationPanel.add(leftPanel);
        creationPanel.add(rightPanel);
        creationDialog = new JDialog(frame, "Structure Creation", true);
        creationDialog.setContentPane(creationPanel);
        creationDialog.pack();
        creationDialog.setSize(new Dimension(800, 600));
        creationDialog.setResizable(false);
        creationDialog.setVisible(false);
        structureType = StructureType.NO_STRUCTURE;

        creationDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                close();
            }
        });
    }

    public void display(Structure structureToCreate)
    {
        nameField.setText(structureToCreate.getName());
        structureType = structureToCreate.getType();

        switch (structureType)
        {
            case SINGLE_UNIT:
            {
                setupSingleUnitCreaterComponents();
            } break;
            case COMPOSITE_UNIT:
            {

            } break;
            case POWER_PLANT:
            {
                setupPowerPlantCreaterComponents(structureToCreate);
            } break;
        }

        creationDialog.setVisible(true);
    }

    private void close()
    {
        switch (structureType)
        {
            case SINGLE_UNIT:
            {
                removeSingleUnitCreaterComponents();
            } break;
            case COMPOSITE_UNIT:
            {

            } break;
            case POWER_PLANT:
            {
                removePowerPlantCreaterComponents();
            } break;
        }

        structureType = StructureType.NO_STRUCTURE;
        creationDialog.setVisible(false);
    }

    private void createComponents()
    {
        notificationPanel = new JPanel(new FlowLayout());
        notificationPanel.add(infoLabel);

        devicePropertiesTableListener = new DevicePropertiesTableListener(controller);
        devicePropertiesTable = new PropertiesTable();
        devicePropertiesTable.addTableModelListener(devicePropertiesTableListener);
        propertyTable = new JTable(devicePropertiesTable)
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

        devicePropertiesScrollPane = new JScrollPane(propertyTable);
        devicePropertiesScrollPane.setBorder(BorderFactory.createTitledBorder("Selected Device"));

        buildingPropertiesTable = new PropertiesTable();
        JTable buildingPropertyTable = new JTable(buildingPropertiesTable);
        buildingPropertiesScrollPane = new JScrollPane(buildingPropertyTable);
        buildingPropertiesScrollPane.setBorder(BorderFactory.createTitledBorder("Building Properties"));

        AbstractAction okAction = new AbstractAction("OK")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!conflictsExist())
                {
                    controller.addStructureToWorld();
                    close();
                }
            }
        };

        AbstractAction cancelAction = new AbstractAction("Cancel")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                close();
            }
        };

        creationControlButtons = new JPanel(new FlowLayout());
        creationControlButtons.add(new JButton(okAction));
        creationControlButtons.add(new JButton(cancelAction));

        inputInfoPanel = new JPanel(new FlowLayout());
        inputInfoPanel.add(nameLabel);
        inputInfoPanel.add(nameField);
        inputInfoPanel.add(Box.createHorizontalStrut(15));
        inputInfoPanel.add(suffixLabel);
        inputInfoPanel.add(suffixField);

        deviceSelectedListener = new DeviceSelectedListener(controller);
        deviceTabbedPane = new DeviceTabbedPane(deviceSelectedListener);
        deviceTabbedPane.setBorder(BorderFactory.createTitledBorder("Structure's Devices"));

        AbstractAction addApplianceAction = new AbstractAction("Add Appliance")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                controller.addDeviceToStructure(DeviceType.APPLIANCE);
            }
        };

        AbstractAction addSourceAction = new AbstractAction("Add Energy Source")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                controller.addDeviceToStructure(DeviceType.ENERGY_SOURCE);
            }
        };

        AbstractAction addStorageAction = new AbstractAction("Add Energy Storage")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                controller.addDeviceToStructure(DeviceType.ENERGY_STORAGE);
            }
        };

        deviceButtonPanel = new JPanel(new FlowLayout());
        deviceButtonPanel.add(new JButton(addApplianceAction));
        deviceButtonPanel.add(new JButton(addSourceAction));
        deviceButtonPanel.add(new JButton(addStorageAction));
    }

    private void setupSingleUnitCreaterComponents()
    {
        rightPanel.add(notificationPanel, BorderLayout.PAGE_START);
        rightPanel.add(devicePropertiesScrollPane, BorderLayout.CENTER);
        rightPanel.add(creationControlButtons, BorderLayout.PAGE_END);

        leftPanel.add(inputInfoPanel, BorderLayout.PAGE_START);
        leftPanel.add(deviceTabbedPane, BorderLayout.CENTER);
        leftPanel.add(deviceButtonPanel, BorderLayout.PAGE_END);
    }

    private void removeSingleUnitCreaterComponents()
    {
        rightPanel.remove(notificationPanel);
        rightPanel.remove(devicePropertiesScrollPane);
        rightPanel.remove(creationControlButtons);

        leftPanel.remove(inputInfoPanel);
        leftPanel.remove(deviceTabbedPane);
        leftPanel.remove(deviceButtonPanel);
    }

    private void setupCompositeUnitCreaterComponents()
    {

    }

    private void setupPowerPlantCreaterComponents(Structure structure)
    {
        rightPanel.add(notificationPanel, BorderLayout.PAGE_START);
        rightPanel.add(creationControlButtons, BorderLayout.PAGE_END);

        leftPanel.add(inputInfoPanel, BorderLayout.PAGE_START);
        leftPanel.add(buildingPropertiesScrollPane, BorderLayout.CENTER);

        buildingPropertiesTable.clearTable();
        List<PropertyModel> properties = structure.getProperties();

        for (PropertyModel property : properties)
        {
            Object[] row = {property.getName(), property.getValue()};
            buildingPropertiesTable.addRow(row);
        }
    }

    private void removePowerPlantCreaterComponents()
    {
        rightPanel.remove(notificationPanel);
        rightPanel.remove(creationControlButtons);

        leftPanel.remove(inputInfoPanel);
        leftPanel.remove(buildingPropertiesScrollPane);
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

    @Override
    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(TemplateManager.PC_ADD_DEVICE))
        {
            Device device = (Device)event.getNewValue();

            switch(device.getType())
            {
                case APPLIANCE:
                {
                    deviceTabbedPane.addAppliance(device);
                } break;
                case ENERGY_SOURCE:
                {
                    deviceTabbedPane.addEnergySource(device);
                } break;
                case ENERGY_STORAGE:
                {
                    deviceTabbedPane.addEnergyStorage(device);
                } break;
            }
        }
        else if (event.getPropertyName().equals(TemplateManager.PC_DEVICE_SELECTED))
        {
            devicePropertiesTable.clearTable();
            Device device = (Device)event.getNewValue();
            List<PropertyModel> properties = device.getProperties();

            for (PropertyModel property : properties)
            {
                Object[] row = {property.getName(), property.getValue()};
                devicePropertiesTable.addRow(row);
            }
        }
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
