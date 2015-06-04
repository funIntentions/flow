package com.projects.gui;

import java.beans.PropertyChangeEvent;

/**
 * Created by Dan on 5/28/2015.
 */
public interface SubscribedView
{
    public abstract void modelPropertyChange(final PropertyChangeEvent event);
}
