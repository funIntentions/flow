package com.projects.view;

import com.projects.Main;
import com.projects.helper.Constants;
import com.projects.helper.StructureUtil;
import com.projects.model.Building;
import com.projects.model.PowerPlant;
import com.projects.model.Structure;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Random;

/**
 * Controller for the simulation overview view.
 */
public class SimulationOverviewController {
    @FXML
    private VBox leftPane;

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

    /**
     * Selects the currently selected structure in the template list as the currently selected structure in the simulation.
     */
    private void selectTemplateStructure() {
        Structure structure = templateStructureList.getSelectionModel().getSelectedItem();
        if (structure != null) {
            main.selectedTemplateStructureProperty().set(structure);
            main.selectedStructureProperty().set(structure);
        }
    }

    /**
     * Selects the currently selected structure in the world list as the currently selected structure in the simulation.
     */
    private void selectWorldStructure() {
        Structure structure = worldStructureList.getSelectionModel().getSelectedItem();
        if (structure != null) {
            main.selectedWorldStructureProperty().set(structure);
            main.selectedStructureProperty().set(structure);
        }
    }

    /**
     * Prompts the user with a dialog if no template structure is selected.
     */
    private void alertNoTemplateStructureSelected() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(main.getPrimaryStage());
        alert.setTitle("No Selection");
        alert.setHeaderText("No Structure Selected");
        alert.setContentText("Please select a structure from the template structures list.");

        alert.showAndWait();
    }

    /**
     * Prompts the user with a dialog if no world structure is selected.
     */
    private void alertNoWorldStructureSelected() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(main.getPrimaryStage());
        alert.setTitle("No Selection");
        alert.setHeaderText("No Structure Selected");
        alert.setContentText("Please select a structure from the world structures list.");

        alert.showAndWait();
    }

    /**
     * Removes the selected world structure from the world.
     */
    @FXML
    private void handleWorldRemoveStructure() {
        int selectedIndex = worldStructureList.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0) {
            worldStructureList.getItems().remove(selectedIndex);
            main.selectedStructureProperty().set(worldStructureList.getSelectionModel().getSelectedItem());
        } else
            alertNoWorldStructureSelected();
    }

    /**
     * Removes the selected template structure from the templates.
     */
    @FXML
    private void handleTemplateRemoveStructure() {
        int selectedIndex = templateStructureList.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0) {
            templateStructureList.getItems().remove(selectedIndex);
            main.selectedStructureProperty().set(templateStructureList.getSelectionModel().getSelectedItem());
        } else
            alertNoTemplateStructureSelected();
    }

    /**
     * Adds the currently selected template structure to the world.
     */
    @FXML
    private void handleTemplateAddStructure() {
        Structure selectedStructure = templateStructureList.getSelectionModel().getSelectedItem();

        if (selectedStructure != null) {
            Structure structure;

            if (selectedStructure instanceof PowerPlant) {
                structure = new PowerPlant((PowerPlant) selectedStructure);
            } else //if (selectedStructure instanceof Building)
            {
                structure = new Building((Building) selectedStructure);
            } // TODO: add Composite Unit Structures or modify single unit structures so that they do the same

            worldStructureList.getItems().add(structure);
            main.selectedStructureProperty().set(structure);
        } else
            alertNoTemplateStructureSelected();
    }

    /**
     * Adds the currently selected world structure to the templates.
     */
    @FXML
    private void handleWorldAddStructure() {
        Structure selectedStructure = worldStructureList.getSelectionModel().getSelectedItem();

        if (selectedStructure != null) {
            Structure structure;

            if (selectedStructure instanceof PowerPlant) {
                structure = new PowerPlant((PowerPlant) selectedStructure);
            } else {
                structure = new Building((Building) selectedStructure);
            }

            templateStructureList.getItems().add(structure);
            main.selectedStructureProperty().set(structure);
        } else
            alertNoWorldStructureSelected();
    }

    /**
     * Opens an edit dialog for the selected world structure.
     */
    @FXML
    private void handleWorldEditStructure() {
        Structure structure = worldStructureList.getSelectionModel().getSelectedItem();

        if (structure != null) {
            if (structure instanceof PowerPlant) {
                main.showPowerPlantEditDialog((PowerPlant) structure);
            } else {
                main.showBuildingEditDialog((Building) structure);
            }
            triggerWorldListUpdate(structure);
        } else
            alertNoWorldStructureSelected();
    }

    /**
     * Opens an edit dialog for the selected template structure.
     */
    @FXML
    private void handleTemplateEditStructure() {
        Structure structure = templateStructureList.getSelectionModel().getSelectedItem();

        if (structure != null) {
            if (structure instanceof PowerPlant) {
                main.showPowerPlantEditDialog((PowerPlant) structure);
            } else {
                main.showBuildingEditDialog((Building) structure);
            }
            triggerTemplateListUpdate(structure);
        } else
            alertNoTemplateStructureSelected();
    }


    /**
     * Triggers a list changed event for the template list in main.
     * @param structure structure updating list
     */
    private void triggerTemplateListUpdate(Structure structure) {
        int index = templateStructureList.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            main.getTemplateStructureData().set(index, structure);
        }
    }

    /**
     * Triggers a list changed event for the world list in main.
     * @param structure structure updating list
     */
    private void triggerWorldListUpdate(Structure structure) {
        int index = worldStructureList.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            main.getWorldStructureData().set(index, structure);
        }
    }

    /**
     * Gets a random x position between the left of the world and half it's width.
     * @return x position
     */
    private int getRandomStructureXPosition() {
        return new Random().nextInt((int) Math.floor(main.getWorldViewController().getWidth()/2 - Constants.IMAGE_SIZE));
    }

    /**
     * Gets a random y position between the top of the world and half it's height.
     * @return y position
     */
    private int getRandomStructureYPosition() {
        return new Random().nextInt((int) Math.floor(main.getWorldViewController().getHeight()/2 - Constants.IMAGE_SIZE));
    }

    /**
     * Creates a new building in the world.
     */
    @FXML
    private void handleWorldCreateBuildingStructure() {
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

    /**
     * Creates a new power plant in the world.
     */
    @FXML
    private void handleWorldCreatePowerPlantStructure() {
        PowerPlant powerPlant = new PowerPlant("New Power Plant",
                StructureUtil.getNextStructureId(),
                getRandomStructureXPosition(),
                getRandomStructureYPosition(),
                main.getPowerPlantSprites().get(1),
                0.0, 0.0, 0.0);

        main.getWorldStructureData().add(powerPlant);
        main.selectedStructureProperty().set(powerPlant);
    }

    /**
     * Creates a new building in the templates.
     */
    @FXML
    private void handleTemplateCreateBuildingStructure() {
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

    /**
     * Creates a new power plant in the templates.
     */
    @FXML
    private void handleTemplateCreatePowerPlantStructure() {
        PowerPlant powerPlant = new PowerPlant("New Power Plant",
                StructureUtil.getNextStructureId(),
                getRandomStructureXPosition(),
                getRandomStructureYPosition(),
                main.getPowerPlantSprites().get(1),
                0.0, 0.0, 0.0);

        main.getTemplateStructureData().add(powerPlant);
        main.selectedStructureProperty().set(powerPlant);
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        templateStructureList.setOnMouseClicked((event) -> selectTemplateStructure());
        worldStructureList.setOnMouseClicked((event) -> selectWorldStructure());
        SplitPane.setResizableWithParent(leftPane.getParent(), false);
    }

    /**
     * Shows the building details pane in its tab.
     * @param buildingDetails pane
     */
    public void showBuildingDetailsPane(Pane buildingDetails) {
        structureDetailsTab.setContent(buildingDetails);
    }

    /**
     * Shows the simulationsControls pane in its titled pane.
     * @param simulationControls pane
     */
    public void showSimulationControlsPane(Pane simulationControls) {
        simulationControlsTitledPane.setContent(simulationControls);
    }

    /**
     * Shows the world view pane in its tab.
     * @param worldView pane
     */
    public void showWorldViewPane(Pane worldView) {
        worldViewTab.setContent(worldView);
    }

    /**
     * Shows the daily stats pane in its tab.
     * @param dailyStatistics pane
     */
    public void showDailyStatisticsPane(Pane dailyStatistics) {
        dailyStatsTab.setContent(dailyStatistics);
    }

    /**
     * shows teh production stats pane in its tab.
     * @param productionStatistics pane
     */
    public void showProductionStatisticsPane(Pane productionStatistics) {
        productionStatsTab.setContent(productionStatistics);
    }

    /**
     * Provides a reference to main and allows the controller to listen for events it cares about.
     * @param main a reference to main.
     */
    public void setMain(Main main) {
        this.main = main;

        templateStructureList.setItems(main.getTemplateStructureData());
        worldStructureList.setItems(main.getWorldStructureData());

        main.selectedStructureProperty().addListener((observable, oldValue, newValue) ->
        {
            templateStructureList.getSelectionModel().clearSelection();
            worldStructureList.getSelectionModel().clearSelection();

            if (templateStructureList.getItems().contains(newValue)) {
                templateStructureList.getSelectionModel().select(newValue);

            } else if (worldStructureList.getItems().contains(newValue)) {
                worldStructureList.getSelectionModel().select(newValue);
            }
        });

        main.simulationStateProperty().addListener((observable, oldValue, newValue) ->
        {
            switch (newValue) {
                case RUNNING: {
                    worldEditButton.setDisable(true);
                    worldAddButton.setDisable(true);
                    worldRemoveButton.setDisable(true);
                    worldCreateButton.setDisable(true);
                    templateEditButton.setDisable(true);
                    templateAddButton.setDisable(true);
                    templateRemoveButton.setDisable(true);
                    templateCreateButton.setDisable(true);
                }
                break;
                case RESET:
                case FINISHED: {
                    worldEditButton.setDisable(false);
                    worldAddButton.setDisable(false);
                    worldRemoveButton.setDisable(false);
                    worldCreateButton.setDisable(false);
                    templateEditButton.setDisable(false);
                    templateAddButton.setDisable(false);
                    templateRemoveButton.setDisable(false);
                    templateCreateButton.setDisable(false);
                }
                break;
            }
        });
    }
}
