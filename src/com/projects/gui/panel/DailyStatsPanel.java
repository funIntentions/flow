package com.projects.gui.panel;

import com.projects.gui.SubscribedView;
import com.projects.systems.simulation.StatsManager;
import com.projects.systems.simulation.World;
import com.sun.org.glassfish.external.statistics.Stats;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Created by Dan on 7/16/2015.
 */
public class DailyStatsPanel extends JPanel implements SubscribedView
{
    private ChartPanel dailyPriceChartPanel;
    private XYSeriesCollection dailyPriceData;
    private ChartPanel dailyEmissionsChartPanel;
    private XYSeriesCollection dailyEmissionsData;
    private ChartPanel dailyDemandChartPanel;
    private XYSeriesCollection dailyDemandData;

    public DailyStatsPanel()
    {
        setLayout(new GridLayout(1,3));

        dailyPriceChartPanel = new ChartPanel(createPriceChart());
        dailyEmissionsChartPanel = new ChartPanel(createEmissionsChart());
        dailyDemandChartPanel = new ChartPanel(createDemandChart());

        add(dailyPriceChartPanel);
        add(dailyEmissionsChartPanel);
        add(dailyDemandChartPanel);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(World.PC_DAILY_STATS_UPDATED))
        {
            StatsManager statsManager = (StatsManager)event.getNewValue();
            updateDataCollection(dailyPriceData, "Prices", statsManager.getDailyPriceTrends());
            updateDataCollection(dailyEmissionsData, "Emissions", statsManager.getDailyEmissionTrends());
            updateDataCollection(dailyDemandData, "Demand", statsManager.getDailyDemandTrends());
        }
        else if (event.getPropertyName().equals(World.PC_WORLD_RESET))
        {
            dailyPriceData.removeAllSeries();
            dailyEmissionsData.removeAllSeries();
            dailyDemandData.removeAllSeries();
        }
    }

    private void updateDataCollection(XYSeriesCollection data, String name,  float[] values)
    {
        data.removeAllSeries();
        XYSeries priceSeries = new XYSeries(name);

        int intervalCount = values.length;
        for (int interval = 0; interval < intervalCount; ++interval)
        {
            priceSeries.add(interval, values[interval]);
        }

        data.addSeries(priceSeries);
    }

    private void updateDailyPriceDataSeries(float[] prices)
    {
        dailyPriceData.removeAllSeries();
        XYSeries priceSeries = new XYSeries("Price");

        int intervalCount = prices.length;
        for (int interval = 0; interval < intervalCount; ++interval)
        {
            priceSeries.add(interval, prices[interval]);
        }

        dailyPriceData.addSeries(priceSeries);
    }

    private void updateDailyEmissionsDataSeries(float[] emissions)
    {
        dailyEmissionsData.removeAllSeries();
        XYSeries emissionsSeries = new XYSeries("Emissions");

        int maxDemand = emissions.length;
        for (int demand = 1; demand < maxDemand; ++demand)
        {
            emissionsSeries.add(demand, emissions[demand]);
        }

        dailyEmissionsData.addSeries(emissionsSeries);
    }

    private JFreeChart createDemandChart()
    {
        XYSeries series1 = new XYSeries("Structures");
        dailyDemandData = new XYSeriesCollection(series1);
        return ChartFactory.createXYLineChart(
                "Demand",  // chart title
                "Time of Day",
                "Usage (Watts)",
                dailyDemandData
        );
    }

    private JFreeChart createPriceChart()
    {
        XYSeries series1 = new XYSeries("Price");
        dailyPriceData = new XYSeriesCollection(series1);
        return ChartFactory.createXYLineChart(
                "Price",  // chart title
                "Time of Day",
                "Price $/kWh",
                dailyPriceData
        );
    }

    private JFreeChart createEmissionsChart()
    {

        XYSeries series1 = new XYSeries("Emissions");
        dailyEmissionsData = new XYSeriesCollection(series1);
        return ChartFactory.createXYLineChart(
                "Emissions",  // chart title
                "Time of Day",
                "Emissions (g/kWh)",
                dailyEmissionsData
        );
    }
}
