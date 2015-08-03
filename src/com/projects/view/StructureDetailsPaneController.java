package com.projects.view;

import com.projects.model.Structure;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.List;


/**
 * Created by Dan on 7/30/2015.
 */
public class StructureDetailsPaneController
{
    @FXML
    private LineChart<String, Float> loadProfileChart;

    private XYChart.Series<String, Float> series;
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize()
    {
        series = new XYChart.Series<>();
        series.setName("Empty");
        loadProfileChart.getData().add(series);
    }

    /**
     * Sets the persons to show the statistics for.
     *
     * @param structure
     * @param loadProfile
     */
    public void setStructureData(Structure structure, List<Float> loadProfile)
    {
        series.getData().clear();

        series.setName(structure.getName());

        for (int i = 0; i < loadProfile.size(); i+=30)
        {
            series.getData().add(new XYChart.Data<>(String.valueOf(i), loadProfile.get(i)));
        }
    }
}
