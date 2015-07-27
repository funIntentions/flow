package com.projects.gui.panel;

import com.projects.gui.DeviceTabbedPane;
import com.projects.gui.SubscribedView;
import com.projects.gui.table.PropertiesTable;
import com.projects.input.listeners.DeviceSelectedListener;
import com.projects.input.listeners.DeviceTableListener;
import com.projects.management.SystemController;
import com.projects.models.*;
import com.projects.systems.StructureManager;
import com.projects.systems.simulation.World;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dan on 5/27/2015.
 */
public class SelectionInfoPanel extends JPanel implements SubscribedView
{
    private ChartPanel chartPanel;
    private XYSeriesCollection data;
    private PropertiesTable devicePropertiesTableModel;
    private PropertiesTable structurePropertiesTableModel;
    private DeviceTabbedPane deviceTabbedPane;

    public SelectionInfoPanel(SystemController controller)
    {
        setLayout(new GridLayout(1,2));

        chartPanel = new ChartPanel(createChart());

        devicePropertiesTableModel = new PropertiesTable();
        JTable devicePropertiesTable = new JTable(devicePropertiesTableModel);

        structurePropertiesTableModel = new PropertiesTable();
        JTable structurePropertiesTable = new JTable(structurePropertiesTableModel);

        DeviceSelectedListener deviceSelectedListener = new DeviceSelectedListener(controller);
        DeviceTableListener deviceTableListener = new DeviceTableListener(controller);

        JPanel deviceAndPropertyPanel = new JPanel(new GridLayout(1,2));
        deviceTabbedPane = new DeviceTabbedPane(deviceTableListener, deviceSelectedListener);

        JScrollPane devicePropertiesScrollPane = new JScrollPane(devicePropertiesTable);
        JScrollPane structurePropertiesScrollPane = new JScrollPane(structurePropertiesTable);

        JPanel propertyPanel = new JPanel(new GridLayout(2,1));
        propertyPanel.add(devicePropertiesScrollPane);
        propertyPanel.add(structurePropertiesScrollPane);

        deviceAndPropertyPanel.add(deviceTabbedPane);
        deviceAndPropertyPanel.add(propertyPanel);

        add(chartPanel);
        add(deviceAndPropertyPanel);
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

        structurePropertiesTableModel.clearTable();
        List<Property> properties = structure.getProperties();

        for (Property property : properties)
        {
            Object[] row = {property.getName(), property.getValue(), property.getUnits()};
            structurePropertiesTableModel.addRow(row);
        }
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(World.PC_SELECTED_LOAD_PROFILE_CHANGED)
                || event.getPropertyName().equals(StructureManager.SELECTED_LOAD_PROFILE_CHANGED))
        {
            data.removeAllSeries();
            data.addSeries(makeLoadProfileDataSeries((List<Float>) event.getNewValue()));
        }
        else if (event.getPropertyName().equals(StructureManager.PC_TEMPLATE_SELECTED)
                || event.getPropertyName().equals(World.PC_STRUCTURE_SELECTED))
        {
            deviceTabbedPane.clearTables();
            devicePropertiesTableModel.clearTable();
            structurePropertiesTableModel.clearTable();

            populateStructureDevicesAndProperties((Structure)event.getNewValue());
        }
        else if (event.getPropertyName().equals(StructureManager.PC_DEVICE_SELECTED))
        {
            devicePropertiesTableModel.clearTable();

            Device device = (Device)event.getNewValue();
            List<Property> properties = device.getProperties();

            for (Property property : properties)
            {
                Object[] row = {property.getName(), property.getValue(), property.getUnits()};
                devicePropertiesTableModel.addRow(row);
            }
        }
    }

    private XYSeries calculateStructuresLoadProfileDataSeries(Structure structure)
    {
        XYSeries series = new XYSeries(structure.getName());

        long timeSpanLength = TimeUnit.MINUTES.toSeconds(30);
        int numTimeSpans = 48;
        long previous = 0;

        for (int time = 0; time < numTimeSpans; ++time)
        {
            java.util.List<Appliance> appliances = (java.util.List)structure.getAppliances();
            double usageDuringTimeSpan = 0;

            for (Appliance appliance : appliances)
            {
                usageDuringTimeSpan += appliance.getElectricityUsageSchedule().getElectricityUsageDuringSpan(new TimeSpan(previous, time * timeSpanLength)) > 0 ? appliance.getUsageConsumption() : 0;
            }

            previous = time * timeSpanLength;

            series.add(time + 1, usageDuringTimeSpan);
        }

        return series;
    }

    private XYSeries makeLoadProfileDataSeries(List<Float> loadProfile)
    {
        XYSeries series = new XYSeries("Structure");

        int length = loadProfile.size();
        for (int i = 0; i < length; ++i)
        {
            series.add(i, loadProfile.get(i));
        }

        return series;
    }

    private JFreeChart createChart()
    {
        XYSeries series1 = new XYSeries("Structure");
        data = new XYSeriesCollection(series1);
        return ChartFactory.createXYLineChart(
                "Load Profile",  // chart title
                "Time of Day",
                "Usage (Watts)",
                data
        );
    }
}
