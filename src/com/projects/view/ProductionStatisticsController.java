package com.projects.view;

import com.projects.Main;
import com.projects.helper.Utils;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.List;

/**
 * Created by Dan on 7/31/2015.
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

    @FXML
    private void initialize() {
        priceForDemandSeries = new XYChart.Series<>();
        priceForDemandSeries.setName("Price");
        priceForDemandChart.getData().add(priceForDemandSeries);

        emissionsForDemandSeries = new XYChart.Series<>();
        emissionsForDemandSeries.setName("Emissions");
        emissionsForDemandChart.getData().add(emissionsForDemandSeries);
    }

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

    public void clearPriceForDemandChart() {
        priceForDemandSeries.getData().clear();
    }

    public void clearEmissionForDemandChart() {
        emissionsForDemandSeries.getData().clear();
    }

    public void setMain(Main main) {
        this.main = main;
    }
}
