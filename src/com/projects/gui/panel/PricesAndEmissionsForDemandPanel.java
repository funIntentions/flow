package com.projects.gui.panel;

import com.projects.gui.SubscribedView;
import com.projects.systems.simulation.SupplyManager;
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

/**
 * Created by Dan on 7/14/2015.
 */
public class PricesAndEmissionsForDemandPanel extends JPanel implements SubscribedView
{
    private ChartPanel priceChartPanel;
    private XYSeriesCollection priceData;
    private ChartPanel emissionsChartPanel;
    private XYSeriesCollection emissionsData;

    public PricesAndEmissionsForDemandPanel()
    {
        setLayout(new GridLayout(1,2));

        priceChartPanel = new ChartPanel(createPriceChart());
        emissionsChartPanel = new ChartPanel(createEmissionsChart());

        add(priceChartPanel);
        add(emissionsChartPanel);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(World.PC_PRICE_STATS_UPDATED))
        {
            updatePriceDataSeries((List<Float>)event.getNewValue());
        }
        else if (event.getPropertyName().equals(World.PC_EMISSIONS_STATS_UPDATED))
        {
            updateEmissionsDataSeries((List<Float>) event.getNewValue());
        }
    }

    private void updatePriceDataSeries(List<Float> prices)
    {
        priceData.removeAllSeries();
        XYSeries priceSeries = new XYSeries("Price");

        int maxDemand = prices.size();
        for (int demand = 0; demand < maxDemand; ++demand)
        {
            priceSeries.add(demand, prices.get(demand));
        }

        priceData.addSeries(priceSeries);
    }

    private void updateEmissionsDataSeries(List<Float> emissions)
    {
        emissionsData.removeAllSeries();
        XYSeries emissionsSeries = new XYSeries("Emissions");

        int maxDemand = emissions.size();
        for (int demand = 1; demand < maxDemand; ++demand)
        {
            emissionsSeries.add(demand, emissions.get(demand));
        }

        emissionsData.addSeries(emissionsSeries);
    }

    private JFreeChart createPriceChart()
    {
        XYSeries series1 = new XYSeries("Price");
        priceData = new XYSeriesCollection(series1);
        return ChartFactory.createXYLineChart(
                "Price",  // chart title
                "Demand(Watts)",
                "Price $/kWh",
                priceData
        );
    }

    private JFreeChart createEmissionsChart()
    {

        XYSeries series1 = new XYSeries("Emissions");
        emissionsData = new XYSeriesCollection(series1);
        return ChartFactory.createXYLineChart(
                "Emissions",  // chart title
                "Demand (Watts)",
                "Emissions (g/kWh)",
                emissionsData
        );
    }
}
