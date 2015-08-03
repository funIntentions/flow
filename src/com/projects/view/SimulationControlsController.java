package com.projects.view;

import com.projects.Main;
import com.projects.model.WorldTimer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.LocalDate;

/**
 * Created by Dan on 7/31/2015.
 */
public class SimulationControlsController
{
    @FXML
    private Label timeLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private DatePicker datePickerForStartDate;

    @FXML
    private DatePicker datePickerForEndDate;

    @FXML
    private ComboBox<WorldTimer.UpdateRate> updateRate;

    @FXML
    private Button runButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button resetButton;

    private Main main;
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize()
    {
        updateRate.setItems(FXCollections.observableArrayList(WorldTimer.UpdateRate.values()));
        updateRate.setValue(WorldTimer.UpdateRate.DAYS);
        updateRate.setOnAction((event) ->
        {
            main.updateRateProperty().setValue(updateRate.getValue());
        });

        datePickerForStartDate.setValue(LocalDate.now());
        datePickerForStartDate.setOnAction((event) ->
        {
            main.startDateProperty().setValue(datePickerForStartDate.getValue());
        });

        datePickerForEndDate.setValue(datePickerForStartDate.getValue().plusDays(1));
        datePickerForEndDate.setOnAction((event) ->
        {
            main.endDateProperty().setValue(datePickerForEndDate.getValue());
        });

        final Callback<DatePicker, DateCell> startDayCellFactory =
                new Callback<DatePicker, DateCell>()
                {
                    @Override
                    public DateCell call(final DatePicker datePicker)
                    {
                        return new DateCell()
                        {
                            @Override
                            public void updateItem(LocalDate item, boolean empty)
                            {
                                super.updateItem(item, empty);

                                if (item.isBefore(datePickerForStartDate.getValue().plusDays(1)))
                                {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };

        datePickerForEndDate.setDayCellFactory(startDayCellFactory);

        final Callback<DatePicker, DateCell> endDayCellFactory =
                new Callback<DatePicker, DateCell>()
                {
                    @Override
                    public DateCell call(final DatePicker datePicker)
                    {
                        return new DateCell()
                        {
                            @Override
                            public void updateItem(LocalDate item, boolean empty)
                            {
                                super.updateItem(item, empty);

                                if (item.isAfter(datePickerForEndDate.getValue().minusDays(1)))
                                {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };

        datePickerForStartDate.setDayCellFactory(endDayCellFactory);
    }

    @FXML
    private void handleRun()
    {
        main.getWorld().runSimulation();
    }

    @FXML
    private void handlePause()
    {
        main.getWorld().pauseSimulation();
    }

    @FXML
    private void handleReset()
    {
        main.getWorld().resetSimulation();
    }

    public void setMain(Main main)
    {
        this.main = main;

        main.currentDateProperty().addListener((observable, oldValue, newValue) ->
        {
            dateLabel.setText(newValue.toString());
        });

        main.currentTimeProperty().addListener((observable, oldValue, newValue) ->
        {
            timeLabel.setText(newValue.toString());
        });

        main.simulationStateProperty().addListener((observable, oldValue, newValue) ->
        {
            switch (newValue)
            {
                case RUNNING:
                {
                    runButton.setDisable(true);
                    pauseButton.setDisable(false);
                    resetButton.setDisable(false);
                    datePickerForEndDate.setDisable(true);
                    datePickerForStartDate.setDisable(true);
                    updateRate.setDisable(true);
                } break;
                case PAUSED:
                {
                    runButton.setDisable(false);
                } break;
                case RESET:
                {
                    runButton.setDisable(false);
                    pauseButton.setDisable(true);
                    resetButton.setDisable(true);
                    datePickerForEndDate.setDisable(false);
                    datePickerForStartDate.setDisable(false);
                    updateRate.setDisable(false);
                } break;
                case FINISHED:
                {
                    runButton.setDisable(true);
                    pauseButton.setDisable(true);
                } break;
            }
        });
    }
}
