package com.projects.gui;

import com.projects.management.SystemController;
import com.projects.models.WorldTimer;
import com.projects.systems.simulation.SimulationStatus;
import com.projects.systems.simulation.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.math.RoundingMode;
import java.text.DecimalFormat;

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
    SystemController controller;

    public SimulationInfoPanel(final SystemController systemController)
    {
        controller = systemController;
        setLayout(new GridBagLayout());
        decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        timeFormat = new DecimalFormat("00");
        GridBagConstraints constraints = new GridBagConstraints();

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
        JPanel timeLimitPanel = new JPanel(new GridLayout(1,2));
        JLabel timeLimitLabel = new JLabel("Time Limit(Days): ");
        timeLimitPanel.add(timeLimitLabel);

        timeLimitField = new JFormattedTextField();
        timeLimitField.setEditable(true);
        timeLimitField.setPreferredSize(new Dimension(64, 14));
        timeLimitField.setText("1");
        timeLimitPanel.add(timeLimitField);
        add(timeLimitPanel, constraints);

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

            timeLabel.setText("Time: " + timeFormat.format(worldTimer.getHourOfDay()) + ":" + timeFormat.format(worldTimer.getMinutesOfHour()) + ":" + timeFormat.format(worldTimer.getSecondsOfMinute())
                    + " Day: " + worldTimer.getDay()
                    + " Week: " + worldTimer.getWeek()
                    + " Month: " + worldTimer.getMonth()
                    + " Year: " + worldTimer.getYear());
            usageLabel.setText("Usage: " + decimalFormat.format(simulationStatus.totalUsageInkWh) + " kWh");
            costLabel.setText("Cost: $" + decimalFormat.format(simulationStatus.priceOfProduction));
            emissionsLabel.setText("Emissions: " + decimalFormat.format(simulationStatus.emissions) + "g");
        }
        else if (event.getPropertyName().equals(World.PC_SIMULATION_STARTED))
        {
            // TODO: find way to get other listeners to work
            controller.setTimeLimit(Double.valueOf(timeLimitField.getText()) * WorldTimer.SECONDS_IN_DAY);
        }
    }
}
