package com.projects.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Created by Dan on 6/26/2015.
 */
public class Appliance extends Device
{
    private DoubleProperty standbyConsumption;
    private DoubleProperty usageConsumption;
    private ObservableList<TimeSpan> activeTimeSpans;

    public Appliance(String name, int id, Double standbyConsumption, Double usageConsumption, List<TimeSpan> activeTimeSpans)
    {
        super(name, id);

        this.standbyConsumption = new SimpleDoubleProperty(standbyConsumption);
        this.usageConsumption = new SimpleDoubleProperty(usageConsumption);
        this.activeTimeSpans = FXCollections.observableArrayList(activeTimeSpans);
    }

    public boolean isOnAtTime(long time)
    {
        for (TimeSpan deviceUsage : activeTimeSpans)
        {
            if (deviceUsage.getFrom().toSecondOfDay() <= time && deviceUsage.getTo().toSecondOfDay() >= time)
            {
                return true;
            }
        }

        return false;
    }

    public double getStandbyConsumption()
    {
        return standbyConsumption.get();
    }

    public DoubleProperty standbyConsumptionProperty()
    {
        return standbyConsumption;
    }

    public void setStandbyConsumption(double standbyConsumption)
    {
        this.standbyConsumption.set(standbyConsumption);
    }

    public double getUsageConsumption()
    {
        return usageConsumption.get();
    }

    public DoubleProperty usageConsumptionProperty()
    {
        return usageConsumption;
    }

    public void setUsageConsumption(double usageConsumption)
    {
        this.usageConsumption.set(usageConsumption);
    }

    public ObservableList<TimeSpan> getActiveTimeSpans()
    {
        return activeTimeSpans;
    }

    public void setActiveTimeSpans(ObservableList<TimeSpan> activeTimeSpans)
    {
        this.activeTimeSpans = activeTimeSpans;
    }
}
