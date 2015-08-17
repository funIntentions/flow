package com.projects.view;

import com.projects.Main;
import com.projects.helper.Constants;
import com.projects.helper.ImageType;
import com.projects.helper.StructureUtil;
import com.projects.model.Appliance;
import com.projects.model.PowerPlant;
import com.projects.model.SingleUnitStructure;
import com.projects.model.Structure;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.List;
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

    @FXML
    private void handleWorldRemoveStructure()
    {
        int selectedIndex = worldStructureList.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0)
            worldStructureList.getItems().remove(selectedIndex);
    }

    @FXML
    private void handleTemplateRemoveStructure()
    {
        int selectedIndex = templateStructureList.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0)
            templateStructureList.getItems().remove(selectedIndex);
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
            else //if (selectedStructure instanceof SingleUnitStructure)
            {
                structure = new SingleUnitStructure((SingleUnitStructure)selectedStructure);
            } // TODO: add Composite Unit Structures or modify single unit structures so that they do the same

            worldStructureList.getItems().add(structure);
        }
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
                structure = new SingleUnitStructure((SingleUnitStructure)selectedStructure);
            }

            templateStructureList.getItems().add(structure);
        }
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
                main.showStructureEditDialog((SingleUnitStructure)structure);
            }

            triggerWorldListUpdate(structure); // TODO: find a more elegant way
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(main.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Structure Selected");
            alert.setContentText("Please select a structure in the table.");

            alert.showAndWait();
        }
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
                main.showStructureEditDialog((SingleUnitStructure)structure);
            }

            triggerTemplateListUpdate(structure);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(main.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Structure Selected");
            alert.setContentText("Please select a structure in the table.");

            alert.showAndWait();
        }
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
        int random = new Random().nextInt((int)Math.floor(main.getWorldViewController().getWidth()));

        return random - random % Constants.IMAGE_SIZE;
    }

    private int getRandomStructureYPosition()
    {
        int random = new Random().nextInt((int)Math.floor(main.getWorldViewController().getHeight()));

        return random - random % Constants.IMAGE_SIZE;
    }

    @FXML
    private void handleWorldCreateSingleUnitStructure()
    {
        SingleUnitStructure structure = new SingleUnitStructure("New Single Unit Structure",
                StructureUtil.getNextStructureId(),
                ImageType.HOUSE_IMAGE,
                getRandomStructureXPosition(),
                getRandomStructureYPosition(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());

        main.getWorldStructureData().add(structure);
    }

    @FXML
    private void handleWorldCreatePowerPlantStructure()
    {
        PowerPlant powerPlant = new PowerPlant("New Power Plant",
                StructureUtil.getNextStructureId(),
                ImageType.POWER_PLANT_IMAGE,
                getRandomStructureXPosition(),
                getRandomStructureYPosition(),
                0.0, 0.0, 0.0);

        main.getWorldStructureData().add(powerPlant);
    }

    @FXML
    private void handleTemplateCreateSingleUnitStructure()
    {
        SingleUnitStructure structure = new SingleUnitStructure("New Single Unit Structure",
                StructureUtil.getNextStructureId(),
                ImageType.HOUSE_IMAGE,
                getRandomStructureXPosition(),
                getRandomStructureYPosition(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());

        main.getTemplateStructureData().add(structure);
    }

    @FXML
    private void handleTemplateCreatePowerPlantStructure()
    {
        PowerPlant powerPlant = new PowerPlant("New Power Plant",
                StructureUtil.getNextStructureId(),
                ImageType.POWER_PLANT_IMAGE,
                getRandomStructureXPosition(),
                getRandomStructureYPosition(),
                0.0, 0.0, 0.0);

        main.getTemplateStructureData().add(powerPlant);
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
