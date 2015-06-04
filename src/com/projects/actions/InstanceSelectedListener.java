package com.projects.actions;

import com.projects.management.SystemController;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Dan on 5/29/2015.
 */
public class InstanceSelectedListener extends MouseAdapter
{
    SystemController controller;

    public InstanceSelectedListener(SystemController control)
    {
        controller = control;
    }

    public void mousePressed(MouseEvent event)
    {
        JTable target = (JTable)event.getSource();
        int row = target.getSelectedRow();
        controller.newInstanceSelected(row);
    }
}
