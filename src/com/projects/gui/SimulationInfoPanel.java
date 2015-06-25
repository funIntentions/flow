package com.projects.gui;

import com.projects.systems.simulation.World;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Created by Dan on 6/25/2015.
 */
public class SimulationInfoPanel extends JPanel implements SubscribedView
{
    private JLabel time;

    public SimulationInfoPanel()
    {
        setLayout(new FlowLayout());
        time = new JLabel("Time: ");
        add(time);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(World.PC_WORLD_TIME_UPDATE))
        {
            double totalTime = (Double)event.getNewValue();
            time.setText("Time: " + String.valueOf(totalTime));
        }
    }
}
