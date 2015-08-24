package com.projects.view;

import com.projects.Main;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Dan on 8/1/2015.
 */
public class StructureComparisonsController
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

    private ObservableList<StructureResults> observableList;

    private SortedList<StructureResults> sortedList;

    private Main main;

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

    public void setMain(Main main)
    {
        this.main = main;

        main.simulationStateProperty().addListener((observable, oldValue, newValue) ->
        {
            switch (newValue)
            {
                case RESET:
                {
                    observableList.clear();
                }
            }
        });
    }
}
