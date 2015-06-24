package com.projects.systems;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by Dan on 6/19/2015.
 */
public class System
{
    protected PropertyChangeSupport changeSupport;

    public System()
    {
        changeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        changeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        changeSupport.removePropertyChangeListener(l);
    }

    public void postSetupSync()
    {

    }
}
