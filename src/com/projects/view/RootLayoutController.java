package com.projects.view;

import com.projects.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Created by Dan on 7/30/2015.
 */
public class RootLayoutController
{
    private Main main;

    public RootLayoutController()
    {

    }

    public void setMain(Main main)
    {
        this.main = main;
    }

    @FXML
    private void initialize()
    {

    }

    @FXML
    private void handleNew()
    {
        main.reset();
    }

    @FXML
    private void handleOpen()
    {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);

        // Show save file dialog
        File file = fileChooser.showOpenDialog(main.getPrimaryStage());

        if (file != null)
        {
            main.readSimulation(file);
        }
    }

    @FXML
    private void handleSave()
    {
        File simulationFile = main.getSimulationFilePath();

        if (simulationFile != null)
        {
            main.saveSimulation(simulationFile);
        }
        else
        {
            handleSaveAs();
        }
    }

    @FXML
    private void handleSaveAs()
    {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showSaveDialog(main.getPrimaryStage());

        if (file != null)
        {
            if (!file.getPath().endsWith(".xml"))
            {
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
        alert.setTitle("Test");
        alert.setHeaderText("About");
        alert.setContentText("Author: Daniel Russell");

        alert.showAndWait();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleClose()
    {
        System.exit(0);
    }
}
