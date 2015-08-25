package com.projects.view;

import com.projects.Main;
import com.projects.model.Building;
import com.projects.model.Structure;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Dan on 7/30/2015.
 */
public class StructureDetailsPaneController
{

    private class StructureResults
    {
        public ObjectProperty<Structure> structureProperty = new SimpleObjectProperty<>();
        public FloatProperty expensesProperty = new SimpleFloatProperty();
        public FloatProperty environmentalImpactProperty = new SimpleFloatProperty();
    }

    @FXML
    private TableView<StructureResults> structureRankingTable;

    @FXML
    private TableColumn<StructureResults, String> structureNameColumn;

    @FXML
    private TableColumn<StructureResults, Float> structureExpensesColumn;

    @FXML
    private TableColumn<StructureResults, Float> structureEnvironmentalImpactColumn;

    @FXML
    private LineChart<String, Float> loadProfileChart;

    @FXML
    private TabPane daysOfTheWeekTabPane;

    private ObservableList<StructureResults> structureResultsList;

    private XYChart.Series<String, Float> series = new XYChart.Series<>();
    private List<ObservableList<Float>> loadProfilesForWeek = new ArrayList<>();
    private Main main;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize()
    {

        structureNameColumn.setCellValueFactory(cellData -> cellData.getValue().structureProperty.get().nameProperty());
        structureExpensesColumn.setCellValueFactory(cellData -> cellData.getValue().expensesProperty.asObject());
        structureEnvironmentalImpactColumn.setCellValueFactory(cellData -> cellData.getValue().environmentalImpactProperty.asObject());

        structureResultsList = FXCollections.observableArrayList();
        structureRankingTable.setItems(structureResultsList);


        structureRankingTable.setOnMouseClicked((event) -> main.selectedStructureProperty().set(structureRankingTable.getSelectionModel().getSelectedItem().structureProperty.get()));

        series.setName("No Selection");
        loadProfileChart.getData().add(series);

        daysOfTheWeekTabPane.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> switchChartData());


    }

    public void displayResults(List<Building> structureList, HashMap<Integer, Float> expenses, HashMap<Integer, Float> environmentalImpact)
    {
        structureResultsList.clear();
        for (Structure structure : structureList)
        {
            StructureResults structureResults = new StructureResults();
            structureResults.structureProperty.set(structure);
            structureResults.expensesProperty.set(expenses.get(structure.getId()));
            structureResults.environmentalImpactProperty.set(environmentalImpact.get(structure.getId()));

            structureResultsList.add(structureResults);
        }
    }

    public void clearComparisions()
    {
        structureResultsList.clear();
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

    public void setMain(Main main)
    {
        this.main = main;

        main.selectedStructureProperty().addListener((observable, oldValue, newValue) ->
        {
            structureRankingTable.getSelectionModel().clearSelection();

            for (StructureResults results : structureResultsList)
            {
                if (results.structureProperty.get().getId() == newValue.getId())
                {
                    structureRankingTable.getSelectionModel().select(results);
                    break;
                }
            }
        });
    }
}
