package com.projects.view;

import com.projects.Main;
import com.projects.helper.SimulationState;
import com.projects.model.Building;
import com.projects.model.Structure;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
 * Controller for the building details view
 */
public class BuildingDetailsController {

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
    private int minuteInterval = 30;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

        structureNameColumn.setCellValueFactory(cellData -> cellData.getValue().structureProperty.get().nameProperty());
        structureExpensesColumn.setCellValueFactory(cellData -> cellData.getValue().expensesProperty.asObject());
        structureEnvironmentalImpactColumn.setCellValueFactory(cellData -> cellData.getValue().environmentalImpactProperty.asObject());

        structureResultsList = FXCollections.observableArrayList();
        structureRankingTable.setItems(structureResultsList);


        structureRankingTable.setOnMouseClicked((event) -> {
            StructureResults structureResults = structureRankingTable.getSelectionModel().getSelectedItem();
            if (structureResults != null)
                main.selectedStructureProperty().set(structureResults.structureProperty.get());
        });

        series.setName("No Building Selected");
        loadProfileChart.getData().add(series);

        daysOfTheWeekTabPane.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateChartData());


    }

    /**
     * Displays the expenses and environmental impact of buildings from the simulation.
     *
     * @param structureList       buildings from simulation
     * @param expenses            building expenses
     * @param environmentalImpact environmental impacts of buildings
     */
    public void displayResults(List<Building> structureList, HashMap<Integer, Float> expenses, HashMap<Integer, Float> environmentalImpact) {
        structureResultsList.clear();
        for (Structure structure : structureList) {
            StructureResults structureResults = new StructureResults();
            structureResults.structureProperty.set(structure);
            structureResults.expensesProperty.set(expenses.get(structure.getId()));
            structureResults.environmentalImpactProperty.set(environmentalImpact.get(structure.getId()));

            structureResultsList.add(structureResults);
        }
    }

    /**
     * Clears the comparison table.
     */
    public void clearComparisons() {
        structureResultsList.clear();
    }

    /**
     * Displays the load profile for the selected day of the week
     */
    private void updateChartData() {
        int day = daysOfTheWeekTabPane.getSelectionModel().getSelectedIndex();

        List<Float> loadProfile = loadProfilesForWeek != null && loadProfilesForWeek.size() > 0 ? loadProfilesForWeek.get(day) : new ArrayList<>();

        series.getData().clear();

        for (int i = 0; i < loadProfile.size(); i += minuteInterval) {
            float hour = (i / minuteInterval) / 2f;
            series.getData().add(new XYChart.Data<>(String.valueOf(hour), loadProfile.get(i)));
        }
    }

    /**
     * Sets the structure to show the load profiles for.
     *
     * @param structure           structure
     * @param loadProfilesForWeek the structures load profiles (if it's a building, otherwise an empty array)
     */
    public void setStructureData(Structure structure, List<ObservableList<Float>> loadProfilesForWeek) {
        this.loadProfilesForWeek = loadProfilesForWeek;

        if (structure instanceof Building)
            series.setName(structure.getName());
        else
            series.setName("No Building Selected");

        updateChartData();
    }

    /**
     * Clears the chart data.
     */
    public void clearLoadProfileDetails() {
        loadProfilesForWeek.clear();
        updateChartData();
    }

    /**
     * Provides a reference to main and allows the controller to listen for events it cares about.
     *
     * @param main a reference to main.
     */
    public void setMain(Main main) {
        this.main = main;

        main.selectedStructureProperty().addListener((observable, oldValue, newValue) ->
        {
            structureRankingTable.getSelectionModel().clearSelection();

            if (newValue != null) {
                for (StructureResults results : structureResultsList) {
                    if (results.structureProperty.get().getId() == newValue.getId()) {
                        structureRankingTable.getSelectionModel().select(results);
                        break;
                    }
                }
            }
        });


        main.simulationStateProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue == SimulationState.RESET) {
                clearComparisons();
            }
        });

        main.getWorldStructureData().addListener((ListChangeListener<Structure>) c -> {

            while (c.next()) {
                if (c.getRemoved().size() > 0) {
                    List<StructureResults> resultsToRemove = new ArrayList<>();
                    for (Structure removed : c.getRemoved()) {
                        for (StructureResults structureResults : structureRankingTable.getItems()) {
                            if (removed.getId() == structureResults.structureProperty.get().getId())
                                resultsToRemove.add(structureResults);
                        }
                    }
                    structureRankingTable.getItems().removeAll(resultsToRemove);
                }
            }

        });
    }

    private class StructureResults {
        public ObjectProperty<Structure> structureProperty = new SimpleObjectProperty<>();
        public FloatProperty expensesProperty = new SimpleFloatProperty();
        public FloatProperty environmentalImpactProperty = new SimpleFloatProperty();
    }
}
