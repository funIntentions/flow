package com.projects.gui.panel;

import com.projects.gui.SubscribedView;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Created by Dan on 5/27/2015.
 */
public class SelectionPropertyPanel extends JPanel implements SubscribedView
{
    private JFreeChart objChart;

    public SelectionPropertyPanel(TableModelListener propertiesTableListener)
    {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Load Profile"));

        DefaultPieDataset objDataset = new DefaultPieDataset();
        objDataset.setValue("Apple",29);
        objDataset.setValue("HTC",15);
        objDataset.setValue("Samsung",24);
        objDataset.setValue("LG",7);
        objDataset.setValue("Motorola",10);

        objChart = ChartFactory.createPieChart(
                "Demo Pie Chart",   //Chart title
                objDataset,          //Chart Data
                true,               // include legend?
                true,               // include tooltips?
                false               // include URLs?
        );

        ChartPanel chartPanel = new ChartPanel(objChart);
        add(chartPanel, BorderLayout.CENTER);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {

    }
}
