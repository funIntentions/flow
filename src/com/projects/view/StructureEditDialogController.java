package com.projects.view;

import com.projects.Main;
import com.projects.helper.Constants;
import com.projects.helper.DeviceUtil;
import com.projects.helper.Utils;
import com.projects.model.*;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.LocalTimeStringConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controller for Structure Edit Dialog view
 */
public class StructureEditDialogController {

    @FXML
    private ComboBox<AnimatedSprite> structureSpriteComboBox;

    @FXML
    private TextField structureNameField;

    @FXML
    private TabPane deviceTabPane;

    @FXML
    private TabPane devicePropertyTabPane;

    @FXML
    private Tab applianceTab;

    @FXML
    private Tab energyStorageTab;

    @FXML
    private Tab appliancePropertyTab;

    @FXML
    private Tab energyStoragePropertyTab;

    @FXML
    private ListView<Appliance> applianceList;

    @FXML
    private ListView<EnergyStorage> energyStorageList;

    @FXML
    private TableView<TimeSpan> usageTable;

    @FXML
    private TableColumn<TimeSpan, Boolean> mondayColumn;

    @FXML
    private TableColumn<TimeSpan, Boolean> tuesdayColumn;

    @FXML
    private TableColumn<TimeSpan, Boolean> wednesdayColumn;

    @FXML
    private TableColumn<TimeSpan, Boolean> thursdayColumn;

    @FXML
    private TableColumn<TimeSpan, Boolean> fridayColumn;

    @FXML
    private TableColumn<TimeSpan, Boolean> saturdayColumn;

    @FXML
    private TableColumn<TimeSpan, Boolean> sundayColumn;

    @FXML
    private TableColumn<TimeSpan, LocalTime> usageFromColumn;

    @FXML
    private TableColumn<TimeSpan, LocalTime> usageToColumn;

    @FXML
    private TextField applianceNameField;

    @FXML
    private TextField applianceStandbyConsumptionField;

    @FXML
    private TextField applianceUsageConsumptionField;

    @FXML
    private TextField energyStorageNameField;

    @FXML
    private TextField energyStorageChargeDischargeRate;

    @FXML
    private TextField energyStorageCapacity;

    @FXML
    private ComboBox<String> energyStorageStrategyComboBox;

    @FXML
    private Button manuallyEditLoadProfileButton;

    @FXML
    private CheckBox useCustomLoadProfileCheckBox;

    private ObservableList<Appliance> appliances;
    private ObservableList<EnergyStorage> energyStorageDevices;

    private HashMap<String, String> storageStrategies;

    private Stage dialogStage = null;
    private Building structure = null;
    private Main main = null;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        usageTable.setEditable(true);
        appliancePropertyTab.setDisable(false);
        energyStoragePropertyTab.setDisable(true);

        deviceTabPane.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> switchPropertyTab(oldValue));

        applianceList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showApplianceProperties(oldValue, newValue));

        energyStorageList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showEnergyStorageProperties(oldValue, newValue));

        usageFromColumn.setCellValueFactory(cellData -> cellData.getValue().fromProperty());
        usageToColumn.setCellValueFactory(cellData -> cellData.getValue().toProperty());
        mondayColumn.setCellValueFactory(cellData -> cellData.getValue().mondayProperty());
        tuesdayColumn.setCellValueFactory(cellData -> cellData.getValue().tuesdayProperty());
        wednesdayColumn.setCellValueFactory(cellData -> cellData.getValue().wednesdayProperty());
        thursdayColumn.setCellValueFactory(cellData -> cellData.getValue().thursdayProperty());
        fridayColumn.setCellValueFactory(cellData -> cellData.getValue().fridayProperty());
        saturdayColumn.setCellValueFactory(cellData -> cellData.getValue().saturdayProperty());
        sundayColumn.setCellValueFactory(cellData -> cellData.getValue().sundayProperty());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.HOURS_AND_MINUTES_FORMAT);
        LocalTimeStringConverter localTimeStringConverter = new LocalTimeStringConverter(dateTimeFormatter, dateTimeFormatter);

        usageFromColumn.setCellFactory(TextFieldTableCell.<TimeSpan, LocalTime>forTableColumn(localTimeStringConverter));
        usageFromColumn.setOnEditCommit((TableColumn.CellEditEvent<TimeSpan, LocalTime> t) ->
                (t.getTableView().getItems().get(t.getTablePosition().getRow())).setFrom(t.getNewValue()));

        usageToColumn.setCellFactory(TextFieldTableCell.<TimeSpan, LocalTime>forTableColumn(localTimeStringConverter));
        usageToColumn.setOnEditCommit((TableColumn.CellEditEvent<TimeSpan, LocalTime> t) ->
                (t.getTableView().getItems().get(t.getTablePosition().getRow())).setTo(t.getNewValue()));

        mondayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(mondayColumn));
        tuesdayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(tuesdayColumn));
        wednesdayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(wednesdayColumn));
        thursdayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(thursdayColumn));
        fridayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(fridayColumn));
        saturdayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(saturdayColumn));
        sundayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(sundayColumn));

        appliances = FXCollections.observableArrayList(appliance -> new Observable[]{appliance.nameProperty()});
        applianceList.setItems(appliances);

        energyStorageDevices = FXCollections.observableArrayList(energyStorage -> new Observable[]{energyStorage.nameProperty()});
        energyStorageList.setItems(energyStorageDevices);

        structureSpriteComboBox.setCellFactory(new Callback<ListView<AnimatedSprite>, ListCell<AnimatedSprite>>() {
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
        structureSpriteComboBox.setButtonCell(structureSpriteComboBox.getCellFactory().call(null));

        storageStrategies = new HashMap<>();
    }

    public void setStructure(Building structure) {
        this.structure = structure;
        structureNameField.setText(structure.getName());
        appliances.clear();
        appliances.addAll(structure.getAppliances());
        energyStorageDevices.clear();
        energyStorageDevices.addAll(structure.getEnergyStorageDevices());

        useCustomLoadProfileCheckBox.setSelected(structure.isUsingCustomLoadProfile());

        if (structure.isUsingCustomLoadProfile())
            manuallyEditLoadProfileButton.setDisable(false);
        else
            manuallyEditLoadProfileButton.setDisable(true);
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage dialog stage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        dialogStage.setOnCloseRequest(we -> handleClose());
    }

    /**
     * Provides a reference to main and allows the controller to listen for events it cares about.
     *
     * @param main a reference to main.
     */
    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Provides a collection of potential sprites this building can use.
     *
     * @param sprites building sprites
     */
    public void setStructureSprites(HashMap<Integer, AnimatedSprite> sprites) {
        structureSpriteComboBox.setItems(FXCollections.observableArrayList(sprites.values()));
        structureSpriteComboBox.getSelectionModel().select(sprites.get(structure.getAnimatedSprite().getId()));
    }

    /**
     * Provides a collection of storage strategies that could be assigned to storage devices.
     *
     * @param storageStrategies storage strategies
     */
    public void setStorageStrategies(HashMap<String, String> storageStrategies) {
        this.storageStrategies = storageStrategies;
        energyStorageStrategyComboBox.setItems(FXCollections.observableArrayList(storageStrategies.keySet()));
        energyStorageStrategyComboBox.getSelectionModel().select(0);
    }

    /**
     * Displays the selected appliance's properties while trying to save the previously selected appliance's properties.
     *
     * @param lastSelected      previously selected appliance
     * @param applianceSelected newly selected appliance
     */
    private void showApplianceProperties(Appliance lastSelected, Appliance applianceSelected) {
        if (lastSelected != null) {
            updateAppliance(lastSelected);
        }

        if (applianceSelected != null) {
            applianceNameField.setText(applianceSelected.getName());
            applianceStandbyConsumptionField.setText(String.valueOf(applianceSelected.getStandbyConsumption()));
            applianceUsageConsumptionField.setText(String.valueOf(applianceSelected.getUsageConsumption()));
            usageTable.setItems(applianceSelected.getActiveTimeSpans());
        }
    }

    /**
     * Displays the selected energy storage device's properties while trying to apply the previously selected storage device's properties that were being edited.
     *
     * @param lastSelected previously selected storage device
     * @param selected     newly selected storage device
     */
    private void showEnergyStorageProperties(EnergyStorage lastSelected, EnergyStorage selected) {
        if (lastSelected != null) {
            updateEnergyStorage(lastSelected);
        }

        if (selected != null) {
            String strategyName = Utils.getStrategyName(selected.getStorageStrategy());

            energyStorageNameField.setText(selected.getName());
            energyStorageChargeDischargeRate.setText(String.valueOf(Utils.wattsToKilowatts(selected.getTransferCapacity())));
            energyStorageCapacity.setText(String.valueOf(Utils.wattsToKilowatts(selected.getStorageCapacity())));
            energyStorageStrategyComboBox.getSelectionModel().select(strategyName);
        }
    }

    /**
     * Updates the tab's devices, then makes it disabled while displaying the other tab.
     *
     * @param oldValue the previously selected tab
     */
    private void switchPropertyTab(Tab oldValue) {
        Tab previousPropertyTab = devicePropertyTabPane.getSelectionModel().getSelectedItem();
        previousPropertyTab.setDisable(true);

        updateTabsDevices(oldValue);

        int index = deviceTabPane.getSelectionModel().getSelectedIndex();
        devicePropertyTabPane.getSelectionModel().select(index);

        Tab newPropertyTab = devicePropertyTabPane.getSelectionModel().getSelectedItem();
        newPropertyTab.setDisable(false);
    }

    /**
     * Tries to apply the properties being edited in the tab to the device being edited.
     *
     * @param tab the tab
     * @return true if the properties being edited were valid and able to be applied
     */
    private boolean updateTabsDevices(Tab tab) {
        boolean valid = true;

        if (tab == applianceTab) {
            Appliance appliance = applianceList.getSelectionModel().getSelectedItem();
            if (appliance != null)
                valid = updateAppliance(appliance);
        } else if (tab == energyStorageTab) {
            EnergyStorage energyStorage = energyStorageList.getSelectionModel().getSelectedItem();
            if (energyStorage != null)
                valid = updateEnergyStorage(energyStorage);
        }

        return valid;
    }

    /**
     * Checks if the property input fields hold valid values for an appliance.
     *
     * @param previouslySelectedDevice the previously selected appliance
     * @return true if the values are valid
     */
    private boolean updateAppliance(Appliance previouslySelectedDevice) {
        String errorMessage = "";

        if (applianceNameField.getText() == null || applianceNameField.getText().length() == 0) {
            errorMessage += "No valid appliance name!\n";
        } else {
            previouslySelectedDevice.setName(applianceNameField.getText());
        }

        if (applianceUsageConsumptionField.getText() == null || applianceUsageConsumptionField.getText().length() == 0) {
            errorMessage += "No valid usage consumption!\n";
        } else {
            // try to parse the postal code into an int.
            try {
                double usageConsumption = Double.parseDouble(applianceUsageConsumptionField.getText());
                previouslySelectedDevice.setUsageConsumption(usageConsumption);
            } catch (NumberFormatException e) {
                errorMessage += "No valid usage consumption (must be an double)!\n";
            }
        }

        if (applianceStandbyConsumptionField.getText() == null || applianceStandbyConsumptionField.getText().length() == 0) {
            errorMessage += "No valid standby consumption!\n";
        } else {
            // try to parse the postal code into an int.
            try {
                double standbyConsumption = Double.parseDouble(applianceStandbyConsumptionField.getText());
                previouslySelectedDevice.setStandbyConsumption(standbyConsumption);
            } catch (NumberFormatException e) {
                errorMessage += "No valid standby consumption (must be an double)!\n";
            }
        }

        structure.calculateLoadProfile();

        if (errorMessage.length() == 0)
            return true;
        else {
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

    /**
     * Checks if the property input fields hold valid values for an energy storage device.
     *
     * @param energyStorage the previously selected appliance
     * @return true if the values are valid
     */
    private boolean updateEnergyStorage(EnergyStorage energyStorage) {
        String errorMessage = "";

        if (energyStorageNameField.getText() == null || energyStorageNameField.getText().length() == 0) {
            errorMessage += "No valid energy storage name!\n";
        } else {
            energyStorage.setName(energyStorageNameField.getText());
        }

        if (energyStorageChargeDischargeRate.getText() == null || energyStorageChargeDischargeRate.getText().length() == 0) {
            errorMessage += "No valid charging rate!\n";
        } else {
            // try to parse the postal code into an int.
            try {
                double chargingRate = Utils.kilowattsToWatts(Double.parseDouble(energyStorageChargeDischargeRate.getText()));
                energyStorage.setTransferCapacity(chargingRate);
            } catch (NumberFormatException e) {
                errorMessage += "No valid charging rate (must be an double)!\n";
            }
        }

        if (energyStorageCapacity.getText() == null || energyStorageCapacity.getText().length() == 0) {
            errorMessage += "No valid storage capacity!\n";
        } else {
            // try to parse the postal code into an int.
            try {
                double storageCapacity = Utils.kilowattsToWatts(Double.parseDouble(energyStorageCapacity.getText()));
                energyStorage.setStorageCapacity(storageCapacity);
            } catch (NumberFormatException e) {
                errorMessage += "No valid storage capacity (must be an double)!\n";
            }
        }

        energyStorage.setStorageStrategy(storageStrategies.get(energyStorageStrategyComboBox.getSelectionModel().getSelectedItem()));

        if (errorMessage.length() == 0)
            return true;
        else {
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

    /**
     * Shows the load profile edit dialog.
     */
    @FXML
    private void handleManuallyEditLoadProfile() {
        main.showLoadProfileEditDialog(structure);
    }

    /**
     * Toggles the building using its custom manually created load profiles.
     */
    @FXML
    private void handleUseCustomLoadProfile() {
        structure.setUsingCustomLoadProfile(useCustomLoadProfileCheckBox.isSelected());
        structure.calculateLoadProfile();

        if (useCustomLoadProfileCheckBox.isSelected()) {
            manuallyEditLoadProfileButton.setDisable(false);

        } else {
            manuallyEditLoadProfileButton.setDisable(true);
        }
    }

    /**
     * Creates a new appliance.
     */
    @FXML
    private void handleCreateNewAppliance() {
        Appliance appliance = new Appliance("Appliance", DeviceUtil.getNextDeviceId(), 0.0, 0.0, new ArrayList<>());
        appliances.add(appliance);
    }

    /**
     * Creates a new energy storage device.
     */
    @FXML
    private void handleCreateNewEnergyStorageDevice() {
        EnergyStorage energyStorage = new EnergyStorage("Energy Storage", DeviceUtil.getNextDeviceId(), 0.0, 0.0, 0.0, energyStorageStrategyComboBox.getSelectionModel().getSelectedItem());
        energyStorageDevices.add(energyStorage);
    }

    /**
     * Removes the currently selected device.
     */
    @FXML
    private void handleRemoveDevice() {
        Tab selectedTab = deviceTabPane.getSelectionModel().getSelectedItem();

        if (selectedTab == applianceTab) {
            int index = applianceList.getSelectionModel().getSelectedIndex();
            if (index >= 0)
                appliances.remove(index);
        } else if (selectedTab == energyStorageTab && energyStorageList.getItems().size() > 0) {
            int index = energyStorageList.getSelectionModel().getSelectedIndex();
            if (index >= 0)
                energyStorageDevices.remove(index);
        }
    }

    /**
     * Creates a new time span for the selected appliance.
     */
    @FXML
    private void handleCreateTimeSpan() {
        Appliance appliance = applianceList.getSelectionModel().getSelectedItem();

        if (appliance != null) {
            TimeSpan timeSpan = new TimeSpan(LocalTime.ofSecondOfDay(0), LocalTime.ofSecondOfDay(0));
            usageTable.getItems().add(timeSpan);
            structure.calculateLoadProfile();
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Action");
            alert.setHeaderText("Please select an appliance.");
            alert.setContentText("An appliance must be selected when adjusting usage.");

            alert.showAndWait();
        }
    }

    /**
     * Removes the selected time span from the selected appliance.
     */
    @FXML
    private void handleRemoveTimeSpan() {
        int index = usageTable.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            usageTable.getItems().remove(index);
            structure.calculateLoadProfile();
        }
    }

    /**
     * Closes the dialog.
     */
    @FXML
    private void handleClose() {
        if (updateTabsDevices(deviceTabPane.getSelectionModel().getSelectedItem()) && isInputValid()) {
            structure.setName(structureNameField.getText());
            structure.setAppliances(appliances);
            structure.setEnergyStorageDevices(energyStorageDevices);

            AnimatedSprite animatedSprite = new AnimatedSprite(structureSpriteComboBox.getValue());
            animatedSprite.setXPosition(structure.getAnimatedSprite().getXPosition());
            animatedSprite.setYPosition(structure.getAnimatedSprite().getYPosition());
            structure.setAnimatedSprite(animatedSprite);
            structure.calculateLoadProfile();

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

        if (structureNameField.getText() == null || structureNameField.getText().length() == 0) {
            errorMessage += "No valid structure name!\n";
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
