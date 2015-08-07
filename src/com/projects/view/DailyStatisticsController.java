package com.projects.view;

import com.projects.Main;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by Dan on 7/31/2015.
 */
public class DailyStatisticsController
{
    @FXML
    private LineChart<String, Float> demandStatsChart;

    @FXML
    private LineChart<String, Float> priceStatsChart;

    @FXML
    private LineChart<String, Float> emissionStatsChart;

    @FXML
    private DatePicker datePickerForSelectedDate;

    private XYChart.Series<String, Float> demandSeries;

    private XYChart.Series<String, Float> priceSeries;

    private XYChart.Series<String, Float> emissionSeries;

    private Main main;

    @FXML
    private void initialize()
    {
        datePickerForSelectedDate.setValue(LocalDate.now());
        datePickerForSelectedDate.setOnAction(event ->
        {
            LocalDate date = datePickerForSelectedDate.getValue();
            long day = ChronoUnit.DAYS.between(main.getStartDate(), date);
            main.dailyStatsDayProperty().setValue(day);
        });

        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<DatePicker, DateCell>()
                {
                    @Override
                    public DateCell call(final DatePicker datePicker)
                    {
                        return new DateCell()
                        {
                            @Override
                            public void updateItem(LocalDate item, boolean empty)
                            {
                                super.updateItem(item, empty);

                                if (item.isBefore(main.getStartDate()))
                                {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                                else if (item.isAfter(main.getEndDate().minusDays(1)))
                                {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };

        datePickerForSelectedDate.setDayCellFactory(dayCellFactory);

        demandSeries = new XYChart.Series<>();
        demandSeries.setName("Demand");
        demandStatsChart.getData().add(demandSeries);

        priceSeries = new XYChart.Series<>();
        priceSeries.setName("Price");
        priceStatsChart.getData().add(priceSeries);

        emissionSeries = new XYChart.Series<>();
        emissionSeries.setName("Emissions");
        emissionStatsChart.getData().add(emissionSeries);

    }

    public void setDemandChartData(List<Float> demand)
    {
        clearDemandChart();

        for (int i = 0; i < demand.size(); i+=30)
        {
            demandSeries.getData().add(new XYChart.Data<>(String.valueOf(i), demand.get(i)));
        }
    }

    public void setPriceChartData(List<Float> price)
    {
        clearPriceChart();

        for (int i = 0; i < price.size(); i+=30)
        {
            priceSeries.getData().add(new XYChart.Data<>(String.valueOf(i), price.get(i)));
        }
    }

    public void setEmissionsChartData(List<Float> emissions)
    {
        clearEmissionChart();

        for (int i = 0; i < emissions.size(); i+=30)
        {
            emissionSeries.getData().add(new XYChart.Data<>(String.valueOf(i), emissions.get(i)));
        }
    }

    private void clearDemandChart()
    {
        demandSeries.getData().clear();
    }

    private void clearPriceChart()
    {
        priceSeries.getData().clear();
    }

    private void clearEmissionChart()
    {
        emissionSeries.getData().clear();
    }

    public void setMain(Main main)
    {
        this.main = main;

        main.simulationStateProperty().addListener((observable, oldValue, newValue) ->
        {
            switch (newValue)
            {
                case RESET:
                {
                    clearDemandChart();
                    clearPriceChart();
                    clearEmissionChart();
                }
                case RUNNING:
                {
                    datePickerForSelectedDate.setDisable(true);
                } break;
                case FINISHED:
                {
                    datePickerForSelectedDate.setDisable(false);
                }
            }
        });
    }
}