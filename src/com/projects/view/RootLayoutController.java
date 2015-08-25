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
 * Created by Dan on 7/30/2015.
 */
public class RootLayoutController
{
    @FXML
    private RadioMenuItem showLegendRadioMenuItem;

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
        showLegendRadioMenuItem.setSelected(true);
        showLegendRadioMenuItem.setOnAction((new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                main.toggleLegendShowing();
            }
        }));
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
            main.reset();
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
        alert.setTitle("About");
        alert.setHeaderText("Hello there,");
        alert.setContentText("Please reference the readme for instructions.");

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
