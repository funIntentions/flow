package com.projects.input.actions;

import com.projects.management.SystemController;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Dan on 6/4/2015.
 */
public class EditStructureAction extends AbstractAction
{
    private SystemController controller;

    public EditStructureAction(SystemController control)
    {
        super("Edit Structure", null);
        putValue(SHORT_DESCRIPTION, null);
        putValue(MNEMONIC_KEY, null);
        controller = control;
    }

    public void actionPerformed(ActionEvent event)
    {
        controller.editActiveSelection();
    }
}
