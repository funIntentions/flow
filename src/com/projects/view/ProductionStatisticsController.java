package com.projects.view;

import com.projects.Main;
import com.projects.helper.Utils;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.List;

/**
 * Controller for the production statistics view.
 */
public class ProductionStatisticsController {
    @FXML
    private LineChart<String, Float> priceForDemandChart;

    @FXML
    private LineChart<String, Float> emissionsForDemandChart;

    private XYChart.Series<String, Float> priceForDemandSeries;

    private XYChart.Series<String, Float> emissionsForDemandSeries;

    private Main main;
    private int length = 100;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        priceForDemandSeries = new XYChart.Series<>();
        priceForDemandSeries.setName("Price");
        priceForDemandChart.getData().add(priceForDemandSeries);

        emissionsForDemandSeries = new XYChart.Series<>();
        emissionsForDemandSeries.setName("Emissions");
        emissionsForDemandChart.getData().add(emissionsForDemandSeries);
    }

    /**
     * Displays the price versus demand data in a chart.
     * @param prices prices versus demand data
     */
    public void setPriceForDemandData(List<Float> prices) {
        priceForDemandSeries.getData().clear();
        int increments = prices.size() / length;

        for (int index = 0; index < length; ++index) {
            int demand = index * increments;
            if (demand > prices.size())
                demand = prices.size() - 1;

            priceForDemandSeries.getData().add(new XYChart.Data<>(String.valueOf(Utils.wattsToKilowatts(demand)), prices.get(demand)));
        }
    }

    /**
     * Displays the emissions versus demand data in a chart.
     * @param emissions emissions versus demand data
     */
    public void setEmissionsForDemandData(List<Float> emissions) {
        emissionsForDemandSeries.getData().clear();
        int increments = emissions.size() / length;

        for (int index = 0; index < length; ++index) {
            int demand = index * increments;
            if (demand > emissions.size())
                demand = emissions.size() - 1;

            emissionsForDemandSeries.getData().add(new XYChart.Data<>(String.valueOf(Utils.wattsToKilowatts(demand)), emissions.get(demand)));
        }
    }

    /**
     * Clears the price versus demand chart.
     */
    public void clearPriceForDemandChart() {
        priceForDemandSeries.getData().clear();
    }

    /**
     * Clears the emission rate versus demand chart.
     */
    public void clearEmissionForDemandChart() {
        emissionsForDemandSeries.getData().clear();
    }

    /**
     * Provides a reference to main and allows the controller to listen for events it cares about.
     * @param main a reference to main.
     */
    public void setMain(Main main) {
        this.main = main;
    }
}
