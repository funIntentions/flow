package com.projects.gui.panel;

import com.projects.gui.SubscribedView;
import com.projects.models.Appliance;
import com.projects.models.Structure;
import com.projects.models.TimeSpan;
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

    public SelectionInfoPanel()
    {
        setLayout(new BorderLayout());

        chartPanel = new ChartPanel(createChart());

        add(chartPanel, BorderLayout.CENTER);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        /*if (event.getPropertyName().equals(World.PC_STRUCTURE_SELECTED)
                || event.getPropertyName().equals(World.PC_STRUCTURE_UPDATE)
                || event.getPropertyName().equals(StructureManager.PC_TEMPLATE_SELECTED)
                || event.getPropertyName().equals(StructureManager.PC_STRUCTURE_EDITED))*/
        if (event.getPropertyName().equals(World.PC_SELECTED_LOAD_PROFILE_CHANGED)
                || event.getPropertyName().equals(StructureManager.SELECTED_LOAD_PROFILE_CHANGED))
        {
            data.removeAllSeries();
            data.addSeries(makeLoadProfileDataSeries((List<Float>) event.getNewValue()));
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
                usageDuringTimeSpan += appliance.getElectricityUsageSchedule().getElectricityUsageDuringSpan(new TimeSpan(previous, time * timeSpanLength)) > 0 ? appliance.getAverageConsumption() : 0;
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
