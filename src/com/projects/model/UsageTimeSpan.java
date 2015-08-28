package com.projects.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.time.LocalTime;

/**
 * TimeSpan that also has a particular electricity usage during it's span.
 */
public class UsageTimeSpan extends TimeSpan {
    private DoubleProperty usage;

    /**
     * UsageTimeSpan constructor.
     * @param usage electricity usage during span
     * @param from beginning of the time span
     * @param to end of the time span
     */
    public UsageTimeSpan(double usage, LocalTime from, LocalTime to) {
        super(from, to);

        this.usage = new SimpleDoubleProperty(usage);
    }

    public double getUsage() {
        return usage.get();
    }

    public void setUsage(double usage) {
        this.usage.set(usage);
    }

    public DoubleProperty usageProperty() {
        return usage;
    }
}
