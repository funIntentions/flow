package com.projects.view;

import com.projects.model.Building;
import com.projects.model.Structure;
import javafx.beans.Observable;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

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

    private ObservableList<StructureResults> observableList;

    private SortedList<StructureResults> sortedList;

    private XYChart.Series<String, Float> series = new XYChart.Series<>();
    private List<ObservableList<Float>> loadProfilesForWeek = new ArrayList<>();

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

        Callback<StructureResults, Observable[]> callback = (StructureResults results) -> new Observable[]
                {
                        results.expensesProperty,
                };


        observableList = FXCollections.observableArrayList(callback);
        sortedList = new SortedList<StructureResults>(observableList,
                (StructureResults results1, StructureResults results2) ->
                {
                    if (results1.expensesProperty.get() < results2.expensesProperty.get())
                    {
                        return -1;
                    }
                    else if (results1.expensesProperty.get() > results2.expensesProperty.get())
                    {
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                });

        sortedList.comparatorProperty().bind(structureRankingTable.comparatorProperty());
        structureRankingTable.setItems(sortedList);

        series.setName("No Selection");
        loadProfileChart.getData().add(series);

        daysOfTheWeekTabPane.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> switchChartData());
    }

    public void displayResults(List<Building> structureList, HashMap<Integer, Float> expenses, HashMap<Integer, Float> environmentalImpact)
    {
        observableList.clear();
        for (Structure structure : structureList)
        {
            StructureResults structureResults = new StructureResults();
            structureResults.structureProperty.set(structure);
            structureResults.expensesProperty.set(expenses.get(structure.getId()));
            structureResults.environmentalImpactProperty.set(environmentalImpact.get(structure.getId()));

            observableList.add(structureResults);
        }
    }

    public void clearComparisions()
    {
        observableList.clear();
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
