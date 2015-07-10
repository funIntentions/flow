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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Created by Dan on 6/25/2015.
 */
public class SimulationInfoPanel extends JPanel implements SubscribedView
{
    private JLabel timeLabel;
    private JComboBox<WorldTimer.UpdateRate> updateRateOptions;
    private JLabel costLabel;
    private JLabel usageLabel;
    private JLabel emissionsLabel;
    private DecimalFormat decimalFormat;
    private DecimalFormat timeFormat;
    private JFormattedTextField timeLimitField;
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
        final JFXPanel fxToDatePanel = new JFXPanel();
        final JFXPanel fxCurrentDatePanel = new JFXPanel();

        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                VBox vBoxEndDate = new VBox(20);
                Scene sceneEndDate = new Scene(vBoxEndDate);
                fxToDatePanel.setScene(sceneEndDate);

                javafx.scene.control.Label labelEndDate = new Label("End Date: ");
                GridPane gridPaneEndDate = new GridPane();
                datePickerForEndDate = new DatePicker();
                datePickerForEndDate.setValue(LocalDate.now());
                gridPaneEndDate.add(labelEndDate, 0,0);
                gridPaneEndDate.add(datePickerForEndDate, 0, 1);
                vBoxEndDate.getChildren().add(gridPaneEndDate);

                VBox vBoxStartDate = new VBox(20);
                Scene sceneStartDate = new Scene(vBoxStartDate);
                fxCurrentDatePanel.setScene(sceneStartDate);

                Label labelStartDate = new Label("Start Date: ");
                GridPane gridPaneStartDate = new GridPane();
                datePickerForStartDate = new DatePicker();
                datePickerForStartDate.setValue(LocalDate.now());
                gridPaneStartDate.add(labelStartDate, 0, 0);
                gridPaneStartDate.add(datePickerForStartDate, 0, 1);
                vBoxStartDate.getChildren().add(gridPaneStartDate);
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
        add(updateRateOptions, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        timeLabel = new JLabel("Time: ");
        add(timeLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        fxCurrentDatePanel.setPreferredSize(new Dimension(112, 64));
        add(fxCurrentDatePanel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        JPanel timeLimitPanel = new JPanel(new GridLayout(1,2));
        JLabel timeLimitLabel = new JLabel("Time Limit(Days): ");
        timeLimitPanel.add(timeLimitLabel);

        timeLimitField = new JFormattedTextField();
        timeLimitField.setEditable(true);
        timeLimitField.setPreferredSize(new Dimension(124, 34));
        timeLimitField.setText("1");

        fxToDatePanel.setPreferredSize(new Dimension(112, 64));
        add(fxToDatePanel, constraints);

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
        if (event.getPropertyName().equals(World.PC_WORLD_UPDATE))
        {
            SimulationStatus simulationStatus = (SimulationStatus)event.getNewValue();
            WorldTimer worldTimer = simulationStatus.worldTimer;

            currentDate = datePickerForStartDate.getValue().plusDays(worldTimer.getDay());

            timeLabel.setText("Time: " + timeFormat.format(worldTimer.getHourOfDay()) + ":" + timeFormat.format(worldTimer.getMinutesOfHour()) + ":" + timeFormat.format(worldTimer.getSecondsOfMinute())
                    + " Date: " + currentDate.toString());
                    /*+ " Day: " + worldTimer.getDay()
                    + " Week: " + worldTimer.getWeek()
                    + " Month: " + worldTimer.getMonth()
                    + " Year: " + worldTimer.getYear());*/
            usageLabel.setText("Usage: " + decimalFormat.format(simulationStatus.totalUsageInkWh) + " kWh");
            costLabel.setText("Cost: $" + decimalFormat.format(simulationStatus.priceOfProduction));
            emissionsLabel.setText("Emissions: " + decimalFormat.format(simulationStatus.emissions) + "g");
        }
        else if (event.getPropertyName().equals(World.PC_SIMULATION_STARTED))
        {
            LocalDate startDate = datePickerForStartDate.getValue();
            LocalDate endDate = datePickerForEndDate.getValue();
            long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate);
            controller.setTimeLimit(numberOfDays * WorldTimer.SECONDS_IN_DAY);
        }
    }
}
