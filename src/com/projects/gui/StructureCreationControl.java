package com.projects.gui;

import com.projects.actions.DevicePropertiesTableListener;
import com.projects.actions.DeviceSelectedListener;
import com.projects.actions.ObjectPropertiesTableListener;
import com.projects.gui.table.DeviceTable;
import com.projects.gui.table.ObjectTable;
import com.projects.gui.table.PropertiesTable;
import com.projects.helper.DeviceType;
import com.projects.helper.StructureType;
import com.projects.management.SystemController;
import com.projects.models.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    private JLabel unitNameLabel;
    private JLabel numberOfUnitsLabel;
    private JLabel infoLabel;
    private JTextField nameField;
    private JFormattedTextField numberOfUnitsField;
    private JPanel creationPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private DeviceSelectedListener deviceSelectedListener;
    private DeviceTabbedPane deviceTabbedPane;
    private DeviceTabbedPane unitDeviceTabbedPane;
    private JDialog creationDialog;
    private PropertiesTable devicePropertiesTable;
    private PropertiesTable buildingPropertiesTable;
    private JTable propertyTable;
    private TableModelListener devicePropertiesTableListener;
    private SystemController controller;

    private JPanel deviceButtonPanel;
    private JPanel inputUnitInfoPanel, inputCompositeUnitInfoPanel;
    private JPanel notificationPanel;
    private JScrollPane devicePropertiesScrollPane;
    private JScrollPane buildingPropertiesScrollPane;
    private JPanel creationControlButtons;

    private JPanel compositeUnitDevicePanes;

    private StructureType structureType;

    public StructureCreationControl(JFrame frame, SystemController systemController)
    {
        controller = systemController;
        nameField = new JTextField(14);
        numberOfUnitsField = new JFormattedTextField();
        numberOfUnitsField.setValue(1);
        numberOfUnitsField.setColumns(4);
        unitNameLabel = new JLabel("Unit Name: ");
        numberOfUnitsLabel = new JLabel("Number of Units: ");
        infoLabel = new JLabel("Enter a unique name and suffix.");
        nameLabel = new JLabel("Structure's Name: ");
        creationPanel = new JPanel(new GridLayout(1,2));
        leftPanel = new JPanel(new BorderLayout(10,10));
        rightPanel = new JPanel(new BorderLayout(10,10));

        createComponents();

        creationPanel.add(leftPanel);
        creationPanel.add(rightPanel);
        creationDialog = new JDialog(frame, "Structure Creation", true);
        creationDialog.setContentPane(creationPanel);
        creationDialog.setPreferredSize(new Dimension(800, 400));
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

    public void display(Structure structure)
    {
        nameField.setText(structure.getName());
        numberOfUnitsField.setValue(structure.getNumberOfUnits());
        structureType = structure.getType();

        switch (structureType)
        {
            case SINGLE_UNIT:
            {
                setupSingleUnitCreaterComponents(structure);
            } break;
            case COMPOSITE_UNIT:
            {
                setupCompositeUnitCreaterComponents(structure);
            } break;
            case POWER_PLANT:
            {
                setupPowerPlantCreaterComponents(structure);
            } break;
        }

        creationDialog.pack();
        creationDialog.setVisible(true);
    }

    private void completeEditing()
    {
        controller.editStructuresName(nameField.getText());
        controller.editStructuresNumberOfUnits(((Number) numberOfUnitsField.getValue()).intValue());
        nameField.setText("");
        numberOfUnitsField.setValue(1);
        controller.editingComplete();
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
                removeCompositeUnitCreaterComponents();
            } break;
            case POWER_PLANT:
            {
                removePowerPlantCreaterComponents();
            } break;
        }

        structureType = StructureType.NO_STRUCTURE;
        buildingPropertiesTable.clearTable();
        devicePropertiesTable.clearTable();
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

        ObjectPropertiesTableListener objectPropertiesTableListener = new ObjectPropertiesTableListener(controller);
        buildingPropertiesTable = new PropertiesTable();
        buildingPropertiesTable.addTableModelListener(objectPropertiesTableListener);
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
                    completeEditing();
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

        inputCompositeUnitInfoPanel = new JPanel(new GridLayout(2,2));
        inputCompositeUnitInfoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        inputUnitInfoPanel = new JPanel(new GridLayout(1,2));
        inputUnitInfoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        deviceSelectedListener = new DeviceSelectedListener(controller);
        deviceTabbedPane = new DeviceTabbedPane(deviceSelectedListener);
        deviceTabbedPane.setBorder(BorderFactory.createTitledBorder("Building's Devices"));
        deviceTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                devicePropertiesTable.clearTable();
            }
        });

        unitDeviceTabbedPane = new DeviceTabbedPane(deviceSelectedListener);
        unitDeviceTabbedPane.setBorder(BorderFactory.createTitledBorder("Unit's Devices"));

        compositeUnitDevicePanes = new JPanel(new GridLayout(2,1));
        compositeUnitDevicePanes.add(unitDeviceTabbedPane);
        compositeUnitDevicePanes.add(deviceTabbedPane);

        AbstractAction removeSelection = new AbstractAction("Remove Device")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DevicePanel devicePanel = (DevicePanel)deviceTabbedPane.getSelectedComponent();
                int selectedRow = devicePanel.getTable().getSelectedRow();
                if (selectedRow >= 0)
                {
                    DeviceTable deviceTable = (DeviceTable)devicePanel.getTable().getModel();
                    Device device =  deviceTable.getRow(selectedRow);
                    deviceTable.removeRow(selectedRow);

                    devicePropertiesTable.clearTable();
                    controller.removeDevice(device.getId());
                }
            }
        };

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

        deviceButtonPanel = new JPanel(new GridLayout(1, 4));
        deviceButtonPanel.add(new JButton(addApplianceAction));
        deviceButtonPanel.add(new JButton(addSourceAction));
        deviceButtonPanel.add(new JButton(addStorageAction));
        deviceButtonPanel.add(new JButton(removeSelection));
    }

    private void populateStructureDevicesAndProperties(Structure structure)
    {
        deviceTabbedPane.clearTables();

        List<Device> devices = structure.getAppliances();
        for (Device device : devices)
        {
            deviceTabbedPane.addAppliance(device);
        }

        devices = structure.getEnergySources();
        for (Device device : devices)
        {
            deviceTabbedPane.addEnergySource(device);
        }

        devices = structure.getEnergyStorageDevices();
        for (Device device : devices)
        {
            deviceTabbedPane.addEnergyStorage(device);
        }

        buildingPropertiesTable.clearTable();
        List<PropertyModel> properties = structure.getProperties();

        for (PropertyModel property : properties)
        {
            Object[] row = {property.getName(), property.getValue()};
            buildingPropertiesTable.addRow(row);
        }
    }

    private void setupSingleUnitCreaterComponents(Structure structure)
    {
        inputUnitInfoPanel.add(nameLabel);
        inputUnitInfoPanel.add(nameField);

        rightPanel.add(notificationPanel, BorderLayout.PAGE_START);
        rightPanel.add(devicePropertiesScrollPane, BorderLayout.CENTER);
        rightPanel.add(creationControlButtons, BorderLayout.PAGE_END);

        leftPanel.add(inputUnitInfoPanel, BorderLayout.PAGE_START);
        leftPanel.add(deviceTabbedPane, BorderLayout.CENTER);
        leftPanel.add(deviceButtonPanel, BorderLayout.PAGE_END);

        populateStructureDevicesAndProperties(structure);
    }

    private void removeSingleUnitCreaterComponents()
    {
        inputUnitInfoPanel.remove(nameLabel);
        inputUnitInfoPanel.remove(nameField);

        rightPanel.remove(notificationPanel);
        rightPanel.remove(devicePropertiesScrollPane);
        rightPanel.remove(creationControlButtons);

        leftPanel.remove(inputUnitInfoPanel);
        leftPanel.remove(deviceTabbedPane);
        leftPanel.remove(deviceButtonPanel);
    }

    private void setupCompositeUnitCreaterComponents(Structure structure)
    {
        inputCompositeUnitInfoPanel.add(nameLabel);
        inputCompositeUnitInfoPanel.add(nameField);
        //inputCompositeUnitInfoPanel.add(unitNameLabel);
        //inputCompositeUnitInfoPanel.add(unitNameField);
        inputCompositeUnitInfoPanel.add(numberOfUnitsLabel);
        inputCompositeUnitInfoPanel.add(numberOfUnitsField);

        rightPanel.add(notificationPanel, BorderLayout.PAGE_START);
        rightPanel.add(devicePropertiesScrollPane, BorderLayout.CENTER);
        rightPanel.add(creationControlButtons, BorderLayout.PAGE_END);

        leftPanel.add(inputCompositeUnitInfoPanel, BorderLayout.PAGE_START);
        leftPanel.add(deviceTabbedPane, BorderLayout.CENTER);
        leftPanel.add(deviceButtonPanel, BorderLayout.PAGE_END);

        populateStructureDevicesAndProperties(structure);
    }

    private void removeCompositeUnitCreaterComponents()
    {
        inputCompositeUnitInfoPanel.remove(nameLabel);
        inputCompositeUnitInfoPanel.remove(nameField);
        //inputCompositeUnitInfoPanel.remove(unitNameLabel);
        //inputCompositeUnitInfoPanel.remove(unitNameField);
        inputCompositeUnitInfoPanel.remove(numberOfUnitsLabel);
        inputCompositeUnitInfoPanel.remove(numberOfUnitsField);

        rightPanel.remove(notificationPanel);
        rightPanel.remove(devicePropertiesScrollPane);
        rightPanel.remove(creationControlButtons);

        leftPanel.remove(inputCompositeUnitInfoPanel);
        leftPanel.remove(deviceTabbedPane);
        leftPanel.remove(deviceButtonPanel);
    }

    private void setupPowerPlantCreaterComponents(Structure structure)
    {
        inputUnitInfoPanel.add(nameLabel);
        inputUnitInfoPanel.add(nameField);

        rightPanel.add(notificationPanel, BorderLayout.PAGE_START);
        rightPanel.add(creationControlButtons, BorderLayout.PAGE_END);

        leftPanel.add(inputUnitInfoPanel, BorderLayout.PAGE_START);
        leftPanel.add(buildingPropertiesScrollPane, BorderLayout.CENTER);

        populateStructureDevicesAndProperties(structure);
    }

    private void removePowerPlantCreaterComponents()
    {
        inputUnitInfoPanel.remove(nameLabel);
        inputUnitInfoPanel.remove(nameField);

        rightPanel.remove(notificationPanel);
        rightPanel.remove(creationControlButtons);

        leftPanel.remove(inputUnitInfoPanel);
        leftPanel.remove(buildingPropertiesScrollPane);
    }

    Boolean conflictsExist()
    {
        String newName = nameField.getText();

        if (newName.equals(""))
        {
            infoLabel.setText("Name cannot be blank");
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
}