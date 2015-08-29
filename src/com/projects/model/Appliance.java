package com.projects.model;

import com.projects.helper.DeviceUtil;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * A device that consumes electricity.
 */
public class Appliance extends Device {
    private DoubleProperty standbyConsumption; // watts
    private DoubleProperty usageConsumption; // watts
    private ObservableList<TimeSpan> activeTimeSpans;

    /**
     * Appliance constructor.
     *
     * @param name               appliance's name
     * @param id                 unique device identifier
     * @param standbyConsumption consumption that occurs when the appliance isn't active
     * @param usageConsumption   consumption that occurs when the appliance is active
     * @param activeTimeSpans    times when the appliance will be active
     */
    public Appliance(String name, int id, Double standbyConsumption, Double usageConsumption, List<TimeSpan> activeTimeSpans) {
        super(name, id);

        this.standbyConsumption = new SimpleDoubleProperty(standbyConsumption);
        this.usageConsumption = new SimpleDoubleProperty(usageConsumption);
        this.activeTimeSpans = FXCollections.observableArrayList(activeTimeSpans);
    }


    /**
     * Appliance copy constructor.
     *
     * @param appliance Appliance to be copied
     */
    public Appliance(Appliance appliance) {
        super(appliance.getName(), DeviceUtil.getNextDeviceId());

        this.standbyConsumption = new SimpleDoubleProperty(appliance.getStandbyConsumption());
        this.usageConsumption = new SimpleDoubleProperty(appliance.getUsageConsumption());
        this.activeTimeSpans = FXCollections.observableArrayList();

        for (TimeSpan timeSpan : appliance.getActiveTimeSpans()) {
            this.activeTimeSpans.add(new TimeSpan(timeSpan));
        }
    }

    /**
     * Checks if this appliance is scheduled to be on at this time of this day
     *
     * @param day  day of the week
     * @param time time in minutes
     * @return true if the appliance is active, false otherwise
     */
    public boolean isOn(int day, long time) {
        for (TimeSpan deviceUsage : activeTimeSpans) {
            if (deviceUsage.isActiveForDay(day) &&
                    (deviceUsage.getFrom().toSecondOfDay() <= time &&
                            deviceUsage.getTo().toSecondOfDay() >= time)) {
                return true;
            }
        }

        return false;
    }

    public double getStandbyConsumption() {
        return standbyConsumption.get();
    }

    public void setStandbyConsumption(double standbyConsumption) {
        this.standbyConsumption.set(standbyConsumption);
    }

    public DoubleProperty standbyConsumptionProperty() {
        return standbyConsumption;
    }

    public double getUsageConsumption() {
        return usageConsumption.get();
    }

    public void setUsageConsumption(double usageConsumption) {
        this.usageConsumption.set(usageConsumption);
    }

    public DoubleProperty usageConsumptionProperty() {
        return usageConsumption;
    }

    public ObservableList<TimeSpan> getActiveTimeSpans() {
        return activeTimeSpans;
    }

    public void setActiveTimeSpans(ObservableList<TimeSpan> activeTimeSpans) {
        this.activeTimeSpans = activeTimeSpans;
    }
}
