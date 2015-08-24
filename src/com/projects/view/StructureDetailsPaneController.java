package com.projects.view;

import com.projects.model.Structure;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TabPane;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dan on 7/30/2015.
 */
public class StructureDetailsPaneController
{
    @FXML
    private LineChart<String, Float> loadProfileChart;

    @FXML
    private TabPane daysOfTheWeekTabPane;

    private XYChart.Series<String, Float> series = new XYChart.Series<>();
    private List<ObservableList<Float>> loadProfilesForWeek = new ArrayList<>();

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize()
    {
        series.setName("No Selection");
        loadProfileChart.getData().add(series);

        daysOfTheWeekTabPane.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> switchChartData());
    }

    private void switchChartData()
    {
        int day = daysOfTheWeekTabPane.getSelectionModel().getSelectedIndex();

        List<Float> loadProfile = loadProfilesForWeek != null && loadProfilesForWeek.size() > 0 ? loadProfilesForWeek.get(day): new ArrayList<>();

        series.getData().clear();

        for (int i = 0; i < loadProfile.size(); i+=30)
        {
            series.getData().add(new XYChart.Data<>(String.valueOf(i), loadProfile.get(i)));
        }
    }

    /**
     * Sets the persons to show the statistics for.
     *
     * @param structure
     * @param loadProfilesForWeek
     */
    public void setStructureData(Structure structure, List<ObservableList<Float>> loadProfilesForWeek)
    {
        this.loadProfilesForWeek = loadProfilesForWeek;

        if (structure != null)
            series.setName(structure.getName());
        else
            series.setName("No Selection");

        switchChartData();
    }

    public void clearLoadProfileDetails()
    {
        loadProfilesForWeek.clear();
        switchChartData();
    }
}
