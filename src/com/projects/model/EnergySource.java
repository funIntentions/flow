package com.projects.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Device that produces electricity.
 */
public class EnergySource extends Device {
    private DoubleProperty ratedDCVoltage;
    private DoubleProperty ratedACVoltage;
    private DoubleProperty current;

    /**
     * Energy Source constructor.
     *
     * @param name           energy source's name
     * @param id             unique identifier for energy source
     * @param ratedACVoltage ratedACVoltage
     * @param ratedDCVoltage ratedDCVoltage
     * @param current        current
     */
    public EnergySource(String name, int id, double ratedACVoltage, double ratedDCVoltage, double current) {
        super(name, id);

        this.ratedACVoltage = new SimpleDoubleProperty(ratedACVoltage);
        this.ratedDCVoltage = new SimpleDoubleProperty(ratedDCVoltage);
        this.current = new SimpleDoubleProperty(current);
    }

    public double getRatedDCVoltage() {
        return ratedDCVoltage.get();
    }

    public void setRatedDCVoltage(double ratedDCVoltage) {
        this.ratedDCVoltage.set(ratedDCVoltage);
    }

    public DoubleProperty ratedDCVoltageProperty() {
        return ratedDCVoltage;
    }

    public double getRatedACVoltage() {
        return ratedACVoltage.get();
    }

    public void setRatedACVoltage(double ratedACVoltage) {
        this.ratedACVoltage.set(ratedACVoltage);
    }

    public DoubleProperty ratedACVoltageProperty() {
        return ratedACVoltage;
    }

    public double getCurrent() {
        return current.get();
    }

    public void setCurrent(double current) {
        this.current.set(current);
    }

    public DoubleProperty currentProperty() {
        return current;
    }
}
