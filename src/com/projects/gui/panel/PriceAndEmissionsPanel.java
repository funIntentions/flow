package com.projects.gui.panel;

import com.projects.gui.SubscribedView;
import com.projects.models.Appliance;
import com.projects.models.Structure;
import com.projects.models.TimeSpan;
import com.projects.models.WorldTimer;
import com.projects.systems.simulation.SimulationStatus;
import com.projects.systems.simulation.SupplyManager;
import com.projects.systems.simulation.World;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dan on 7/14/2015.
 */
public class PriceAndEmissionsPanel extends JPanel implements SubscribedView
{
    private ChartPanel priceChartPanel;
    private XYSeriesCollection priceData;
    private ChartPanel emissionsChartPanel;
    private XYSeriesCollection emissionsData;

    public PriceAndEmissionsPanel()
    {
        setLayout(new GridLayout(1,2));

        priceChartPanel = new ChartPanel(createPriceChart());
        emissionsChartPanel = new ChartPanel(createEmissionsChart());

        add(priceChartPanel);
        add(emissionsChartPanel);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(World.PC_SUPPLY_MANAGER_UPDATED))
        {
            SupplyManager supplyManager = (SupplyManager)event.getNewValue();
            priceData.removeAllSeries();
        }
    }

    private XYSeries calculatePriceVsDemandSeries(Structure structure)
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

    private JFreeChart createPriceChart()
    {
        XYSeries series1 = new XYSeries("Price");
        priceData = new XYSeriesCollection(series1);
        return ChartFactory.createXYLineChart(
                "Price",  // chart title
                "Demand",
                "Price",
                priceData
        );
    }

    private JFreeChart createEmissionsChart()
    {

        XYSeries series1 = new XYSeries("Emissions");
        emissionsData = new XYSeriesCollection(series1);
        return ChartFactory.createXYLineChart(
                "Emissions",  // chart title
                "Demand",
                "Emissions (g/kWh)",
                emissionsData
        );    }
}
