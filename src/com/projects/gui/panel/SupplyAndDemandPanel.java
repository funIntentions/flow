package com.projects.gui.panel;

import com.projects.gui.SubscribedView;
import com.projects.systems.simulation.SimulationStatus;
import com.projects.systems.simulation.World;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Hour;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Created by Dan on 7/13/2015.
 */
public class SupplyAndDemandPanel extends JPanel implements SubscribedView
{
    private ChartPanel supplyChartPanel;
    private ChartPanel demandChartPanel;
    private XYSeriesCollection supplyData;
    private DynamicTimeSeriesCollection demandData;

    public SupplyAndDemandPanel()
    {
        setLayout(new GridLayout(1,2));

        supplyChartPanel = new ChartPanel(createSupplyChart());
        demandChartPanel = new ChartPanel(createDemandChart());

        add(supplyChartPanel);
        add(demandChartPanel);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(World.PC_WORLD_UPDATE))
        {
            float[] newData = new float[1];
            SimulationStatus status = (SimulationStatus)event.getNewValue();
            newData[0] = (float)status.electricityDemand;

            demandData.advanceTime();
            demandData.appendData(newData);
        }
    }

    private JFreeChart createSupplyChart() {
        XYSeries series1 = new XYSeries("Coal");
        supplyData = new XYSeriesCollection(series1);
        return ChartFactory.createXYLineChart(
                "Supply",  // chart title
                "Time of Day",
                "Supply(kWh)",
                supplyData
        );
    }

    private JFreeChart createDemandChart() {
        demandData = new DynamicTimeSeriesCollection(1, 360, new Hour());
        demandData.setTimeBase(new Hour(1, 1, 1, 2015));
        float[] series = {0};
        demandData.addSeries(series, 0, "Usage");

        return ChartFactory.createTimeSeriesChart(
                "Demand",  // chart title
                "hh:mm",
                "Demand(kW)",
                demandData,
                true,
                true,
                false
        );
    }
}