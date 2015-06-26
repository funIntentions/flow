package com.projects.gui;

import com.projects.models.Time;
import com.projects.systems.simulation.World;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Created by Dan on 6/25/2015.
 */
public class SimulationInfoPanel extends JPanel implements SubscribedView
{
    private JLabel timeLabel;

    public SimulationInfoPanel()
    {
        setLayout(new FlowLayout());
        timeLabel = new JLabel("Time: ");
        add(timeLabel);
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
