package com.projects.view;

import com.projects.Main;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.List;

/**
 * Created by Dan on 7/31/2015.
 */
public class ProductionStatisticsController
{
    @FXML
    private LineChart<String, Float> priceForDemandChart;

    @FXML
    private LineChart<String, Float> emissionsForDemandChart;

    private XYChart.Series<String, Float> priceForDemandSeries;

    private XYChart.Series<String, Float> emissionsForDemandSeries;

    private Main main;

    @FXML
    private void initialize()
    {
        priceForDemandSeries= new XYChart.Series<>();
        priceForDemandSeries.setName("Price");
        priceForDemandChart.getData().add(priceForDemandSeries);

        emissionsForDemandSeries = new XYChart.Series<>();
        emissionsForDemandSeries.setName("Emissions");
        emissionsForDemandChart.getData().add(emissionsForDemandSeries);
    }

    public void setPriceForDemandData(List<Float> prices)
    {
        priceForDemandSeries.getData().clear();

        for (int demand = 0; demand < prices.size(); demand+=30)
        {
            priceForDemandSeries.getData().add(new XYChart.Data<>(String.valueOf(demand), prices.get(demand)));
        }
    }

    public void setEmissionsForDemandData(List<Float> emissions)
    {
        emissionsForDemandSeries.getData().clear();

        for (int demand = 0; demand < emissions.size(); demand+=30)
        {
            emissionsForDemandSeries.getData().add(new XYChart.Data<>(String.valueOf(demand), emissions.get(demand)));
        }
    }

    public void setMain(Main main)
    {
        this.main = main;
    }
}
