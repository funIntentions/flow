package com.projects.actions;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by Dan on 6/11/2015.
 */
public class TabTogglingListener implements ItemListener
{
    String title;
    JComponent tab;
    JTabbedPane pane;
    public TabTogglingListener(JTabbedPane tabbedPane, String tabTitle, JComponent tabToToggle)
    {
        title = tabTitle;
        tab = tabToToggle;
        pane = tabbedPane;
    }

    public void itemStateChanged(ItemEvent event)
    {
        if (event.getStateChange() == ItemEvent.SELECTED)
        {
            pane.addTab(title, tab);
        }
        else if (event.getStateChange() == ItemEvent.DESELECTED)
        {
            pane.remove(tab);
        }
    }
}
