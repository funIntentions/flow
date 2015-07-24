package com.projects.gui.panel;

import com.projects.gui.SubscribedView;
import com.projects.models.PowerPlant;
import com.projects.models.WorldTimer;
import com.projects.systems.simulation.SimulationStatus;
import com.projects.systems.simulation.World;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private DynamicTimeSeriesCollection supplyData;
    private DynamicTimeSeriesCollection demandData;
    private HashMap<Integer, Integer> powerPlantIdToSeriesNumberMap;
    private final int TWO_MINUTES = 120;
    private int displayInterval = TWO_MINUTES;
    private int timeInterval = 1; // 1 second
    private RegularTimePeriod timeBase;
    private LocalDate startDate = LocalDate.now();
    private double previousTime;

    public SupplyAndDemandPanel()
    {
        setLayout(new GridLayout(1,2));
        timeBase = new Second(0, 58, 23, 1, 1, 2015);
        powerPlantIdToSeriesNumberMap = new HashMap<Integer, Integer>();

        supplyChartPanel = new ChartPanel(createSupplyChart(new ArrayList<PowerPlant>()));
        demandChartPanel = new ChartPanel(createDemandChart());

        add(supplyChartPanel);
        add(demandChartPanel);
        previousTime = 0;
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(World.PC_WORLD_UPDATE))
        {
            SimulationStatus status = (SimulationStatus)event.getNewValue();

            updateCharts(status);
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
                    timeBase = new Hour(0, 1, 1, 2015); // TODO: use the start date and update timeInterval when start date changes
                } break;
            }
        }
        else if (event.getPropertyName().equals(World.PC_WORLD_RESET))
        {
            previousTime = 0;
            SimulationStatus status = (SimulationStatus)event.getNewValue();

            remove(supplyChartPanel);
            supplyChartPanel = new ChartPanel(createSupplyChart(status.powerPlants));
            add(supplyChartPanel);

            remove(demandChartPanel);
            demandChartPanel = new ChartPanel(createDemandChart());
            add(demandChartPanel);
        }
        else if (event.getPropertyName().equals(World.PC_SIMULATION_STARTED)) // TODO: find a way to keep the power pant map up to date without having to create two charts (reset and start) potencially right after the other.
        {
            SimulationStatus status = (SimulationStatus)event.getNewValue();

            remove(supplyChartPanel);
            supplyChartPanel = new ChartPanel(createSupplyChart(status.powerPlants));
            add(supplyChartPanel);

            remove(demandChartPanel);
            demandChartPanel = new ChartPanel(createDemandChart());
            add(demandChartPanel);
        }
        else if (event.getPropertyName().equals(World.PC_START_DATE_CHANGED))
        {
            startDate = (LocalDate)event.getNewValue();
        }
    }

    private void updateCharts(SimulationStatus status)
    {
        int difference = (int)(Math.floor(status.worldTimer.getTotalTimeInSeconds() - previousTime));

        if (difference/timeInterval > 0)
        {
            difference /= timeInterval;

            for (int i = 0; i < difference; ++i)
            {
                updateDemand(status);
                updateSupply(status);

                previousTime += timeInterval;
            }
        }
    }

    private void updateSupply(SimulationStatus status)
    {
        float[] newData = new float[status.powerPlants.size()];

        supplyData.advanceTime();

        for (PowerPlant powerPlant : status.powerPlants)
        {
            newData[powerPlantIdToSeriesNumberMap.get(powerPlant.getId())] = (float)powerPlant.getCurrentOutput();
        }

        supplyData.appendData(newData);
    }

    private void updateDemand(SimulationStatus status)
    {
        float[] newData = new float[1];
        newData[0] = (float)status.electricityDemand;

        demandData.advanceTime();
        demandData.appendData(newData);
    }

    private JFreeChart createSupplyChart(List<PowerPlant> powerPlants)
    {
        supplyData = new DynamicTimeSeriesCollection(powerPlants.size(), displayInterval, timeBase);
        supplyData.setTimeBase(timeBase); // Offset so that it will begin at 00:00:00

        int seriesNumber = 0;
        powerPlantIdToSeriesNumberMap.clear();

        for (PowerPlant powerPlant : powerPlants)
        {
            float[] series = {};
            supplyData.addSeries(series, seriesNumber, powerPlant.getName());
            powerPlantIdToSeriesNumberMap.put(powerPlant.getId(), seriesNumber);
            ++seriesNumber;
        }

        JFreeChart result = ChartFactory.createTimeSeriesChart(
            "Supply",  // chart title
            "Time",
            "Supply(kWh)",
            supplyData,
            true,
            true,
            false
        );

        final XYPlot plot = result.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange(true); // TODO: try and find a way to change the lower and upper bound...
        ValueAxis range = plot.getRangeAxis();
        range.setLowerBound(0);
        range.setUpperBound(1000);

        return result;
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
        range.setLowerBound(0);
        range.setUpperBound(2000);

        return result;
    }
}