package com.projects.view;

import com.projects.Main;
import com.projects.helper.Constants;
import com.projects.helper.StructureUtil;
import com.projects.model.Building;
import com.projects.model.PowerPlant;
import com.projects.model.Structure;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Dan on 7/27/2015.
 */
public class StructureOverviewController
{
    @FXML
    private ListView<Structure> templateStructureList;

    @FXML
    private ListView<Structure> worldStructureList;

    @FXML
    private Tab worldViewTab;

    @FXML
    private Tab structureDetailsTab;

    @FXML
    private Tab dailyStatsTab;

    @FXML
    private Tab productionStatsTab;

    @FXML
    private Tab structureComparisonsTab;

    @FXML
    private TitledPane simulationControlsTitledPane;

    @FXML
    private Button worldEditButton;

    @FXML
    private Button worldAddButton;

    @FXML
    private Button worldRemoveButton;

    @FXML
    private MenuButton worldCreateButton;

    @FXML
    private Button templateEditButton;

    @FXML
    private Button templateAddButton;

    @FXML
    private Button templateRemoveButton;

    @FXML
    private MenuButton templateCreateButton;

    private Main main;

    public StructureOverviewController()
    {
    }

    private void selectTemplateStructure()
    {
        Structure structure = templateStructureList.getSelectionModel().getSelectedItem();
        if (structure != null)
        {
            main.selectedTemplateStructureProperty().set(structure);
            main.selectedStructureProperty().set(structure);
        }
    }

    private void selectWorldStructure()
    {
        Structure structure = worldStructureList.getSelectionModel().getSelectedItem();
        if (structure != null)
        {
            main.selectedWorldStructureProperty().set(structure);
            main.selectedStructureProperty().set(structure);
        }
    }

    private void alertNoTemplateStructureSelected()
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(main.getPrimaryStage());
        alert.setTitle("No Selection");
        alert.setHeaderText("No Structure Selected");
        alert.setContentText("Please select a structure from the template structures list.");

        alert.showAndWait();
    }

    private void alertNoWorldStructureSelected()
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(main.getPrimaryStage());
        alert.setTitle("No Selection");
        alert.setHeaderText("No Structure Selected");
        alert.setContentText("Please select a structure from the world structures list.");

        alert.showAndWait();
    }

    @FXML
    private void handleWorldRemoveStructure()
    {
        int selectedIndex = worldStructureList.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0)
        {
            worldStructureList.getItems().remove(selectedIndex);
            main.selectedStructureProperty().set(worldStructureList.getSelectionModel().getSelectedItem());
        }
        else
            alertNoWorldStructureSelected();
    }

    @FXML
    private void handleTemplateRemoveStructure()
    {
        int selectedIndex = templateStructureList.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0)
        {
            templateStructureList.getItems().remove(selectedIndex);
            main.selectedStructureProperty().set(templateStructureList.getSelectionModel().getSelectedItem());
        }
        else
            alertNoTemplateStructureSelected();
    }

    @FXML
    private void handleTemplateAddStructure()
    {
        Structure selectedStructure = templateStructureList.getSelectionModel().getSelectedItem();

        if (selectedStructure != null)
        {
            Structure structure;

            if (selectedStructure instanceof PowerPlant)
            {
                structure = new PowerPlant((PowerPlant)selectedStructure);
            }
            else //if (selectedStructure instanceof Building)
            {
                structure = new Building((Building)selectedStructure);
            } // TODO: add Composite Unit Structures or modify single unit structures so that they do the same

            worldStructureList.getItems().add(structure);
            main.selectedStructureProperty().set(structure);
        }
        else
            alertNoTemplateStructureSelected();
    }

    @FXML
    private void handleWorldAddStructure()
    {
        Structure selectedStructure = worldStructureList.getSelectionModel().getSelectedItem();

        if (selectedStructure != null)
        {
            Structure structure;

            if (selectedStructure instanceof PowerPlant)
            {
                structure = new PowerPlant((PowerPlant)selectedStructure);
            }
            else
            {
                structure = new Building((Building)selectedStructure);
            }

            templateStructureList.getItems().add(structure);
            main.selectedStructureProperty().set(structure);
        }
        else
            alertNoWorldStructureSelected();
    }

    @FXML
    private void handleWorldEditStructure()
    {
        Structure structure = worldStructureList.getSelectionModel().getSelectedItem();

        if (structure != null)
        {
            if (structure instanceof PowerPlant)
            {
                main.showPowerPlantEditDialog((PowerPlant)structure);
            }
            else
            {
                main.showBuildingEditDialog((Building) structure);
            }

            triggerWorldListUpdate(structure); // TODO: find a more elegant way
        }
        else
            alertNoWorldStructureSelected();
    }

    @FXML
    private void handleTemplateEditStructure()
    {
        Structure structure = templateStructureList.getSelectionModel().getSelectedItem();

        if (structure != null)
        {
            if (structure instanceof PowerPlant)
            {
                main.showPowerPlantEditDialog((PowerPlant)structure);
            }
            else
            {
                main.showBuildingEditDialog((Building) structure);
            }

            triggerTemplateListUpdate(structure);
        }
        else
            alertNoTemplateStructureSelected();
    }

    private void triggerWorldListUpdate(Structure structure)
    {
        int index = worldStructureList.getSelectionModel().getSelectedIndex();
        if (index >= 0)
            main.getWorldStructureData().set(index, structure);
    }

    private void triggerTemplateListUpdate(Structure structure)
    {
        int index = templateStructureList.getSelectionModel().getSelectedIndex();
        if (index >= 0)
            main.getTemplateStructureData().set(index, structure);
    }

    private int getRandomStructureXPosition()
    {
        return new Random().nextInt((int)Math.floor(main.getWorldViewController().getWidth() - Constants.IMAGE_SIZE));
    }

    private int getRandomStructureYPosition()
    {
        return new Random().nextInt((int)Math.floor(main.getWorldViewController().getHeight() - Constants.IMAGE_SIZE));
    }

    @FXML
    private void handleWorldCreateBuildingStructure()
    {
        Building structure = new Building("New Building",
                StructureUtil.getNextStructureId(),
                getRandomStructureXPosition(),
                getRandomStructureYPosition(),
                main.getBuildingSprites().get(0),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                false);

        main.getWorldStructureData().add(structure);
        main.selectedStructureProperty().set(structure);
    }

    @FXML
    private void handleWorldCreatePowerPlantStructure()
    {
        PowerPlant powerPlant = new PowerPlant("New Power Plant",
                StructureUtil.getNextStructureId(),
                getRandomStructureXPosition(),
                getRandomStructureYPosition(),
                main.getPowerPlantSprites().get(1),
                0.0, 0.0, 0.0);

        main.getWorldStructureData().add(powerPlant);
        main.selectedStructureProperty().set(powerPlant);
    }

    @FXML
    private void handleTemplateCreateBuildingStructure()
    {
        Building structure = new Building("New Building",
                StructureUtil.getNextStructureId(),
                getRandomStructureXPosition(),
                getRandomStructureYPosition(),
                main.getBuildingSprites().get(0),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                false);

        main.getTemplateStructureData().add(structure);
        main.selectedStructureProperty().set(structure);
    }

    @FXML
    private void handleTemplateCreatePowerPlantStructure()
    {
        PowerPlant powerPlant = new PowerPlant("New Power Plant",
                StructureUtil.getNextStructureId(),
                getRandomStructureXPosition(),
                getRandomStructureYPosition(),
                main.getPowerPlantSprites().get(1),
                0.0, 0.0, 0.0);

        main.getTemplateStructureData().add(powerPlant);
        main.selectedStructureProperty().set(powerPlant);
    }

    @FXML
    private void initialize()
    {
        templateStructureList.setOnMouseClicked((event) -> selectTemplateStructure());
        worldStructureList.setOnMouseClicked((event) -> selectWorldStructure());
    }

    public void showStructureDetailsPane(BorderPane structureDetails)
    {
        structureDetailsTab.setContent(structureDetails);
    }

    public void showSimulationControlsPane(BorderPane simulationControls)
    {
        simulationControlsTitledPane.setContent(simulationControls);
    }

    public void showWorldViewPane(BorderPane worldView)
    {
        worldViewTab.setContent(worldView);
    }

    public void showDailyStatisticsPane(BorderPane dailyStatistics)
    {
        dailyStatsTab.setContent(dailyStatistics);
    }

    public void showProductionStatisticsPane(BorderPane productionStatistics)
    {
        productionStatsTab.setContent(productionStatistics);
    }

    public void showStructureComparisonsPane(BorderPane comparisonsPane)
    {
        structureComparisonsTab.setContent(comparisonsPane);
    }

    public void setMain(Main main)
    {
        this.main = main;

        templateStructureList.setItems(main.getTemplateStructureData());
        worldStructureList.setItems(main.getWorldStructureData());

        main.selectedStructureProperty().addListener((observable, oldValue, newValue) ->
        {
            templateStructureList.getSelectionModel().clearSelection();
            worldStructureList.getSelectionModel().clearSelection();

            if (templateStructureList.getItems().contains(newValue))
            {
                templateStructureList.getSelectionModel().select(newValue);

            }
            else if (worldStructureList.getItems().contains(newValue))
            {
                worldStructureList.getSelectionModel().select(newValue);
            }
        });

        main.simulationStateProperty().addListener((observable, oldValue, newValue) ->
        {
            switch (newValue)
            {
                case RUNNING:
                {
                    worldEditButton.setDisable(true);
                    worldAddButton.setDisable(true);
                    worldRemoveButton.setDisable(true);
                    worldCreateButton.setDisable(true);
                    templateEditButton.setDisable(true);
                    templateAddButton.setDisable(true);
                    templateRemoveButton.setDisable(true);
                    templateCreateButton.setDisable(true);
                } break;
                case RESET:
                case FINISHED:
                {
                    worldEditButton.setDisable(false);
                    worldAddButton.setDisable(false);
                    worldRemoveButton.setDisable(false);
                    worldCreateButton.setDisable(false);
                    templateEditButton.setDisable(false);
                    templateAddButton.setDisable(false);
                    templateRemoveButton.setDisable(false);
                    templateCreateButton.setDisable(false);
                } break;
            }
        });
    }
}
