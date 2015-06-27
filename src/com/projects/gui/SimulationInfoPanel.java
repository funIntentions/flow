package com.projects.gui;

import com.projects.management.SystemController;
import com.projects.models.Time;
import com.projects.systems.simulation.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by Dan on 6/25/2015.
 */
public class SimulationInfoPanel extends JPanel implements SubscribedView
{
    private JLabel timeLabel;
    private JComboBox<Time.UpdateRate> updateRateOptions;

    public SimulationInfoPanel(final SystemController systemController)
    {
        setLayout(new GridLayout(2,1));
        timeLabel = new JLabel("Time: ");
        updateRateOptions = new JComboBox<Time.UpdateRate>(Time.UpdateRate.values());
        updateRateOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                systemController.changeSimulationUpdateRate(Time.UpdateRate.valueOf(updateRateOptions.getSelectedItem().toString()));
            }
        });
        add(timeLabel);
        add(updateRateOptions);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(World.PC_WORLD_TIME_UPDATE))
        {
            Time time = (Time)event.getNewValue();
            timeLabel.setText("Time: " + time.getTotalTime() + " Day: " + time.getDay() + " Week: " + time.getWeek() + " Month: " + time.getMonth() + " Year: " + time.getYear());
        }
    }
}
