package com.projects.management;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by Dan on 6/2/2015.
 */
public class Task
{
    private int id;
    private String name;
    private String description;
    private PropertyChangeSupport changeSupport;
    public static final String PC_TASK_UPDATE = "PC_TASK_UPDATE";

    public Task(int id, String taskName, String taskDesc)
    {
        name = taskName;
        description = taskDesc;
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

    public void setDescription(String desc)
    {
        description = desc;
        changeSupport.firePropertyChange(PC_TASK_UPDATE, null, description);
    }
}
