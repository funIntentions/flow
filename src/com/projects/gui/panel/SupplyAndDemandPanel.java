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
    private int timeInterval = 1; // 1 second
    private RegularTimePeriod timeBase;
    private double previousTime;

    public SupplyAndDemandPanel()
    {
        setLayout(new GridLayout(1,2));
        timeBase = new Second(0, 58, 23, 1, 1, 2015);

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

            if (difference/timeInterval > 0)
            {
                difference /= timeInterval;

                for (int i = 0; i < difference; ++i)
                {
                    demandData.advanceTime();
                    previousTime += timeInterval;
                    demandData.appendData(newData);
                }
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
                    timeInterval = 1;
                    timeBase = new Second(0, 58, 23, 1, 1, 2015);
                } break;
                case MINUTES:
                {
                    displayInterval = 60;
                    timeInterval = (int)WorldTimer.SECONDS_IN_MINUTE;
                    timeBase = new Minute(59, 23, 1, 1, 2015);
                } break;
                case HOURS:
                {
                    displayInterval = WorldTimer.HOURS_IN_DAY / 4;
                    timeInterval = (int)WorldTimer.SECONDS_IN_HOUR;
                    timeBase = new Hour(19, 1, 1, 2015);
                } break;
                case DAYS:
                {
                    displayInterval = (int)(WorldTimer.DAYS_IN_WEEK * WorldTimer.HOURS_IN_DAY);
                    timeInterval = (int)WorldTimer.SECONDS_IN_HOUR;
                    timeBase = new Hour(0, 1, 1, 2015);
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

    private JFreeChart createSupplyChart()
    {
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
        demandData = new DynamicTimeSeriesCollection(1, displayInterval, timeBase);
        demandData.setTimeBase(timeBase); // Offset so that it will begin at 00:00:00

        float[] series = {};
        demandData.addSeries(series, 0, "Usage");

        JFreeChart result = ChartFactory.createTimeSeriesChart(
                "Demand",  // chart title
                "Time",
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