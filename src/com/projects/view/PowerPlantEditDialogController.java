package com.projects.view;

import com.projects.helper.Utils;
import com.projects.model.AnimatedSprite;
import com.projects.model.PowerPlant;
import com.projects.model.UsageTimeSpan;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.util.HashMap;

/**
 *  Controller for the power plant edit dialog view.
 */
public class PowerPlantEditDialogController {
    @FXML
    private ComboBox<AnimatedSprite> powerPlantSpriteComboBox;

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

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        powerPlantSpriteComboBox.setCellFactory(new Callback<ListView<AnimatedSprite>, ListCell<AnimatedSprite>>() {
            @Override
            public ListCell<AnimatedSprite> call(ListView<AnimatedSprite> p) {
                return new ListCell<AnimatedSprite>() {
                    private ImageView imageView = new ImageView();

                    {
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    }

                    @Override
                    protected void updateItem(AnimatedSprite item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            imageView.setImage(item.getAnimation().getFrames().get(0));
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });

        powerPlantSpriteComboBox.setButtonCell(powerPlantSpriteComboBox.getCellFactory().call(null));
    }

    /**
     * Sets the power plant that is going to have it's properties edited.
     * @param powerPlant power plant to be edited
     */
    public void setPowerPlant(PowerPlant powerPlant) {
        this.powerPlant = powerPlant;
        powerPlantNameField.setText(powerPlant.getName());
        emissionRateField.setText(String.valueOf(powerPlant.getEmissionRate()));
        costField.setText(String.valueOf(powerPlant.getCost()));
        capacityField.setText(String.valueOf(Utils.wattsToKilowatts(powerPlant.getCapacity())));
    }

    /**
     * Provides a collection of potential sprites this power plant can use.
     * @param sprites power plant sprites
     */
    public void setSprites(HashMap<Integer, AnimatedSprite> sprites) {
        powerPlantSpriteComboBox.setItems(FXCollections.observableArrayList(sprites.values()));
        powerPlantSpriteComboBox.getSelectionModel().select(sprites.get(powerPlant.getAnimatedSprite().getId()));
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;

        dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                handleClose();
            }
        });
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleClose() {
        if (isInputValid()) {
            powerPlant.setName(powerPlantNameField.getText());
            powerPlant.setEmissionRate(Double.valueOf(emissionRateField.getText()));
            powerPlant.setCost(Double.valueOf(costField.getText()));
            powerPlant.setCapacity(Utils.kilowattsToWatts(Double.valueOf(capacityField.getText())));

            AnimatedSprite animatedSprite = new AnimatedSprite(powerPlantSpriteComboBox.getValue());
            animatedSprite.setXPosition(powerPlant.getAnimatedSprite().getXPosition());
            animatedSprite.setYPosition(powerPlant.getAnimatedSprite().getYPosition());
            powerPlant.setAnimatedSprite(animatedSprite);

            dialogStage.close();
        }
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (powerPlantNameField.getText() == null || powerPlantNameField.getText().length() == 0) {
            errorMessage += "No valid power plant name!\n";
        }

        if (emissionRateField.getText() == null || emissionRateField.getText().length() == 0) {
            errorMessage += "No valid emission rate!\n";

        } else {
            try {
                Double.parseDouble(emissionRateField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid emission rate (must be an double)!\n";
            }
        }


        if (costField.getText() == null || costField.getText().length() == 0) {
            errorMessage += "No valid cost!\n";

        } else {
            try {
                Double.parseDouble(costField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid cost (must be an double)!\n";
            }
        }


        if (capacityField.getText() == null || capacityField.getText().length() == 0) {
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
