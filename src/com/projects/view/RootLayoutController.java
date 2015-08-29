package com.projects.view;

import com.projects.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioMenuItem;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Controller for the root layout view.
 */
public class RootLayoutController {
    @FXML
    private RadioMenuItem showLegendRadioMenuItem;

    private Main main;

    /**
     * RootLayoutController constructor.
     */
    public RootLayoutController() {

    }

    /**
     * Provides a reference to main and allows the controller to listen for events it cares about.
     * @param main a reference to main.
     */
    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        showLegendRadioMenuItem.setSelected(true);
        showLegendRadioMenuItem.setOnAction((e -> main.toggleLegendShowing()));
    }

    /**
     * Resets main, leaving a blank simulation to work from.
     */
    @FXML
    private void handleNew() {
        main.reset();
    }

    /**
     * Lets the user select a simulation file to be opened.
     */
    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);

        // Show save file dialog
        File file = fileChooser.showOpenDialog(main.getPrimaryStage());

        if (file != null) {
            main.reset();
            main.readSimulation(file);
        }
    }

    /**
     * Saves the current simulation to an already existing xml file if it can, if not, it saves it as a new file.
     */
    @FXML
    private void handleSave() {
        File simulationFile = main.getSimulationFilePath();

        if (simulationFile != null) {
            main.saveSimulation(simulationFile);
        } else {
            handleSaveAs();
        }
    }

    /**
     * Saves the simulation as a new xml file.
     */
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showSaveDialog(main.getPrimaryStage());

        if (file != null) {
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }

            main.saveSimulation(file);
        }
    }

    /**
     * Opens an about dialog.
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Hello there,");
        alert.setContentText("Please reference the readme for instructions.");

        alert.showAndWait();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleClose() {
        System.exit(0);
    }
}
