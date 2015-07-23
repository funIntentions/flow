package com.projects.gui.panel;

import com.projects.gui.SubscribedView;
import com.projects.management.SystemController;
import com.projects.models.WorldTimer;
import com.projects.systems.simulation.SimulationStatus;
import com.projects.systems.simulation.World;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

/**
 * Created by Dan on 6/25/2015.
 */
public class SimulationInfoPanel extends JPanel implements SubscribedView
{
    private JLabel timeLabel;
    private JLabel updateLabel;
    private JComboBox<WorldTimer.UpdateRate> updateRateOptions;
    private JLabel costLabel;
    private JLabel usageLabel;
    private JLabel emissionsLabel;
    private DecimalFormat decimalFormat;
    private DecimalFormat timeFormat;
    private DatePicker datePickerForEndDate;
    private DatePicker datePickerForStartDate;
    private LocalDate currentDate;
    private SystemController controller;

    public SimulationInfoPanel(final SystemController systemController)
    {
        controller = systemController;
        setLayout(new GridBagLayout());
        decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        timeFormat = new DecimalFormat("00");
        GridBagConstraints constraints = new GridBagConstraints();

        currentDate = LocalDate.now();
        final JFXPanel fxEndDatePanel = new JFXPanel();
        final JFXPanel fxStartDatePanel = new JFXPanel();
        final JPanel simulationTimePanel = new JPanel(new BorderLayout(10, 10));
        simulationTimePanel.setBorder(BorderFactory.createTitledBorder("Simulation Time"));

        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                VBox vBoxStartDate = new VBox(20);
                Scene sceneStartDate = new Scene(vBoxStartDate);
                fxStartDatePanel.setScene(sceneStartDate);

                Label labelStartDate = new Label("Start Date: ");
                GridPane gridPaneStartDate = new GridPane();
                datePickerForStartDate = new DatePicker();
                datePickerForStartDate.setValue(LocalDate.now());
                datePickerForStartDate.setOnAction(event -> {
                    controller.setStartDate(datePickerForStartDate.getValue());
                });
                gridPaneStartDate.add(labelStartDate, 0, 0);
                gridPaneStartDate.add(datePickerForStartDate, 0, 1);
                vBoxStartDate.getChildren().add(gridPaneStartDate);

                VBox vBoxEndDate = new VBox(20);
                Scene sceneEndDate = new Scene(vBoxEndDate);
                fxEndDatePanel.setScene(sceneEndDate);

                javafx.scene.control.Label labelEndDate = new Label("End Date: ");
                GridPane gridPaneEndDate = new GridPane();
                datePickerForEndDate = new DatePicker();
                datePickerForEndDate.setValue(datePickerForStartDate.getValue().plusDays(1));
                datePickerForEndDate.setOnAction(event -> {
                    controller.setEndDate(datePickerForEndDate.getValue());
                });
                gridPaneEndDate.add(labelEndDate, 0,0);
                gridPaneEndDate.add(datePickerForEndDate, 0, 1);
                vBoxEndDate.getChildren().add(gridPaneEndDate);

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
        });

        constraints.weightx = 0.5;
        constraints.weighty = 0.5;
        constraints.insets = new Insets(5,5,5,5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        updateRateOptions = new JComboBox<WorldTimer.UpdateRate>(WorldTimer.UpdateRate.values());
        updateRateOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                systemController.changeSimulationUpdateRate(WorldTimer.UpdateRate.valueOf(updateRateOptions.getSelectedItem().toString()));
            }
        });
        JPanel updateRatePanel = new JPanel(new GridLayout(1, 2));
        updateLabel = new JLabel("Update Rate: ");
        updateRatePanel.add(updateLabel);
        updateRatePanel.add(updateRateOptions);

        constraints.gridx = 1;
        constraints.gridy = 0;
        timeLabel = new JLabel("Time: ");

        constraints.gridx = 1;
        constraints.gridy = 1;
        fxStartDatePanel.setPreferredSize(new Dimension(112, 50));

        fxEndDatePanel.setPreferredSize(new Dimension(112, 50));

        simulationTimePanel.add(timeLabel, BorderLayout.PAGE_START);
        JPanel simulationSpanPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        simulationSpanPanel.add(fxStartDatePanel);
        simulationSpanPanel.add(fxEndDatePanel);
        simulationTimePanel.add(simulationSpanPanel, BorderLayout.CENTER);
        simulationTimePanel.add(updateRatePanel, BorderLayout.PAGE_END);

        constraints.gridheight = 3;
        add(simulationTimePanel, constraints);

        constraints.gridheight = 1;

        constraints.gridx = 0;
        constraints.gridy = 1;
        usageLabel = new JLabel("Usage: ");
        add(usageLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        costLabel = new JLabel("Cost: ");
        add(costLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        emissionsLabel = new JLabel("Emissions: ");
        add(emissionsLabel, constraints);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(World.PC_WORLD_UPDATE) || event.getPropertyName().equals(World.PC_WORLD_RESET))
        {
            SimulationStatus simulationStatus = (SimulationStatus)event.getNewValue();
            WorldTimer worldTimer = simulationStatus.worldTimer;

            currentDate = datePickerForStartDate.getValue().plusDays(worldTimer.getDay());

            timeLabel.setText("Time: " + timeFormat.format(worldTimer.getHourOfDay()) + ":" + timeFormat.format(worldTimer.getMinutesOfHour()) + ":" + timeFormat.format(worldTimer.getSecondsOfMinute())
                    + " Date: " + currentDate.toString());
            usageLabel.setText("Usage: " + decimalFormat.format(simulationStatus.totalUsageInkWh) + " kWh");
            costLabel.setText("Cost: $" + decimalFormat.format(simulationStatus.price));
            emissionsLabel.setText("Emissions: " + decimalFormat.format(simulationStatus.emissions) + "g");
        }
        else if (event.getPropertyName().equals(World.PC_SIMULATION_STARTED)) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    datePickerForStartDate.setDisable(true);
                    datePickerForEndDate.setDisable(true);
                }
            });
            updateRateOptions.setEnabled(false);
        }
        else if (event.getPropertyName().equals(World.PC_WORLD_RESET))
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    datePickerForStartDate.setDisable(false);
                    datePickerForEndDate.setDisable(false);
                }
            });
            updateRateOptions.setEnabled(true);
        }
    }
}
