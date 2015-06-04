package com.projects.actions;

import com.projects.management.SystemController;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Created by Dan on 6/1/2015.
 */
public class ClassSelectedListener implements ListSelectionListener
{
    SystemController controller;

    public ClassSelectedListener(SystemController control)
    {
        controller = control;
    }

    public void valueChanged(ListSelectionEvent event)
    {
        if (!event.getValueIsAdjusting())
        {
            JList list = (JList)event.getSource();
            controller.newClassSelected(list.getSelectedIndex());
        }
    }
}
