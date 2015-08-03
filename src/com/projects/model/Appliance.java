package com.projects.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Created by Dan on 6/26/2015.
 */
public class Appliance extends Device
{
    private DoubleProperty standbyConsumption;
    private DoubleProperty usageConsumption;
    private ElectricityUsageSchedule electricityUsageSchedule;

    public Appliance(String name, int id, Double standbyConsumption, Double usageConsumption)
    {
        super(name, id);

        this.standbyConsumption = new SimpleDoubleProperty(standbyConsumption);
        this.usageConsumption = new SimpleDoubleProperty(usageConsumption);
        this.electricityUsageSchedule = new ElectricityUsageSchedule();
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

    public ElectricityUsageSchedule getElectricityUsageSchedule()
    {
        return electricityUsageSchedule;
    }

    public void setElectricityUsageSchedule(ElectricityUsageSchedule electricityUsageSchedule)
    {
        this.electricityUsageSchedule = electricityUsageSchedule;
    }
}
