package com.projects.actions;

import com.projects.management.SystemController;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Dan on 5/28/2015.
 */
public class IndividualSelectedListener extends MouseAdapter
{
    SystemController controller;

    public IndividualSelectedListener(SystemController control)
    {
        controller = control;
    }

    public void mousePressed(MouseEvent event)
    {
        JTable target = (JTable)event.getSource();
        int row = target.getSelectedRow();
        controller.newIndividualSelected(row);
    }
}
