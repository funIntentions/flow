package com.projects.actions;

import com.projects.management.SystemController;
import com.projects.models.IndividualModel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Dan on 5/28/2015.
 */
public class IndividualSelectedListener extends MouseAdapter
{
    private SystemController controller;

    public IndividualSelectedListener(SystemController control)
    {
        controller = control;
    }

    public void mousePressed(MouseEvent event)
    {
        JTable target = (JTable)event.getSource();
        /*IndividualModel model = ((InstanceTable)target.getModel()).getRow(target.getSelectedRow());
        controller.ontologyIndividualSelected(model.getId(), false);*/
    }
}
