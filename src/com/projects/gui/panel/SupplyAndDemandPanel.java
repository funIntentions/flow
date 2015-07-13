package com.projects.gui.panel;

import com.projects.gui.SubscribedView;
import com.projects.models.WorldTimer;
import com.projects.systems.simulation.SimulationStatus;
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
import java.util.Date;

/**
 * Created by Dan on 7/13/2015.
 */
public class SupplyAndDemandPanel extends JPanel implements SubscribedView
{
    private ChartPanel supplyChartPanel;
    private ChartPanel demandChartPanel;
    private XYSeriesCollection supplyData;
    private DynamicTimeSeriesCollection demandData;
    private final int MIN = 0;
    private final int MAX = 5000;
    private final int TWO_MINUTES = 120;
    private int displayInterval = TWO_MINUTES;
    private double previousTime;

    public SupplyAndDemandPanel()
    {
        setLayout(new GridLayout(1,2));

        supplyChartPanel = new ChartPanel(createSupplyChart());
        demandChartPanel = new ChartPanel(createDemandChart());

        add(supplyChartPanel);
        add(demandChartPanel);
        previousTime = 0;
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(World.PC_WORLD_UPDATE))
        {
            float[] newData = new float[1];
            SimulationStatus status = (SimulationStatus)event.getNewValue();
            newData[0] = (float)status.electricityDemand;

            int difference = (int)(Math.floor(status.worldTimer.getTotalTimeInSeconds() - previousTime));

            if (difference > 0)
            {
                for (int i = 0; i < difference; ++i)
                {
                    demandData.advanceTime();
                }

                demandData.appendData(newData);

                previousTime = status.worldTimer.getTotalTimeInSeconds();
            }

        }
        else if (event.getPropertyName().equals(World.PC_UPDATE_RATE_CHANGE))
        {
            WorldTimer.UpdateRate rate = (WorldTimer.UpdateRate)event.getNewValue();
            switch(rate)
            {
                case SECONDS:
                {
                    displayInterval = TWO_MINUTES;
                } break;
                case MINUTES:
                {
                    displayInterval = TWO_MINUTES;
                } break;
                case HOURS:
                {
                    displayInterval = (int)WorldTimer.SECONDS_IN_HOUR;
                } break;
                case DAYS:
                {
                    displayInterval = (int) WorldTimer.SECONDS_IN_DAY;
                } break;
                // TODO: handle others
            }
        }
        else if (event.getPropertyName().equals(World.PC_SIMULATION_STARTED))
        {
            previousTime = 0;
            remove(demandChartPanel);
            demandChartPanel = new ChartPanel(createDemandChart());
            add(demandChartPanel);
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

    private JFreeChart createDemandChart()
    {
        demandData = new DynamicTimeSeriesCollection(1, displayInterval, new Second());
        demandData.setTimeBase(new Second(0, 0, 0, 1, 1, 2011));
        float[] series = {0};
        demandData.addSeries(series, 0, "Usage");

        JFreeChart result = ChartFactory.createTimeSeriesChart(
                "Demand",  // chart title
                "hh:mm:ss",
                "Demand(W)",
                demandData,
                true,
                true,
                false
        );

        final XYPlot plot = result.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange(true);
        ValueAxis range = plot.getRangeAxis();
        range.setRange(MIN, MAX);

        return result;
    }
}