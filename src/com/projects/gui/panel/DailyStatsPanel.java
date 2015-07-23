package com.projects.gui.panel;

import com.projects.gui.SubscribedView;
import com.projects.systems.simulation.StatsManager;
import com.projects.systems.simulation.World;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
    private DatePicker datePickerDisplayDate;
    private StatsManager statsManager = null;
    private JPanel chartsPanel;
    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate = startDate.plusDays(1);

    public DailyStatsPanel()
    {
        setLayout(new BorderLayout());
        chartsPanel = new JPanel(new GridLayout(1,3));

        dailyPriceChartPanel = new ChartPanel(createPriceChart());
        dailyEmissionsChartPanel = new ChartPanel(createEmissionsChart());
        dailyDemandChartPanel = new ChartPanel(createDemandChart());

        chartsPanel.add(dailyPriceChartPanel);
        chartsPanel.add(dailyEmissionsChartPanel);
        chartsPanel.add(dailyDemandChartPanel);

        final JFXPanel fxDisplayDatePanel = new JFXPanel();
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                VBox vBoxEndDate = new VBox(20);
                Scene sceneEndDate = new Scene(vBoxEndDate);
                fxDisplayDatePanel.setScene(sceneEndDate);

                javafx.scene.control.Label labelEndDate = new Label("Date: ");
                GridPane gridPaneEndDate = new GridPane();
                datePickerDisplayDate = new DatePicker();
                datePickerDisplayDate.setValue(LocalDate.now());
                datePickerDisplayDate.setOnAction(event -> {
                    LocalDate date = datePickerDisplayDate.getValue();
                    long day = ChronoUnit.DAYS.between(startDate, date);
                    if (statsManager != null)
                    {
                        updateDataCollection(dailyPriceData, "Prices", statsManager.getDailyPriceTrends((int)day));
                        updateDataCollection(dailyEmissionsData, "Emissions", statsManager.getDailyEmissionTrends((int) day));
                        updateDataCollection(dailyDemandData, "Demand", statsManager.getDailyDemandTrends((int)day));
                    }
                });
                gridPaneEndDate.add(labelEndDate, 0, 0);
                gridPaneEndDate.add(datePickerDisplayDate, 0, 1);
                vBoxEndDate.getChildren().add(gridPaneEndDate);

                limitDatePickerRange();
            }
        });
        fxDisplayDatePanel.setPreferredSize(new Dimension(112, 50));

        JPanel displayDatePanel = new JPanel(new FlowLayout());
        displayDatePanel.add(fxDisplayDatePanel);
        add(displayDatePanel, BorderLayout.PAGE_START);
        add(chartsPanel, BorderLayout.CENTER);
    }

    private void limitDatePickerRange()
    {
        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item.isBefore(startDate))
                                {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                                else if (item.isAfter(endDate.minusDays(1)))
                                {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };

        datePickerDisplayDate.setDayCellFactory(dayCellFactory);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(World.PC_DAILY_STATS_UPDATED))
        {
            statsManager = (StatsManager)event.getNewValue();
            updateDataCollection(dailyPriceData, "Prices", statsManager.getDailyPriceTrends());
            updateDataCollection(dailyEmissionsData, "Emissions", statsManager.getDailyEmissionTrends());
            updateDataCollection(dailyDemandData, "Demand", statsManager.getDailyDemandTrends());
        }
        else if (event.getPropertyName().equals(World.PC_SIMULATION_STARTED))
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                  datePickerDisplayDate.setDisable(true);
                }
            });
        }
        else if (event.getPropertyName().equals(World.PC_WORLD_RESET))
        {
            dailyPriceData.removeAllSeries();
            dailyEmissionsData.removeAllSeries();
            dailyDemandData.removeAllSeries();

        }
        else if (event.getPropertyName().equals(World.PC_START_DATE_CHANGED))
        {
            startDate = (LocalDate)event.getNewValue();
        }
        else if (event.getPropertyName().equals(World.PC_END_DATE_CHANGED))
        {
            endDate = (LocalDate)event.getNewValue();
        }


        if (event.getPropertyName().equals(World.PC_SIMULATION_FINISHED) || event.getPropertyName().equals(World.PC_WORLD_RESET))
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    datePickerDisplayDate.setDisable(false);
                }
            });
        }
    }

    private void updateDataCollection(XYSeriesCollection data, String name,  List<Float> values)
    {
        data.removeAllSeries();
        XYSeries priceSeries = new XYSeries(name);

        int intervalCount = values.size();
        for (int interval = 0; interval < intervalCount; ++interval)
        {
            priceSeries.add(interval, values.get(interval));
        }

        data.addSeries(priceSeries);
    }

    private void updateDailyPriceDataSeries(List<Float> prices)
    {
        dailyPriceData.removeAllSeries();
        XYSeries priceSeries = new XYSeries("Price");

        int intervalCount = prices.size();
        for (int interval = 0; interval < intervalCount; ++interval)
        {
            priceSeries.add(interval, prices.get(interval));
        }

        dailyPriceData.addSeries(priceSeries);
    }

    private void updateDailyEmissionsDataSeries(List<Float> emissions)
    {
        dailyEmissionsData.removeAllSeries();
        XYSeries emissionsSeries = new XYSeries("Emissions");

        int maxDemand = emissions.size();
        for (int demand = 1; demand < maxDemand; ++demand)
        {
            emissionsSeries.add(demand, emissions.get(demand));
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
