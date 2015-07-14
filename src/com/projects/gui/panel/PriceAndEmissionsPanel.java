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
            updatePriceAndEmissionsDataSeries(supplyManager);
        }
    }

    private void updatePriceAndEmissionsDataSeries(SupplyManager supplyManager)
    {
        priceData.removeAllSeries();
        emissionsData.removeAllSeries();

        XYSeries priceSeries = new XYSeries("Price");
        XYSeries emissionsSeries = new XYSeries("Emissions");

        double demand = 1;
        double demandIncrease = 10;

        while (supplyManager.calculateSupply(demand))
        {
            double price = supplyManager.getPrice();
            double emissions = supplyManager.getEmissions();

            priceSeries.add(demand, price);
            emissionsSeries.add(demand, emissions);

            demand += demandIncrease;
        }

        priceData.addSeries(priceSeries);
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
        );    }
}
