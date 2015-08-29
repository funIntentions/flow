package com.projects.view;

import com.projects.Main;
import com.projects.helper.Utils;
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
 * Controller for the daily statistics view.
 */
public class DailyStatisticsController {
    @FXML
    private LineChart<String, Double> demandStatsChart;

    @FXML
    private LineChart<String, Double> priceStatsChart;

    @FXML
    private LineChart<String, Double> emissionStatsChart;

    @FXML
    private DatePicker datePickerForSelectedDate;

    private XYChart.Series<String, Double> demandSeries;

    private XYChart.Series<String, Double> priceSeries;

    private XYChart.Series<String, Double> emissionSeries;

    private Main main;

    private float minuteInterval = 30f;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        datePickerForSelectedDate.setValue(LocalDate.now());
        datePickerForSelectedDate.setOnAction(event ->
        {
            LocalDate date = datePickerForSelectedDate.getValue();
            long day = ChronoUnit.DAYS.between(main.getStartDate(), date);
            main.dailyStatsDayProperty().setValue(day);
        });

        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item.isBefore(main.getStartDate())) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                } else if (item.isAfter(main.getEndDate().minusDays(1))) {
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

    /**
     * Sets the data for the demand versus time chart.
     *
     * @param demand demand throughout the day
     */
    public void setDemandChartData(List<Float> demand) {
        clearDemandChart();

        for (int i = 0; i < demand.size(); i += minuteInterval) {
            float hour = (i / minuteInterval) / 2f;
            demandSeries.getData().add(new XYChart.Data<>(String.valueOf(hour), Utils.wattsToKilowatts(demand.get(i))));
        }
    }

    /**
     * Sets the data for the price versus time chart.
     *
     * @param price price throughout the day
     */
    public void setPriceChartData(List<Float> price) {
        clearPriceChart();

        for (int i = 0; i < price.size(); i += minuteInterval) {
            float hour = (i / minuteInterval) / 2f;
            priceSeries.getData().add(new XYChart.Data<>(String.valueOf(hour), (double) price.get(i)));
        }
    }

    /**
     * Sets the data for the emission versus time chart.
     *
     * @param emissions emission rates throughout the day
     */
    public void setEmissionsChartData(List<Float> emissions) {
        clearEmissionChart();

        for (int i = 0; i < emissions.size(); i += minuteInterval) {
            float hour = (i / minuteInterval) / 2f;
            emissionSeries.getData().add(new XYChart.Data<>(String.valueOf(hour), (double) emissions.get(i)));
        }
    }

    public void clearDemandChart() {
        demandSeries.getData().clear();
    }

    public void clearPriceChart() {
        priceSeries.getData().clear();
    }

    public void clearEmissionChart() {
        emissionSeries.getData().clear();
    }

    /**
     * Provides a reference to main and allows the controller to listen for events it cares about.
     *
     * @param main a reference to main.
     */
    public void setMain(Main main) {
        this.main = main;

        main.simulationStateProperty().addListener((observable, oldValue, newValue) ->
        {
            switch (newValue) {
                case RESET: {
                    clearDemandChart();
                    clearPriceChart();
                    clearEmissionChart();
                }
                case RUNNING: {
                    datePickerForSelectedDate.setDisable(true);
                }
                break;
                case FINISHED: {
                    datePickerForSelectedDate.setDisable(false);
                    datePickerForSelectedDate.setValue(main.getStartDate());
                }
            }
        });
    }
}
