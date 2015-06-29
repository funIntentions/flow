package com.projects.gui;

import com.projects.management.SystemController;
import com.projects.models.Time;
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
    private JComboBox<Time.UpdateRate> updateRateOptions;
    private JLabel costLabel;
    private JLabel usageLabel;
    private JLabel emissionsLabel;
    private DecimalFormat decimalFormat;
    private DecimalFormat timeFormat;

    public SimulationInfoPanel(final SystemController systemController)
    {
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
        updateRateOptions = new JComboBox<Time.UpdateRate>(Time.UpdateRate.values());
        updateRateOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                systemController.changeSimulationUpdateRate(Time.UpdateRate.valueOf(updateRateOptions.getSelectedItem().toString()));
            }
        });
        add(updateRateOptions, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        timeLabel = new JLabel("Time: ");
        add(timeLabel, constraints);

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
            Time time = simulationStatus.time;

            timeLabel.setText("Time: " + timeFormat.format(time.getHourOfDay()) + ":" + timeFormat.format(time.getMinutesOfHour()) + ":" + timeFormat.format(time.getSecondsOfMinute())
                    + " Day: " + time.getDay()
                    + " Week: " + time.getWeek()
                    + " Month: " + time.getMonth()
                    + " Year: " + time.getYear());
            usageLabel.setText("Usage: " + decimalFormat.format(simulationStatus.totalUsageInkWh) + " kWh");
            costLabel.setText("Cost: $" + decimalFormat.format(simulationStatus.priceOfProduction));
            emissionsLabel.setText("Emissions: " + decimalFormat.format(simulationStatus.emissions) + "g");
        }
    }
}
