package com.projects.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.time.LocalTime;

/**
 * Created by Dan on 8/21/2015.
 */
public class UsageTimeSpan extends TimeSpan {
    private DoubleProperty usage;

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
