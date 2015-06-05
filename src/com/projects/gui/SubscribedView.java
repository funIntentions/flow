package com.projects.gui;

import java.beans.PropertyChangeEvent;

/**
 * Created by Dan on 5/28/2015.
 */
public interface SubscribedView
{
    /**
     * Allows the SubscribedView to receive PropertyChangeEvents that it is interested in handling/responding to.
     * The events are likely passed from an implementation of the PropertyChangeListener.
     * @param event a PropertyChangeEvent that the class has an interest in
     */
    public abstract void modelPropertyChange(final PropertyChangeEvent event);
}
