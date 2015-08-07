package com.projects.view;

import com.projects.model.PowerPlant;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Dan on 7/30/2015.
 */
public class PowerPlantEditDialogController
{
    @FXML
    private TextField powerPlantNameField;

    @FXML
    private TextField emissionRateField;

    @FXML
    private TextField costField;

    @FXML
    private TextField capacityField;

    private Stage dialogStage = null;
    private PowerPlant powerPlant = null;
    private boolean okClicked = false;

    public void setPowerPlant(PowerPlant powerPlant)
    {
        this.powerPlant = powerPlant;
        powerPlantNameField.setText(powerPlant.getName());
        emissionRateField.setText(String.valueOf(powerPlant.getEmissionRate()));
        costField.setText(String.valueOf(powerPlant.getCost()));
        capacityField.setText(String.valueOf(powerPlant.getCapacity()));
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage)
    {
        this.dialogStage = dialogStage;
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk()
    {
        if (isInputValid())
        {
            powerPlant.setName(powerPlantNameField.getText());
            powerPlant.setEmissionRate(Double.valueOf(emissionRateField.getText()));
            powerPlant.setCost(Double.valueOf(costField.getText()));
            powerPlant.setCapacity(Double.valueOf(capacityField.getText()));

            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    private boolean isInputValid()
    {
        String errorMessage = "";

        if (powerPlantNameField.getText() == null || powerPlantNameField.getText().length() == 0)
        {
            errorMessage += "No valid power plant name!\n";
        }

        if (emissionRateField.getText() == null || emissionRateField.getText().length() == 0)
        {
            errorMessage += "No valid emission rate!\n";

        } else {
            try {
                Double.parseDouble(emissionRateField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid emission rate (must be an double)!\n";
            }
        }


        if (costField.getText() == null || costField.getText().length() == 0)
        {
            errorMessage += "No valid cost!\n";

        } else {
            try {
                Double.parseDouble(costField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid cost (must be an double)!\n";
            }
        }


        if (capacityField.getText() == null || capacityField.getText().length() == 0)
        {
            errorMessage += "No valid capacity!\n";
        } else {
            try {
                Double.parseDouble(capacityField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid capacity (must be an double)!\n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}